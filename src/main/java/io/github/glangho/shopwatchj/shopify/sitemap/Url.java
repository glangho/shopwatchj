package io.github.glangho.shopwatchj.shopify.sitemap;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Url implements Serializable, Comparable<Url> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1982284126742577105L;

	private String loc;
	private ZonedDateTime lastmod = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.systemDefault());
	private String changefreq;
	@JacksonXmlProperty(namespace = "http://www.google.com/schemas/sitemap-image/1.1")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<Image> image;

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public ZonedDateTime getLastmod() {
		return lastmod;
	}

	public void setLastmod(ZonedDateTime lastmod) {
		this.lastmod = lastmod;
	}

	public String getChangefreq() {
		return changefreq;
	}

	public void setChangefreq(String changefreq) {
		this.changefreq = changefreq;
	}

	public List<Image> getImage() {
		return image;
	}

	public void setImage(List<Image> image) {
		this.image = image;
	}

	@Override
	public int compareTo(Url o) {
		ZonedDateTime t1 = this.lastmod;
		ZonedDateTime t2 = o.lastmod;

		return -t1.toInstant().compareTo(t2.toInstant());
	}

}
