package com.yum.kfc.brand.bjmls1609.api.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.danga.MemCached.MemCachedClient;
import com.google.gson.Gson;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.web.util.WebUtil;
import com.yum.kfc.brand.bjmls1609.api.Bjmls1609Api;
import com.yum.kfc.brand.bjmls1609.pojo.Bjmls1609Attend;
import com.yum.kfc.brand.bjmls1609.pojo.Bjmls1609Open;
import com.yum.kfc.brand.bjmls1609.service.Bjmls1609Service;
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.RestClientUtil;

@Component
@Path("/bjmls1609")
@Scope("singleton")
public class Bjmls1609ApiImpl extends BaseApiImpl implements Bjmls1609Api {
	private static Logger logger = LoggerFactory.getLogger(Bjmls1609ApiImpl.class);
	
	private final String CACHE_KEY_PREFIX = "KFC.BJMLS1609.";
	private final String CACHE_KEY_USERATTEND = CACHE_KEY_PREFIX+"ATTENDUSER-"; 
	private final String CACHE_KEY_USERID = CACHE_KEY_PREFIX+"LOGIN_USERID-";
	private final String CACHE_KEY_USERCOUNT = CACHE_KEY_PREFIX+"ATTENDUSERCOUNT-";
	
	@Value("${campaign.svc.kbs.user}")	private String KBS_GET_USER_URL;
	@Value("${campaign.svc.kbs.attend}")	private String KBS_ATTEND_ACTIVITY_URL;
	
	
	@Autowired 
	private MemCachedClient memcachedClient;
	
	@Autowired
	private Bjmls1609Service service;
	
