package com.yum.kfc.brand.common.utils;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;


public class RestClientUtil {

	public static <T> T callPostService(String requestUrl, Object param, Class<T> retClass) {
		return callPostService(requestUrl, null, param, retClass);
	}
	
	public static <T> T callPostService(String requestUrl, Map<String, String> custHeaders, Object param, Class<T> retClass) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (custHeaders!=null){
			for (Map.Entry<String, String> e: custHeaders.entrySet()){
				headers.add(e.getKey(), e.getValue());
			}
		}
        String requestBody = new Gson().toJson(param);
        @SuppressWarnings({ "rawtypes", "unchecked" })
		HttpEntity request = new HttpEntity(requestBody, headers);
        
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForObject(requestUrl, request, retClass);
//		String sr = restTemplate.postForObject(requestUrl, request, String.class);
//		return new Gson().fromJson(sr, retClass);
	}
	
	
	public static <T> T callGetService(String requestUrl, Map<String, Object> param, Class<T> retClass) {
		if(null != param && !param.isEmpty()){
			int index = 0;
			String symbol = "";
			for(Map.Entry<String, Object> entry : param.entrySet()) {
				symbol = index == 0 ? "?" : "&";
				requestUrl += symbol + entry.getKey() +"="+ entry.getValue();
				index ++;
		    }  
		}
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(requestUrl, retClass);
	}
	
	public static <T> T callHttpService(String requestUrl, HttpMethod method, Map<String, Object> param, HttpHeaders headers, Class<T> retClass) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//        String requestBody = new Gson().toJson(param);
//        @SuppressWarnings({ "rawtypes", "unchecked" })
//		HttpEntity request = new HttpEntity(requestBody, headers);
//        
//		RestTemplate restTemplate = new RestTemplate();
//		return restTemplate.postForObject(requestUrl, request, retClass);
		RestTemplate restTemplate = new RestTemplate();
		String requestBody = new Gson().toJson(param);
	    @SuppressWarnings({ "rawtypes", "unchecked" })
		HttpEntity entity = new HttpEntity(requestBody, headers);
//		String memberCode = null;
//		String result = null;
//		try {
////			HttpHeaders headers = new HttpHeaders();
////			headers.set("kbck", req.getHeader("kbck"));
//			
//		} catch (Exception e) {
//			log.error("failed to call pri API with token [{}]. Exception occurs:{}", token, StringUtil.getMsgOrClzName(e, true), e);
//			return null;
//		}
		ResponseEntity<T> response = restTemplate.exchange(requestUrl, method, entity, retClass);
		
		return response.getBody();
	}
}
