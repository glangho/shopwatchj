package io.github.glangho.shopwatchj;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.github.glangho.shopwatchj.config.ListenerConfig;
import io.github.glangho.shopwatchj.config.ShopConfig;
import io.github.glangho.shopwatchj.config.WatchConfig;

public class Launcher {

	public static void main(String[] args) throws Exception {
		ObjectMapper configMapper = new com.fasterxml.jackson.databind.ObjectMapper()
				.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule())
				.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module());

		File file = new File(Launcher.class.getClassLoader().getResource("conf/properties.json").getFile());

		WatchConfig watchConfig = configMapper.readValue(file, WatchConfig.class);

		ShopConfig first = watchConfig.getShop();
		Watch watch = new Watch(first);

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

		new Thread(watch).start();
	}

}