	@Override
	public Result open(Bjmls1609Open open, HttpServletRequest request) {
		open.setIpAddr(WebUtil.getRealRemoteAddr(request));
		//检查渠道类型是否正确
		if(null == open.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}

		open.setId(newUUID());
		open.setOpenTime(new Date());
		final Bjmls1609Open bmOpen = open;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, bmOpen);
			RabbitMQHelper.publish(Bjmls1609Open.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveOpen(bmOpen);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			service.saveOpen(open);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sid", open.getId());
		Result result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "", data);
		logger.info("====bjmls1609 open ====\n"+JSON.toJSONString(result));
		return result;
	}

	@Override
	public Result attend(Bjmls1609Attend attend, HttpServletRequest request) {
		Object preCallTime = getPreCallTimeStamp();
		int callCount = getCallCount();
		long lastTime = Long.parseLong(preCallTime.toString());
		if (new Date().getTime() - lastTime <= 1000) {
			if (callCount > 5000) {
				return new Result(ApiErrorCode.CALL_TOO_FREQUENTLY, "more than 5000 call times per second");
			} else {
				memcachedClient.incr("CALL_COUNT");
			}
		} else {
			memcachedClient.delete("PRE_CALL_TIME");
			memcachedClient.delete("CALL_COUNT");
		}
		
		// 检查token
		if (null == attend.getToken() || attend.getToken().trim().isEmpty()) {
			return new Result(ApiErrorCode.MISSING_TOKEN, "no access token");
		}
		
		// 检查tags
		if (null == attend.getToken() || attend.getToken().trim().isEmpty()) {
			return new Result(ApiErrorCode.MISSING_TOKEN, "no tags");
		}

		String userId = getUserId(attend.getToken());
		if (userId.isEmpty()) {
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is invalid");
		}

		// 检查用户是否已经参与过
		if(attendAlready(userId)) {
			return new Result(ApiErrorCode.USER_HAVE_DRAW, "User have participated");
		}
		
		//防止多台机器并发刷分
		if(!memcachedClient.add(CACHE_KEY_USERCOUNT+userId, new Date().getTime(), new Date(new Date().getTime()+24L*60*60*1000))){
			return new Result(ApiErrorCode.USER_HAVE_DRAW, "User have participated.");
		}
		
		Boolean success = false;
		String errMsg="", errData="";
		try {
			String resp = requestPointFromKBS(attend, request);
			JSONObject jo = JSONObject.parseObject(resp);
			JSONObject data = jo.getJSONObject("data");
			if(data != null){
				if(data.getString("result").equals("0")){
					success = true;
				}else {
					errMsg = "您已经参加过活动！";
				}
			}else {
				errData = jo.getString("errData") + "-" + jo.getString("errCode");
				errMsg = jo.getString("errMsg");
				memcachedClient.delete(CACHE_KEY_USERCOUNT+userId);
			}
		} catch (Exception e) {
			logger.info("Oops! Something is wrong when requesting K-gold from CRM.{}", e);
			memcachedClient.delete(CACHE_KEY_USERCOUNT+userId);
			return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "general server error, please try it later");
		}
		
		if(success){
			memcachedClient.set(CACHE_KEY_USERATTEND+userId, "1");
			//保存参加成功的用户信息
			final Bjmls1609Attend bmAttend = attend;
			bmAttend.setId(newUUID());
			bmAttend.setUserId(userId);
			bmAttend.setIpAddr(WebUtil.getRealRemoteAddr(request));
			bmAttend.setAttendTime(new Date());
			if (RabbitMQHelper.RABBIT_MQ_ENABLED){
				Message msg = new Message(Message.Type.CREATE, bmAttend);
				RabbitMQHelper.publish(Bjmls1609Attend.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
					@Override
					public void handleFailPublish(Throwable reason, Message msg) {
						logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
						try{
							service.saveAttend(bmAttend);
						}catch(DuplicateKeyException e){
							logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
						}
					}
					
				});
			} else {
				service.saveAttend(bmAttend);
			}
		}
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("success", success);
		data.put("errData", errData);
		
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, errMsg, data);
		logger.info("====A02 bjmls1609 attend ====\n"+ JSON.toJSONString(result));
		return result;
		
	}
	
	@Override
	public Result isAttend(Bjmls1609Attend attend, HttpServletRequest request) {
		String userId = getUserId(attend.getToken());
		if (userId == null || userId.isEmpty()) {
			logger.info("====A03 bjmls1609 isAttend ====token is invalid or internal server error. Token:"+attend.getToken());
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is invalid or internal server error. Token:"+attend.getToken());
		}
		boolean isAttend = attendAlready(userId);
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("isAttend", isAttend);
		
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("====A03 bjmls1609 isAttend ====\n"+ JSON.toJSONString(result));
		return result;
	}
	
	private String requestPointFromKBS(Bjmls1609Attend attend, HttpServletRequest req) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("kbck", req.getHeader("kbck"));
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("token", attend.getToken());
		p.put("activityId", "a00013");
        String requestBody = new Gson().toJson(p);
        @SuppressWarnings({ "rawtypes", "unchecked" })
		HttpEntity request = new HttpEntity(requestBody, headers);
        
		RestTemplate restTemplate = new RestTemplate();
		String sr = "";
		try {
			sr = restTemplate.postForObject(KBS_ATTEND_ACTIVITY_URL, request, String.class);
			logger.info("request award from kbs, param[{}], result[{}]", attend, sr);
		} catch (Exception e) {
			logger.info("failed to call platformId API after login. Exception occurs:{}", e);
		}
		
		return sr;
	}
	
	private int getCallCount(){
		Object retValue = getCache("CALL_COUNT", null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				return 0;
			}
		});
		
		return Integer.parseInt(retValue.toString());
	}
	
	private Object getPreCallTimeStamp(){
		Object retValue = getCache("PRE_CALL_TIME", null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				return new Date().getTime();
			}
		});
		
		return retValue;
	}
	
	private boolean attendAlready(final String userId) {
		Object retValue = getCache(CACHE_KEY_USERATTEND+userId, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				boolean isAttend = service.queryIsUserAttend(userId);
				return isAttend ? 1 : 0;
			}
		});
		return "1".equalsIgnoreCase(String.valueOf(retValue));
	}
	
	private String getUserId(final String token) {
		Object retValue = getCache(CACHE_KEY_USERID+token, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				String userId = getUserIdByToken(token);
				return userId;
			}
		});
		return null == retValue ? "" : retValue.toString();
	}
	
	private static enum CacheOp{
		GET, GETS, ADD, SET, INCR, ADDORINCR
	}
	
	private static interface DataGenerator{
		Object generate();
	}
	
	private Object getCache(String key, Date expiry, CacheOp copSave, DataGenerator dg){
		Object o = memcachedClient.get(key);
		if (o==null && dg!=null){
			o = dg.generate();
			if (o!=null){
				switch (copSave){
				case ADD:
					memcachedClient.add(key, String.valueOf(o), expiry);
					break;
				case SET:
					memcachedClient.set(key,  String.valueOf(o), expiry);
					break;
				default:
					throw new RuntimeException(copSave+" not support in this method");
				}
			}
		}
		return o;
	}
	
	private String getUserIdByToken(String token) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("token", token);
		String str = RestClientUtil.callPostService(KBS_GET_USER_URL, p, String.class);
		logger.info("request award from kbs, param[{}], result[{}]", p, str);
		JSONObject object = JSONObject.parseObject(str).getJSONObject("data");
		
		String userId = null;
		if(object != null){
			userId = object.getString("userId");
		}
		return userId;
	}

}
