package com.yum.kfc.brand.gkslz1606.api.impl;

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
import com.yum.kfc.brand.gkslz1606.api.Gkslz1606Api;
import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Draw;
import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Open;
import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Share;
import com.yum.kfc.brand.gkslz1606.service.Gkslz1606Service;

@Component
@Path("/gkslz1606")
@Scope("singleton")
public class Gkslz1606ApiImpl extends BaseApiImpl implements Gkslz1606Api {
	private static Logger logger = LoggerFactory.getLogger(Gkslz1606ApiImpl.class);
	
	private final String CACHE_KEY_PREFIX = "KFC.GKSLZ1606.";
	private final String CACHE_KEY_USERCOUNT = CACHE_KEY_PREFIX+"DRAWUSERCOUNT-";
	private final String CACHE_KEY_USERDRAW = CACHE_KEY_PREFIX+"WINUSER-";
	
	@Value("${campaign.start}")			public String CAMPAIGN_START;
	@Value("${campaign.end}")			public String CAMPAIGN_END;
	@Value("${campaign.win.rate}")		public double WIN_RATE;
	@Value("${campaign.svc.kbs.win}")	public String KBS_WIN_URL;
	@Value("${campaign.svc.kbs.user}")	public String KBS_GET_USER_URL;
	@Value("${campaign.user.phones}")	public String USER_PHONES;
	
	public static Random random = new Random();
	public static final int RANDOM_MAX = 1000000;
	
	private static final Integer AWARD_TYPE = Integer.valueOf(ApplicationConfig.getProperty("campaign.awardType", "42"));
	
	@Autowired
	private MemCachedClient memcachedClient;
	
	@Autowired
	private Gkslz1606Service service;
	
	@Override
	public Result open(Gkslz1606Parameter parameter, HttpServletRequest request) {
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
		
		final Gkslz1606Open open = new Gkslz1606Open();
		BeanUtils.copyProperties(parameter, open);
		open.setId(newUUID());
		open.setOpenTime(new Date(System.currentTimeMillis()));
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, open);
			RabbitMQHelper.publish(Gkslz1606Open.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		Result result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "", data);
		logger.info("====A01 gkslz1606 open ====\n"+JSON.toJSONString(result));
		return result;
	}

	@Override
	public Result draw(Gkslz1606Parameter parameter, HttpServletRequest request) {
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
		//检查sid
		if(null == parameter.getSid()){
			return new Result(ApiErrorCode.SID_ISNULL, "sid is empty");
		}		
		
		//根据token获取用户信息
		Map<String, Object> userInfo = getUserInfoByToken(parameter.getToken());
		if(userInfo == null){
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is invalid");
		}
		parameter.setPhone(userInfo.get("phone").toString());
		parameter.setUserId(userInfo.get("userId").toString());
		
		if(USER_PHONES.contains(parameter.getPhone().trim())){
			logger.info("===========================测试手机号：{}", parameter.getPhone());
		}else{
			//检查抽奖有效期
			Date now = new Date();
			if (now.before(DateUtil.parseDate(CAMPAIGN_START)) || now.after(DateUtil.parseDate(CAMPAIGN_END))) {
				return new Result(ApiErrorCode.CAMPAIGN_NOT_START, "campaign not start or finished");
			}
		}
				
		//判断是否抽过奖
		if(drawAlready(parameter)) {
			return new Result(ApiErrorCode.USER_HAVE_DRAW, "User have participated in the draw");
		}

		if(!memcachedClient.add(CACHE_KEY_USERCOUNT+parameter.getChannelType()+"-"+parameter.getPhone(), new Date().getTime(), new Date(new Date().getTime()+24L*60*60*1000))){
			return new Result(ApiErrorCode.USER_HAVE_DRAW, "User have participated in the draw.");
		}
		
		memcachedClient.set(CACHE_KEY_USERDRAW+parameter.getChannelType()+"-"+parameter.getPhone(), "1");
		
		double rate = WIN_RATE;
		int luck = random.nextInt(RANDOM_MAX);
		boolean gotWinChance = 0<=luck && luck<=RANDOM_MAX*rate;
		boolean won = false;
		String awardCodeId = null;
		final Gkslz1606Draw draw = new Gkslz1606Draw();
		BeanUtils.copyProperties(parameter, draw);
		draw.setDrawTime(new Date(System.currentTimeMillis()));
		
		try{	
			if (gotWinChance){
				Integer awardType = AWARD_TYPE; //Integer.valueOf(AWARD_OPTIONS.get(parameter.getStoreCode()));
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
			draw.setSid(parameter.getSid());
			//如果中奖了，直接保存到DB，DB失败写到日志，方便人工处理
			if(!won){ //此次活动中奖率100%，故走MQ
				boolean isSuccess = service.saveDraw(draw);
				if(!isSuccess){
					logger.error("win draw save to DB failure, draw data[{}]", JSON.toJSONString(draw));
				}
			}else{	//如果没有中奖，走MQ
				if (RabbitMQHelper.RABBIT_MQ_ENABLED){
					Message msg = new Message(Message.Type.CREATE, draw);
					RabbitMQHelper.publish(Gkslz1606Draw.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("win", won);
		data.put("awardType", won ? draw.getAwardType() : "");
		data.put("awardCodeId", won ? draw.getAwardCodeId() : "");
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("====A02 gkslz1606 draw ====\n"+ JSON.toJSONString(result));
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

	private KBSResultWin requestAwardFromKBS(Gkslz1606Parameter param) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("channelType", param.getChannelType());
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
		
	
	@SuppressWarnings("unused")
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

	@Override
	public Result share(Gkslz1606Parameter parameter, HttpServletRequest request) {
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
		//检查sid
		if (null == parameter.getSid()) {
			return new Result(ApiErrorCode.SID_ISNULL, "sid is empty");
		}
		// 检查分享媒介
		if (StringUtils.isBlank(parameter.getMediaType())) {
			return new Result(ApiErrorCode.MEDIA_TYPE_ISNULL, "Media Type is empty");
		} else if (!MEDIA_TYPE_LIST.contains(parameter.getMediaType())) {
			return new Result(ApiErrorCode.MEDIA_TYPE_INCORRECT, "Media Type is incorrect");
		}
		// 检查分享结果
		if (null == parameter.getShareResult()) {
			return new Result(ApiErrorCode.SHARE_RESULT_ISNULL, "result is empty");
		} else if (!SHARE_RESULT_LIST.contains(parameter.getShareResult())) {
			return new Result(ApiErrorCode.SHARE_RESULT_INCORRECT, "result is incorrect");
		}	
		
		final Gkslz1606Share share = new Gkslz1606Share();
		BeanUtils.copyProperties(parameter, share);
		share.setId(newUUID());
		share.setShareTime(new Date(System.currentTimeMillis()));
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, share);
			RabbitMQHelper.publish(Gkslz1606Share.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveShare(share);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			service.saveShare(share);
		}
		Result result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "", null);
		logger.info("====A03 gkslz1606 share ====\n"+JSON.toJSONString(result));
		return result;
	}
}
