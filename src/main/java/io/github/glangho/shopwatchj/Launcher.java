package io.github.glangho.shopwatchj;

import java.io.File;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mashape.unirest.http.Unirest;

import io.github.glangho.shopwatchj.config.ListenerConfig;
import io.github.glangho.shopwatchj.config.ShopConfig;
import io.github.glangho.shopwatchj.config.WatchConfig;
import io.github.glangho.shopwatchj.util.WatchUtil;

public class Launcher {

	public static void main(String[] args) throws Exception {
		ObjectMapper configMapper = new ObjectMapper().disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule())
				.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module());

		// load configuration file
		File file = new File(Launcher.class.getClassLoader().getResource("conf/properties.json").getFile());
		WatchConfig watchConfig = configMapper.readValue(file, WatchConfig.class);

		// load shop configurations
		ShopConfig shopConfig = watchConfig.getShop();

		// set HttpClient properties
		String connectTimeout = shopConfig.getParameter("connectTimeout");
		String socketTimeout = shopConfig.getParameter("socketTimeout");
		String retryAttempts = shopConfig.getParameter("retryAttempts");

		WatchUtil.connectTimeout = (connectTimeout == null || connectTimeout.isEmpty()) ? WatchUtil.connectTimeout
				: Integer.parseInt(connectTimeout);

		WatchUtil.socketTimeout = (socketTimeout == null || socketTimeout.isEmpty()) ? WatchUtil.socketTimeout
				: Integer.parseInt(socketTimeout);

		WatchUtil.retryAttempts = (retryAttempts == null || retryAttempts.isEmpty()) ? WatchUtil.retryAttempts
				: Integer.parseInt(retryAttempts);

		// set Watch properties
		Watch watch = new Watch(shopConfig);

		// create and assign listeners
		for (ListenerConfig config : watchConfig.getListeners()) {
			Class<?> clazz = Class.forName(config.getClazz());

			WatchListener listener;

			Map<String, String> parameters = config.getParameters();
			if (parameters == null || parameters.isEmpty()) {
				listener = (WatchListener) clazz.getConstructor().newInstance();
			} else {
				listener = (WatchListener) clazz.getConstructor(ListenerConfig.class).newInstance(config);
			}
			watch.addListener(listener);
		}

		// initialize Unirest client - ObjectMapper must be set first
		Unirest.setObjectMapper(new WatchMapper());

		RequestConfig config = RequestConfig.custom().setConnectTimeout(WatchUtil.connectTimeout)
				.setConnectionRequestTimeout(WatchUtil.connectTimeout).setSocketTimeout(WatchUtil.socketTimeout)
				.setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

		HttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

		Unirest.setHttpClient(httpclient);

		// start watch
		new Thread(watch).start();
	}

}
