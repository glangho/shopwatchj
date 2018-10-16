package io.github.glangho.shopwatchj.shopify;

import java.util.ArrayList;

import io.github.glangho.shopwatchj.WatchMapperInfo;
import io.github.glangho.shopwatchj.WatchMapperInfo.MapperType;

@WatchMapperInfo(type = MapperType.JSON, root = "collections")
public class CollectionList extends ArrayList<Collection> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8134509863516407129L;

}
