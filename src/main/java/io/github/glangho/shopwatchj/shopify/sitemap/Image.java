package io.github.glangho.shopwatchj.shopify.sitemap;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Image implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JacksonXmlProperty(namespace = "http://www.google.com/schemas/sitemap-image/1.1")
	private String loc;
	@JacksonXmlProperty(namespace = "http://www.google.com/schemas/sitemap-image/1.1")
	private String title;

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
