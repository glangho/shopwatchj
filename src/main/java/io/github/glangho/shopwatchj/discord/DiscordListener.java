package io.github.glangho.shopwatchj.discord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.github.glangho.shopwatchj.WatchEvent;
import io.github.glangho.shopwatchj.WatchListener;
import io.github.glangho.shopwatchj.WatchStatus;
import io.github.glangho.shopwatchj.config.ListenerConfig;
import io.github.glangho.shopwatchj.discord.webhook.Embed;
import io.github.glangho.shopwatchj.discord.webhook.Field;
import io.github.glangho.shopwatchj.discord.webhook.WebHook;
import io.github.glangho.shopwatchj.shopify.Product;
import io.github.glangho.shopwatchj.shopify.Variant;
import io.github.glangho.shopwatchj.util.WatchUtil;

public class DiscordListener implements WatchListener {
	private final static Logger LOGGER = Logger.getLogger(DiscordListener.class.getName());

	public static final String MAX_SLEEP_DEFAULT = "3000";
	public static final String MAX_EVENTS_DEFAULT = "10";
	public static final String RATE_LIMIT_DEFAULT = "5";
	public static final String MAX_LENGTH_DEFAULT = "25";
	public static final String LOW_STOCK_DEFAULT = "25";
	public static final String TTS_ALERTS_DEFAULT = "false";
	public static final String CUSTOM_ALERT_DEFAULT = "Store has been updated!";
	public static final String ALERT_FLAGS_DEFAULT = "";
	public static final String NOTIFY_ERRORS_DEFAULT = "false";

	public final long maxSleep;
	public final int maxEvents;
	public final int rateLimit;
	public final int maxLength;
	public final int lowStock;

	public final EnumSet<WatchStatus> alertFlags;
	public final boolean ttsAlerts;
	public final String customAlert;
	public final String endpoint;
	public final boolean notifyErrors;

	private int remaining;
	private long reset;
	private boolean alert;

	private List<WatchEvent> events;

	public DiscordListener(ListenerConfig config) {
		maxSleep = Long.parseLong(config.getParameter("maxSleep", MAX_SLEEP_DEFAULT));
		maxEvents = Integer.parseInt(config.getParameter("maxEvents", MAX_EVENTS_DEFAULT));
		rateLimit = Integer.parseInt(config.getParameter("rateLimit", RATE_LIMIT_DEFAULT));
		maxLength = Integer.parseInt(config.getParameter("maxLength", MAX_LENGTH_DEFAULT));
		lowStock = Integer.parseInt(config.getParameter("lowStock", LOW_STOCK_DEFAULT));

		alertFlags = EnumSet.noneOf(WatchStatus.class);
		String[] flags = config.getParameter("alertFlags", ALERT_FLAGS_DEFAULT).split(",");
		for (String flag : flags) {
			alertFlags.add(WatchStatus.valueOf(flag.trim()));
		}

		ttsAlerts = Boolean.parseBoolean(config.getParameter("ttsAlerts", TTS_ALERTS_DEFAULT));
		customAlert = config.getParameter("customAlert", CUSTOM_ALERT_DEFAULT);
		endpoint = config.getParameter("endpoint");
		notifyErrors = Boolean.parseBoolean(config.getParameter("notifyErrors", NOTIFY_ERRORS_DEFAULT));

		remaining = rateLimit;
		reset = System.currentTimeMillis();
		alert = false;

		events = new ArrayList<>();
	}

	@Override
	public void listen(WatchEvent event) {
		if (event != null) {
			events.add(event);
		}

		if (events.size() >= maxEvents || (!events.isEmpty() && event == null)) {
			processEvents();
			events.clear();
		}

		if (alert && event == null) {
			alert();
		}
	}

	private void processEvents() {
		WebHook webHook = new WebHook();
		List<Embed> embeds = new ArrayList<>();

		for (WatchEvent event : events) {
			embeds.add(createEmbed(event));
		}

		webHook.setEmbeds(embeds);

		send(webHook);
	}

	private void alert() {
		alert = false;

		WebHook webHook = new WebHook();
		webHook.setTts(ttsAlerts);
		webHook.setContent(customAlert);
		send(webHook);
	}

