package io.github.glangho.shopwatchj.discord.webhook;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Author implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5286012154303759116L;

	private String name;
	private String url;
	@JsonProperty("icon_url")
	private String iconUrl;

	public Author() {
		super();
	}

	public Author(String name, String url, String iconUrl) {
		this.name = name;
		this.url = url;
		this.iconUrl = iconUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

}
