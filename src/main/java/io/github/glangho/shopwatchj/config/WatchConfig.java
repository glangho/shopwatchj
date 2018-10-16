package io.github.glangho.shopwatchj.config;

import java.io.Serializable;
import java.util.List;

import io.github.glangho.shopwatchj.WatchMapperInfo;
import io.github.glangho.shopwatchj.WatchMapperInfo.MapperType;

@WatchMapperInfo(type = MapperType.JSON)
public class WatchConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2896784077240076649L;

	private ShopConfig shop;
	private List<ListenerConfig> listeners;

	public ShopConfig getShop() {
		return shop;
	}

	public void setShop(ShopConfig shop) {
		this.shop = shop;
	}

	public List<ListenerConfig> getListeners() {
		return listeners;
	}

	public void setListeners(List<ListenerConfig> listeners) {
		this.listeners = listeners;
	}

}
