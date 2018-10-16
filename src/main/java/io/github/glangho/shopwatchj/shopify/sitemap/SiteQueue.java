package io.github.glangho.shopwatchj.shopify.sitemap;

import java.util.PriorityQueue;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import io.github.glangho.shopwatchj.WatchMapperInfo;
import io.github.glangho.shopwatchj.WatchMapperInfo.MapperType;

@JacksonXmlRootElement(namespace = "http://www.sitemaps.org/schemas/sitemap/0.9", localName = "urlset")
@WatchMapperInfo(type = MapperType.XML)
public class SiteQueue extends PriorityQueue<Url> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6032048722332715838L;

}
