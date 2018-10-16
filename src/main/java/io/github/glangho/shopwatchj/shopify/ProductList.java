package io.github.glangho.shopwatchj.shopify;

import java.util.ArrayList;

import io.github.glangho.shopwatchj.WatchMapperInfo;
import io.github.glangho.shopwatchj.WatchMapperInfo.MapperType;

@WatchMapperInfo(type = MapperType.JSON, root = "products")
public class ProductList extends ArrayList<Product> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5568923058262581123L;

}
