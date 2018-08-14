package com.yum.kfc.brand.szz1604.api.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.danga.MemCached.MemCachedClient;
import com.google.gson.Gson;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.web.util.WebUtil;
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.KBSResultWin;
import com.yum.kfc.brand.common.pojo.Parameter;
import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.common.utils.RestClientUtil;
import com.yum.kfc.brand.szz1604.api.Szz1604Api;
import com.yum.kfc.brand.szz1604.pojo.Szz1604Draw;
import com.yum.kfc.brand.szz1604.pojo.Szz1604Open;
import com.yum.kfc.brand.szz1604.service.Szz1604Service;


@Component
@Path("/szz1604/fashionWeek")
@Scope("singleton")
public class Szz1604ApiImpl extends BaseApiImpl implements Szz1604Api {
	
	private static Logger logger = LoggerFactory.getLogger(Szz1604ApiImpl.class);
	
	public static final String ASK_ENC_KEY = "$@_+&*!S";
	public static final String BROWSER_TOKEN_KEY = "@#!%^$&*^";
	public static final int SALT_LENGTH = 6;
	private final String CACHE_KEY_PREFIX = "KFC.SZZ.SZZ1604.";
	private final String CACHE_KEY_USERCOUNT = CACHE_KEY_PREFIX+"DRAWUSERCOUNT-";
	private final String CACHE_KEY_USERDRAW = CACHE_KEY_PREFIX+"WINUSER-";
	
	@Value("${campaign.start}")			public String CAMPAIGN_START;
	@Value("${campaign.end}")			public String CAMPAIGN_END;
	@Value("${campaign.win.rate}")		public double WIN_RATE;
	@Value("${campaign.limit.user}")	public int MAX_USERCOUNT;
	@Value("${campaign.svc.kbs.win}")	public String KBS_WIN_URL;
	@Value("${campaign.svc.kbs.user}")	public String KBS_GET_USER_URL;
	
	public static Random random = new Random();
	public static final int RANDOM_MAX = 1000000;
	
	private static final Map<String, String> AWARD_OPTIONS;	//配置的奖品选项
	
	static{
		try {
			AWARD_OPTIONS = JSON.parseObject(ApplicationConfig.getProperty("szz.awardType.options"), new TypeReference<Map<String, String>>(){}.getType());
		} catch (Exception e) {
			throw new IllegalArgumentException("incorrect configuration items, "+e.getMessage(), e);
		}
	}
	
	@Autowired
	private MemCachedClient memcachedClient;

	@Autowired
	private Szz1604Service service;

