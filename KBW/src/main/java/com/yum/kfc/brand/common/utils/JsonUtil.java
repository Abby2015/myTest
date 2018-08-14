package com.yum.kfc.brand.common.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

public class JsonUtil {
	private static ObjectMapper MAPPER = generateMapper(JsonSerialize.Inclusion.ALWAYS);

	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
		return (T) ((clazz.equals(String.class)) ? json : MAPPER.readValue(json, clazz));
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String json, TypeReference<?> typeReference) throws IOException {
		return (T) ((typeReference.getType().equals(String.class)) ? json : MAPPER.readValue(json, typeReference));
	}

	public static <T> String toJson(T src) {
		String json = null;
		try {
			json = (src instanceof String) ? (String) src : MAPPER.writeValueAsString(src);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static <T> String toJson(T src, JsonSerialize.Inclusion inclusion) throws IOException {
		if (src instanceof String)
			return ((String) src);

		ObjectMapper customMapper = generateMapper(inclusion);
		return customMapper.writeValueAsString(src);
	}

	public static <T> String toJson(T src, ObjectMapper mapper) throws IOException {
		if (mapper != null) {
			if (src instanceof String)
				return ((String) src);

			return mapper.writeValueAsString(src);
		}

		return null;
	}

	public static ObjectMapper mapper() {
		return MAPPER;
	}

	private static ObjectMapper generateMapper(JsonSerialize.Inclusion inclusion) {
		ObjectMapper customMapper = new ObjectMapper();

		customMapper.setSerializationInclusion(inclusion);

		customMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		customMapper.configure(DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

		customMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		return customMapper;
	}

	public static List<?> toListObject(String json, Class<?>[] elementClasses) throws JsonParseException,
			JsonMappingException, IOException {
		JavaType javaType = MAPPER.getTypeFactory().constructParametricType(ArrayList.class, elementClasses);
		@SuppressWarnings("rawtypes")
		List lst = (List) MAPPER.readValue(json, javaType);
		return lst;
	}
}