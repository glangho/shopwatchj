package io.github.glangho.shopwatchj;

import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mashape.unirest.http.exceptions.UnirestException;

import io.github.glangho.shopwatchj.config.ShopConfig;
import io.github.glangho.shopwatchj.shopify.Product;
import io.github.glangho.shopwatchj.shopify.ProductList;
import io.github.glangho.shopwatchj.shopify.Variant;
import io.github.glangho.shopwatchj.shopify.sitemap.SiteQueue;
import io.github.glangho.shopwatchj.shopify.sitemap.Url;
import io.github.glangho.shopwatchj.util.WatchUtil;

public class Watch implements Runnable {
	private final static Logger LOGGER = Logger.getLogger(Watch.class.getName());

	public static final String CYCLE_TIME_DEFAULT = "30000";
	public static final String STOCK_CYCLE_TIME_DEFAULT = "3600000";
	public static final String SILENT_DEFAULT = "false";

	private final long cycleTime;
	private final long stockCycleTime;

	private final String site;
	private final List<String> siteMaps;

	private ZonedDateTime lastUpdate;
	private ZonedDateTime lastStockUpdate;
	private Map<Product, Product> productMap;
	private Deque<WatchEvent> queue;
	private List<WatchListener> listeners;
	private boolean silent;

	private long lastCycle;
	private long lastStockCycle;

	public Watch(ShopConfig config) {
		lastUpdate = null;
		lastStockUpdate = ZonedDateTime.now();
		productMap = new HashMap<>();
		queue = new ArrayDeque<>();
		listeners = new ArrayList<>();
		lastCycle = 0l;
		lastStockCycle = 0l;

		siteMaps = config.getSiteMaps();
		site = config.getSite();

		silent = Boolean.parseBoolean(config.getParameter("silent", SILENT_DEFAULT));
		cycleTime = Long.parseLong(config.getParameter("cycleTime", CYCLE_TIME_DEFAULT));
		stockCycleTime = Long.parseLong(config.getParameter("stockCycleTime", STOCK_CYCLE_TIME_DEFAULT));
	}