	@Override
	public Result open(Szz1604Parameter parameter, HttpServletRequest request) {
		parameter.setIpAddr(WebUtil.getRealRemoteAddr(request));
		//检查渠道类型是否正确
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		//检查设备类型是否正确
		if(null == parameter.getDeviceType()){
			return new Result(ApiErrorCode.DEVICE_TYPE_ISNULL, "Device Type is empty");
		}else if(!DEVICE_TYPE_LIST.contains(parameter.getDeviceType())){
			return new Result(ApiErrorCode.DEVICE_TYPE_INCORRECT, "Device Type is incorrect");
		}
		//检查是否存在设备号
		if(null == parameter.getDeviceId()){
			return new Result(ApiErrorCode.DEVICE_UUID_ISNULL, "DeviceId is empty");
		}
		//检查openId
		if(null == parameter.getOpenId()){
			return new Result(ApiErrorCode.USERID_ISNULL, "openId is empty");
		}
		final Szz1604Open open = new Szz1604Open();
		BeanUtils.copyProperties(parameter, open);
		open.setId(newUUID());
		open.setOpenTime(new Date());
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, open);
			RabbitMQHelper.publish(Szz1604Open.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveOpen(open);
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
		result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "", data);
		logger.info("====A01 open ====\n"+JSON.toJSONString(result));
		return result;
	}
	
	@Override
	public Result draw(Szz1604Parameter parameter, HttpServletRequest request){
		parameter.setIpAddr(WebUtil.getRealRemoteAddr(request));
		//检查渠道类型是否正确
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		//检查设备类型是否正确
		if(null == parameter.getDeviceType()){
			return new Result(ApiErrorCode.DEVICE_TYPE_ISNULL, "Device Type is empty");
		}else if(!DEVICE_TYPE_LIST.contains(parameter.getDeviceType())){
			return new Result(ApiErrorCode.DEVICE_TYPE_INCORRECT, "Device Type is incorrect");
		}
		//检查抽奖有效期
		Date now = new Date(), midNight = DateUtil.getMidNight();
		if (now.before(DateUtil.parseDate(CAMPAIGN_START)) || now.after(DateUtil.parseDate(CAMPAIGN_END))) {
			return new Result(ApiErrorCode.CAMPAIGN_NOT_START, "campaign not start or finished");
		}
		//检查openId
		if(null == parameter.getOpenId()){
			return new Result(ApiErrorCode.USERID_ISNULL, "openId is empty");
		}
		//根据token获取用户信息
		Map<String, Object> userInfo = getUserInfoByToken(parameter.getToken());
		if(userInfo == null){
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is invalid");
		}
		parameter.setPhone(userInfo.get("phone").toString());
		parameter.setUserId(userInfo.get("userId").toString());
		//判断是否抽过奖
		if(drawAlready(parameter)) {
			return new Result(ApiErrorCode.USER_HAVE_DRAW, "User have participated in the draw");
		}

		if(!memcachedClient.add(CACHE_KEY_USERCOUNT+parameter.getChannelType()+"-"+parameter.getPhone(), String.valueOf(parameter.getStoreCode()), new Date(new Date().getTime()+24L*60*60*1000))){
			return new Result(ApiErrorCode.USER_HAVE_DRAW, "User have participated in the draw.");
		}
		
		memcachedClient.set(CACHE_KEY_USERDRAW+parameter.getChannelType()+"-"+parameter.getPhone(), "1");
		
		double rate = WIN_RATE;
		int luck = random.nextInt(RANDOM_MAX);
		boolean gotWinChance = 0<=luck && luck<=RANDOM_MAX*rate;
		boolean won = false;
		String awardCodeId = null;
		final Szz1604Draw draw = new Szz1604Draw();
		BeanUtils.copyProperties(parameter, draw);
		draw.setDrawTime(new Date(System.currentTimeMillis()));
		
		try{	
			if (gotWinChance){
				Integer awardType = Integer.valueOf(AWARD_OPTIONS.get(parameter.getStoreCode()));
				parameter.setAwardType(awardType);
				KBSResultWin result = requestAwardFromKBS(parameter);
				if (result.isSuccess() && result.getData()!=null && result.getData().isWin() && StringUtils.isNotBlank(result.getData().getCode())){
					won = true;
					awardCodeId = result.getData().getCode();
					logger.info("got award from KBS, result is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
				} else {
					logger.info("failed request award from KBS, return is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
				}
			}
		}catch(Exception e){
			logger.info("Oops! Something is wrong when requesting coupon from CRM.{}", e);
			return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "general server error, please try it later");
		}finally{
			draw.setWinAward(won ? 1 : 0);
			if(won){
				draw.setAwardType(parameter.getAwardType());
				draw.setAwardCodeId(awardCodeId);
			}
			draw.setOpenId(parameter.getSid());
			//如果中奖了，直接保存到DB，DB失败写到日志，方便人工处理
			if(won){	
				boolean isSuccess = service.saveDraw(draw);
				if(!isSuccess){
					logger.error("win draw save to DB failure, draw data[{}]", JSON.toJSONString(draw));
				}
			}else{	//如果没有中奖，走MQ
				if (RabbitMQHelper.RABBIT_MQ_ENABLED){
					Message msg = new Message(Message.Type.CREATE, draw);
					RabbitMQHelper.publish(Szz1604Draw.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
						@Override
						public void handleFailPublish(Throwable reason, Message msg) {
							logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
							try{
								service.saveDraw(draw);
							}catch(DuplicateKeyException e){
								logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
							}
						}
						
					});
				} else {
					service.saveDraw(draw);
				}
			}
		}
		
		
		
//		addOrIncr(CACHE_KEY_USERCOUNT+parameter.getChannelType()+"-"+parameter.getUserId(), 1, midNight);
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("win", won);
		data.put("awardType", won ? draw.getAwardType() : "");
		data.put("awardCodeId", won ? draw.getAwardCodeId() : "");
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("====A02 draw ====\n"+ JSON.toJSONString(result));
		return result;
	}
	
	private Map<String, Object> getUserInfoByToken(String token) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("token", token);
		String str = RestClientUtil.callPostService(KBS_GET_USER_URL, p, String.class);
		logger.info("request award from kbs, param[{}], result[{}]", p, str);
		JSONObject object = JSONObject.parseObject(str).getJSONObject("data");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(object == null){
			return null;
		}
		else{
			resultMap.put("phone", object.getString("phone"));
			resultMap.put("userId", object.getString("userId"));
		}
			
		return resultMap;
	}

	private KBSResultWin requestAwardFromKBS(Szz1604Parameter param) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("channelType", param.getChannelType());
		p.put("userId", param.getUserId());
		p.put("storeCode", param.getStoreCode());
		p.put("token", param.getToken());
		p.put("awardType", param.getAwardType());
		p.put("phone", param.getPhone());
		p.put("openId", param.getOpenId());
		String sr = RestClientUtil.callPostService(KBS_WIN_URL, p, String.class);
		logger.info("request award from kbs, param[{}], result[{}]", param, sr);
		KBSResultWin wr = new Gson().fromJson(sr, KBSResultWin.class);
		return wr;
	}

	private boolean drawAlready(final Parameter param){
		Object owf = getCache(CACHE_KEY_USERDRAW+param.getChannelType()+"-"+param.getPhone(), null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				boolean isDraw = service.queryIsUserDraw(param.getPhone());
				return isDraw ? 1 : 0;
			}
		});
		
		return "1".equalsIgnoreCase(String.valueOf(owf));
	}
		
	
	private void addOrIncr(String key, long inc, Date expiry){
		if(!memcachedClient.add(key, inc, expiry)){
			memcachedClient.incr(key);
		}
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
	
	private static enum CacheOp{
		GET, GETS, ADD, SET, INCR, ADDORINCR
	}
	
	private static interface DataGenerator{
		Object generate();
	}

}
