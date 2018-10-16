package io.github.glangho.shopwatchj.config;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.glangho.shopwatchj.WatchMapperInfo;
import io.github.glangho.shopwatchj.WatchMapperInfo.MapperType;

@WatchMapperInfo(type = MapperType.JSON)
public class ListenerConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8693501391731137164L;

	private String name;
	@JsonProperty("class")
	private String clazz;
	private Map<String, String> parameters;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getParameter(String key) {
		return parameters.get(key);
	}

	public String getParameter(String key, String _default) {
		return (parameters == null || getParameter(key) == null) ? _default : getParameter(key);
	}

	public String putParameter(String key, String value) {
		return parameters.put(key, value);
	}

}
