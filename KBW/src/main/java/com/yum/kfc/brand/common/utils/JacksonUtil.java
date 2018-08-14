package com.yum.kfc.brand.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;


public class JacksonUtil {
	
	public static ObjectMapper mapper = new ObjectMapper();

	public static <T> String marshallToString(T _T){
		try {
			return mapper.writeValueAsString(_T);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static <T> T jsonToObject(String json,Class<T> clazz){
		try{
			return mapper.readValue(json, clazz);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
