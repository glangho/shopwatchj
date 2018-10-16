package io.github.glangho.shopwatchj.discord.webhook;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Footer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1684744532849350928L;

	private String text;
	@JsonProperty("icon_url")
	private String iconUrl;

	public Footer() {
		super();
	}

	public Footer(String text, String iconUrl) {
		this.text = text;
		this.iconUrl = iconUrl;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

}