	private void send(WebHook webHook) {
		long current = System.currentTimeMillis();

		if (remaining > 0 || current > reset) {

			for (int i = 0; i < WatchUtil.retryAttempts; i++) {
				boolean lastAttempt = (i == WatchUtil.retryAttempts - 1) ? true : false;

				try {
					HttpResponse<WebHook> response = sendMe(webHook);

					int status = response.getStatus();
					if (status == 429) {
						Thread.sleep(maxSleep);
						send(webHook);
					} else if (status / 100 != 2) {
						if (lastAttempt) {
							LOGGER.warning("HTTP " + status + " " + response.getStatusText());
							return;
						} else {
							continue;
						}
					}

					Headers headers = response.getHeaders();

					if (headers.containsKey("X-RateLimit-Remaining")) {
						remaining = Integer.valueOf(headers.getFirst("X-RateLimit-Remaining"));
					}

					if (headers.containsKey("X-RateLimit-Reset")) {
						reset = Long.valueOf(headers.getFirst("X-RateLimit-Reset")) * 1000l;
					}

					return;
				} catch (UnirestException e) {
					if (lastAttempt) {
						LOGGER.log(Level.WARNING, e.getMessage(), e);
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		} else {
			long sleep = reset - current;
			try {
				Thread.sleep((sleep > 0 && sleep < maxSleep) ? sleep : maxSleep);
				send(webHook);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private HttpResponse<WebHook> sendMe(WebHook webHook) throws UnirestException {
		return Unirest.post(endpoint).header("Content-Type", "application/json").body(webHook).asObject(WebHook.class);
	}

	private Embed createEmbed(WatchEvent event) {
		Embed embed = new Embed();

		List<Field> fieldList = new ArrayList<>();

		Product product = event.getProduct();
		String productLink = "https://" + event.getSite() + "/products/" + product.getHandle();

		fieldList.add(new Field(product.getTitle(), productLink, false));

		StringBuilder displaySb = new StringBuilder();
		StringBuilder stockSb = new StringBuilder();

		List<Variant> variantList = product.getVariants();
		for (int i = 0; i < variantList.size(); i++) {
			Variant variant = variantList.get(i);

			int quantity = (variant.getInventoryQuantity() > 0) ? variant.getInventoryQuantity() : 0;

			if (quantity < lowStock && quantity > 0) {
				embed.setColor(15735322);
			}

			stockSb.append(quantity);

			// prefix title with price
			displaySb.append("$" + variant.getPrice() + " - ");

			// limit display to MAX_LENGTH characters per line
			String title = variant.getTitle();

			if (title.length() > maxLength) {
				String[] splitTitle = title.split(" ");
				int count = 0;

				for (int j = 0; j < splitTitle.length; j++) {
					String split = splitTitle[j];

					int length = split.length();
					if ((count + split.length() > maxLength) && splitTitle.length != 1) {
						displaySb.append("\n" + split);

						if (j != (splitTitle.length - 1)) {
							displaySb.append(" ");
						}

						stockSb.append("\n");
						count = length;
					} else {
						displaySb.append(split);

						if (j != (splitTitle.length - 1)) {
							displaySb.append(" ");
						}

						count += length;
					}
				}
			} else {
				displaySb.append(title);
			}

			if (i != variantList.size() - 1) {
				displaySb.append("\n");
				stockSb.append("\n");
			}
		}

		StringBuilder updatesSb = new StringBuilder();
		Map<Variant, WatchStatus> updateMap = event.getUpdates();
		Set<Variant> variantSet = updateMap.keySet();

		Variant[] variants = variantSet.toArray(new Variant[variantSet.size()]);
		for (int i = 0; i < variants.length; i++) {
			Variant variant = variants[i];
			WatchStatus status = updateMap.get(variant);

			switch (status) {
			case STOCK_CHANGE:
				alert = (alertFlags.contains(WatchStatus.STOCK_CHANGE)) ? true : alert;
				updatesSb.append(variant.getTitle() + " changed in stock. \n");
				break;
			case IN_STOCK:
				alert = (alertFlags.contains(WatchStatus.IN_STOCK)) ? true : alert;
				updatesSb.append(variant.getTitle() + " is now in stock. \n");
				break;
			case BACK_IN_STOCK:
				alert = (alertFlags.contains(WatchStatus.BACK_IN_STOCK)) ? true : alert;
				updatesSb.append(variant.getTitle() + " is back in stock. \n");
				break;
			case OUT_OF_STOCK:
				alert = (alertFlags.contains(WatchStatus.OUT_OF_STOCK)) ? true : alert;
				updatesSb.append(variant.getTitle() + " is now out of stock. \n");
				break;
			case REMOVED:
				alert = (alertFlags.contains(WatchStatus.REMOVED)) ? true : alert;
				updatesSb.append(variant.getTitle() + " has been removed. \n");
				break;
			}

			if (i == (variants.length - 1)) {
				updatesSb.append("\u200b");
			}

		}

		fieldList.add(new Field("Variants", displaySb.toString(), true));
		fieldList.add(new Field("Stock", stockSb.toString(), true));
		fieldList.add(new Field("Updates", updatesSb.toString(), false));

		embed.setFields(fieldList);

		embed.setTimestamp(ZonedDateTime.now());

		return embed;
	}

	@Override
	public void handle(Exception e) {
		if (notifyErrors) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String fullStackTrace = sw.toString();

			WebHook error = new WebHook();
			error.setContent(fullStackTrace.substring(0, WebHook.MAX_CONTENT_LENGTH));

			send(error);
		}
	}

}
