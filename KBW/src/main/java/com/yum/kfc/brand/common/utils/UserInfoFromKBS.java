package com.yum.kfc.brand.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.api.ApiException;

public class UserInfoFromKBS {
	private static final String KBS_GET_USERINFO_FROMOPENID_URL = ApplicationConfig.getProperty("campaign.svc.kbs.user.from.openid");
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getUserInfoFromKBSByOpenId(String openId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("kbck", "kbwcp94okSpg3edw");
		
		long timeStamp = System.currentTimeMillis();
		String signStr = "kbwcp94okSpg3edw\tuwyErYlupEbqDEnr\t"+timeStamp+"\t/user/openid4YUMInternal\topenid="+openId;
		String forSignature = null;
		try {
			forSignature = DigestUtils.md5Hex(signStr.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		headers.set("kbcts", String.valueOf(timeStamp));
		headers.set("kbsv", forSignature);
		
		String response = RestClientUtil.callHttpService(KBS_GET_USERINFO_FROMOPENID_URL+"?openid="+openId, HttpMethod.GET, null, headers, String.class);
		
		JSONObject jo = JSON.parseObject(response);
		Map<String, Object> result = (Map<String, Object>) jo.get("data");
		if(result == null){
			throw new ApiException("Bad request", String.valueOf(jo.get("errCode"))).setContext("failed to get userInfo from KBS, openid["+openId+"], response["+response+"]");
		}
		
		return result;
	}
}
