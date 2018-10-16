package io.github.glangho.shopwatchj.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.github.glangho.shopwatchj.WatchMapperInfo;
import io.github.glangho.shopwatchj.WatchMapperInfo.MapperType;

@WatchMapperInfo(type = MapperType.JSON)
public class ShopConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8002359822300588643L;

	private String site;
	private List<String> siteMaps;
	private Map<String, String> parameters;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public List<String> getSiteMaps() {
		return siteMaps;
	}

	public void setSiteMaps(List<String> siteMaps) {
		this.siteMaps = siteMaps;
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
