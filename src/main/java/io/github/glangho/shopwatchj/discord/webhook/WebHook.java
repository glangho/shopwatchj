package io.github.glangho.shopwatchj.discord.webhook;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.glangho.shopwatchj.WatchMapperInfo;
import io.github.glangho.shopwatchj.WatchMapperInfo.MapperType;

@WatchMapperInfo(type = MapperType.JSON)
public class WebHook implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7445645519209783397L;

	public static final int MAX_CONTENT_LENGTH = 2000;

	private String content;
	private String username;
	@JsonProperty("avatar_url")
	private String avatarUrl;
	private boolean tts;
	private String file;
	private List<Embed> embeds;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public boolean isTts() {
		return tts;
	}

	public void setTts(boolean tts) {
		this.tts = tts;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public List<Embed> getEmbeds() {
		return embeds;
	}

	public void setEmbeds(List<Embed> embeds) {
		this.embeds = embeds;
	}

}
