package io.github.glangho.shopwatchj;

import java.io.IOException;
import java.lang.annotation.Annotation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mashape.unirest.http.ObjectMapper;

public class WatchMapper implements ObjectMapper {

	private com.fasterxml.jackson.databind.ObjectMapper jsonMapper;
	private com.fasterxml.jackson.databind.ObjectMapper xmlMapper;

	public WatchMapper() {
		jsonMapper = new com.fasterxml.jackson.databind.ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule())
				.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module());

		JacksonXmlModule xmlModule = new JacksonXmlModule();
		xmlModule.setDefaultUseWrapper(false);
		xmlMapper = new XmlMapper(xmlModule).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule())
				.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module());
		;
	}

	public <T> T readValue(String value, Class<T> valueType) {
		try {
			if (valueType.isAnnotationPresent(WatchMapperInfo.class)) {
				Annotation annotation = valueType.getAnnotation(WatchMapperInfo.class);
				WatchMapperInfo mapperInfo = (WatchMapperInfo) annotation;

				switch (mapperInfo.type()) {
				case XML:
					return xmlMapper.readValue(value, valueType);
				case JSON:
					String root = mapperInfo.root();
					if (!root.isEmpty()) {
						return jsonMapper.readerFor(valueType).withRootName(root).readValue(value);
					}

					return jsonMapper.readValue(value, valueType);
				default:
					throw new RuntimeException("Invalid mapper type on class " + valueType.getSimpleName());
				}
			} else {
				throw new RuntimeException("Mapper class missing info for " + valueType.getSimpleName());
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String writeValue(Object value) {
		try {
			Class<? extends Object> clazz = value.getClass();
			if (clazz.isAnnotationPresent(WatchMapperInfo.class)) {
				Annotation annotation = clazz.getAnnotation(WatchMapperInfo.class);
				WatchMapperInfo mapperInfo = (WatchMapperInfo) annotation;

				switch (mapperInfo.type()) {
				case XML:
					return xmlMapper.writeValueAsString(value);
				case JSON:
					return jsonMapper.writeValueAsString(value);
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
