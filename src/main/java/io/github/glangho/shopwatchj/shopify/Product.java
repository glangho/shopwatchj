package io.github.glangho.shopwatchj.shopify;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.glangho.shopwatchj.WatchMapperInfo;
import io.github.glangho.shopwatchj.WatchMapperInfo.MapperType;

@WatchMapperInfo(type = MapperType.JSON, root = "product")
public class Product implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4809730086468042612L;

	private String id;
	private String title;
	private String handle;
	@JsonProperty("published_at")
	private ZonedDateTime publishedAt;
	@JsonProperty("created_at")
	private ZonedDateTime createdAt;
	@JsonProperty("updated_at")
	private ZonedDateTime updatedAt;
	private String vendor;
	private List<Variant> variants;
	private List<Image> images;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public ZonedDateTime getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(ZonedDateTime publishedAt) {
		this.publishedAt = publishedAt;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public List<Variant> getVariants() {
		return variants;
	}

	public void setVariants(List<Variant> variants) {
		this.variants = variants;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