	@Override
	public void run() {
		do {
			long currentCycle = System.currentTimeMillis();

			if (currentCycle - lastCycle >= cycleTime) {
				refresh(currentCycle);
				checkQueue();

				lastCycle = currentCycle;

				long sleepTime = cycleTime - (System.currentTimeMillis() - currentCycle);
				try {
					Thread.sleep((sleepTime > 0) ? sleepTime : 0);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		} while (true);
	}

	private void checkQueue() {
		if (silent) {
			silent = false;
			queue.clear();
		}

		if (!queue.isEmpty()) {
			while (!queue.isEmpty()) {
				notifyListeners(queue.pop());
			}

			// notify listeners we are done
			notifyListeners(null);
		}
	}

	private void refresh(long currentCycle) {
		Queue<Url> urls = new PriorityQueue<>();

		for (String siteMap : siteMaps) {
			String url = "https://" + site + "/" + siteMap;
			Queue<Url> tmpUrls;
			try {
				tmpUrls = WatchUtil.get(SiteQueue.class, url).getBody();
			} catch (UnirestException e) {
				LOGGER.log(Level.WARNING, e.getMessage(), e);

				handleListeners(e);

				return;
			}

			urls.addAll(tmpUrls);
		}

		Url latest = urls.poll();

		ZonedDateTime thisUpdate = latest.getLastmod();

		// check for any changes since last update
		if (lastUpdate == null || thisUpdate.isAfter(lastUpdate)) {
			try {
				refreshProducts(currentCycle);
			} catch (UnirestException e) {
				LOGGER.log(Level.WARNING, e.getMessage(), e);

				handleListeners(e);

				return;
			}

			lastUpdate = thisUpdate;
		}
	}

	private void refreshProducts(long currentCycle) throws UnirestException {
		boolean stockUpdated = false;
		ZonedDateTime latestUpdatedAt = null;

		List<Product> newProducts = new ArrayList<>();
		int page = 1;

		List<Product> next;
		while (!(next = getProductsByPage(page)).isEmpty()) {
			newProducts.addAll(next);
			page++;
		}

		for (Product newProduct : newProducts) {
			Product oldProduct = productMap.get(newProduct);
			Map<Variant, WatchStatus> updates = new HashMap<>();

			if (oldProduct == null) {
				// add new product
				productMap.put(newProduct, newProduct);

				for (Variant newVariant : newProduct.getVariants()) {
					updates.put(newVariant, WatchStatus.IN_STOCK);
				}
			} else {
				List<Variant> newVariants = newProduct.getVariants();
				List<Variant> oldVariants = oldProduct.getVariants();

				for (Variant newVariant : newVariants) {

					// compare new variants with variants found last update
					boolean found = false;
					for (Variant oldVariant : oldVariants) {
						if (newVariant.equals(oldVariant)) {
							ZonedDateTime updatedAt = newVariant.getUpdatedAt();

							if (newVariant.isAvailable() && oldVariant.getInventoryQuantity() <= 0) {
								// back in stock
								updates.put(newVariant, WatchStatus.BACK_IN_STOCK);
							} else if (!newVariant.isAvailable() && oldVariant.getInventoryQuantity() > 0) {
								// out of stock
								updates.put(newVariant, WatchStatus.OUT_OF_STOCK);
							} else if (currentCycle - lastStockCycle >= stockCycleTime) {
								// check if product was been updated
								if (updatedAt.isAfter(lastStockUpdate)) {
									if (newVariant.isAvailable() && oldVariant.getInventoryQuantity() > 0) {
										// stock change
										updates.put(newVariant, WatchStatus.STOCK_CHANGE);

										stockUpdated = true;

										if (latestUpdatedAt == null || updatedAt.isAfter(latestUpdatedAt)) {
											latestUpdatedAt = updatedAt;
										}
									}
								}
							}

							found = true;
							break;
						}
					}

					if (!found) {
						// new variant
						oldVariants.add(newVariant);
						updates.put(newVariant, WatchStatus.IN_STOCK);
					}
				}

				Set<Variant> _oldVariants = new HashSet<>(oldVariants);
				if (_oldVariants.removeAll(newVariants)) {
					for (Variant _oldVariant : _oldVariants) {
						// remove old variants
						updates.put(_oldVariant, WatchStatus.REMOVED);
					}
				}
			}

			if (!updates.isEmpty()) {
				// update the product with inventory quantities
				Product updatedProduct = getProductWithInventory(newProduct);
				productMap.put(updatedProduct, updatedProduct);

				// send event to queue
				queue.offer(new WatchEvent(site, updatedProduct, updates));
			}
		}

		Set<Product> _oldProducts = new HashSet<>(productMap.keySet());
		if (_oldProducts.removeAll(newProducts)) {
			for (Product _oldProduct : _oldProducts) {
				// remove old products
				productMap.remove(_oldProduct);
				Map<Variant, WatchStatus> updates = new HashMap<>();

				for (Variant _oldVariant : _oldProduct.getVariants()) {
					updates.put(_oldVariant, WatchStatus.REMOVED);
				}

				// send event to queue
				queue.offer(new WatchEvent(site, _oldProduct, updates));
			}
		}

		if (stockUpdated) {
			lastStockCycle = currentCycle;
			lastStockUpdate = latestUpdatedAt;
		}
	}

	private ProductList getProductsByPage(int page) throws UnirestException {
		String url = "https://" + site + "/products.json?page=" + page;
		return WatchUtil.get(ProductList.class, url).getBody();
	}

	private Product getProductWithInventory(Product product) throws UnirestException {
		String url = "https://" + site + "/products/" + product.getHandle() + ".json";
		return WatchUtil.get(Product.class, url).getBody();
	}

	public boolean addListener(WatchListener listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(WatchListener listener) {
		return listeners.remove(listener);
	}

	private void notifyListeners(WatchEvent watchEvent) {
		for (WatchListener listener : listeners) {
			listener.listen(watchEvent);
		}
	}

	private void handleListeners(Exception e) {
		for (WatchListener listener : listeners) {
			listener.handle(e);
		}
	}

}
