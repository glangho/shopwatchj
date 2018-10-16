package io.github.glangho.shopwatchj.discord.webhook;

import java.io.Serializable;

public class Image implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7737513596646415598L;

	private String url;

	public Image() {
		super();
	}

	public Image(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
