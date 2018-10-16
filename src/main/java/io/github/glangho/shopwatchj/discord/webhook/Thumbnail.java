package io.github.glangho.shopwatchj.discord.webhook;

import java.io.Serializable;

public class Thumbnail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5272520322339862898L;

	private String url;

	public Thumbnail() {
		super();
	}

	public Thumbnail(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
