package io.github.glangho.shopwatchj.util;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.github.glangho.shopwatchj.WatchMapperInfo;

public class WatchUtil {
	public static final int CONNECT_TIMEOUT_DEFAULT = 10000;
	public static final int SOCKET_TIMEOUT_DEFAULT = 60000;
	public static final int RETRY_ATTEMPTS_DEFAULT = 5;

	public static int connectTimeout = CONNECT_TIMEOUT_DEFAULT;
	public static int socketTimeout = SOCKET_TIMEOUT_DEFAULT;
	public static int retryAttempts = RETRY_ATTEMPTS_DEFAULT;

	private static ObjectMapper jsonMapper;
	private static ObjectWriter jsonWriter;
	private static ObjectMapper xmlMapper;
	private static ObjectWriter xmlWriter;

	static {
		jsonMapper = new ObjectMapper().registerModule(new JavaTimeModule()).registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		jsonWriter = jsonMapper.writerWithDefaultPrettyPrinter();

		JacksonXmlModule xmlModule = new JacksonXmlModule();
		xmlModule.setDefaultUseWrapper(false);
		xmlMapper = new XmlMapper(xmlModule).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule())
				.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module());

		xmlWriter = xmlMapper.writerWithDefaultPrettyPrinter();
	}

	public static <T> HttpResponse<T> get(Class<T> clazz, String url) {
		Exception e = null;
		for (int i = 0; i < retryAttempts; i++) {
			boolean lastAttempt = (i == retryAttempts - 1) ? true : false;

			try {
				return getMe(clazz, url, lastAttempt);
			} catch (UnirestException e1) {
				e = e1;
			}
		}
		throw new RuntimeException(e);
	}

	private static <T> HttpResponse<T> getMe(Class<T> clazz, String url, boolean last) throws UnirestException {
		return Unirest.get(url).asObject(clazz);
	}

	public static void print(Object obj) {
		try {
			Class<? extends Object> clazz = obj.getClass();
			if (clazz.isAnnotationPresent(WatchMapperInfo.class)) {
				Annotation annotation = clazz.getAnnotation(WatchMapperInfo.class);
				WatchMapperInfo mapperInfo = (WatchMapperInfo) annotation;

				switch (mapperInfo.type()) {
				case XML:
					System.out.println(xmlWriter.writeValueAsString(obj));
					break;
				case JSON:
					System.out.println(jsonWriter.writeValueAsString(obj));
					break;
				default:
					throw new RuntimeException("Invalid mapper type on class " + clazz.getSimpleName());
				}
			} else {
				throw new RuntimeException("Mapper class missing info for " + clazz.getSimpleName());
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
