package io.github.glangho.shopwatchj;

import java.util.HashMap;
import java.util.Map;

import io.github.glangho.shopwatchj.shopify.Product;
import io.github.glangho.shopwatchj.shopify.Variant;

public class WatchEvent {

	private String site;
	private Product product;
	private final Map<Variant, WatchStatus> updates;

	public WatchEvent(String site, Product product, Map<Variant, WatchStatus> updates) {
		this.site = site;
		this.product = product;
		this.updates = updates;
	}

	public WatchEvent(String site, Product product) {
		this(site, product, new HashMap<>());
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Map<Variant, WatchStatus> getUpdates() {
		return updates;
	}

}
