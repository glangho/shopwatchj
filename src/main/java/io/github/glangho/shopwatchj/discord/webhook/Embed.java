package io.github.glangho.shopwatchj.discord.webhook;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class Embed implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4182865205784378549L;
	
	public static final int MAX_DESCRIPTION_LENGTH = 2048;

	private int color;
	private Author author;
	private String title;
	private String url;
	private String description;
	private List<Field> fields;
	private Image image;
	private Thumbnail thumbnail;
	private Footer footer;
	private ZonedDateTime timestamp;

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Thumbnail getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Footer getFooter() {
		return footer;
	}

	public void setFooter(Footer footer) {
		this.footer = footer;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
