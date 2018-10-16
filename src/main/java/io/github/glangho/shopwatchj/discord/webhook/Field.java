package io.github.glangho.shopwatchj.discord.webhook;

import java.io.Serializable;

public class Field implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 281652205754413849L;

	private String name;
	private String value;
	private boolean inline;

	public Field() {
		super();
	}

	public Field(String name, String value, boolean inline) {
		this.name = name;
		this.value = value;
		this.inline = inline;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isInline() {
		return inline;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

}
