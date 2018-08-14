package com.yum.kfc.brand.bnbjc1508.api.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import net.sf.json.JSONObject;

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
import com.alibaba.fastjson.TypeReference;
import com.danga.MemCached.MemCachedClient;
import com.google.gson.Gson;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.web.util.WebUtil;
import com.yum.kfc.brand.bnbjc1508.api.Bnbjc1508Api;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Coupon;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Open;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Share;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508User;
import com.yum.kfc.brand.bnbjc1508.service.Bnbjc1508Service;
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.FeiRuiResult;
import com.yum.kfc.brand.common.pojo.Parameter;
import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.common.pojo.TagInfo;
import com.yum.kfc.brand.common.service.AsynJob;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.BrowserUtil;
import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.common.utils.HttpClientUtil;
import com.yum.kfc.brand.common.utils.SaltUtil;

/**
 * @author luolix
 *
 */
@Component
@Path("/bnbjc1508/")
@Scope("singleton")
public class Bnbjc1508ApiImpl extends BaseApiImpl implements Bnbjc1508Api {
	
	private static Logger logger = LoggerFactory.getLogger(Bnbjc1508ApiImpl.class);
	
	public static final String ASK_ENC_KEY = "*&^#@%$!";
	public static final String BROWSER_TOKEN_KEY = "%*/&$-^!";
	public static final int SALT_LENGTH = 6;
	public static final String CACHE_KEY_PREFIX = "KFC.CAMP.BNBJC1508.";
	private static final String CACHE_KEY_TAG = CACHE_KEY_PREFIX+"TAG-";
	private static final String CACHE_KEY_DRAW_COUPON = CACHE_KEY_PREFIX+"DRAWCOUPON-";
	private static final String CACHE_KEY_LAST_COUPON = CACHE_KEY_PREFIX+"LASTCOUPON-";
	private final String CACHE_KEY_NICKNAME = CACHE_KEY_PREFIX+"NICKNAME-";
//	private static final String CACHE_KEY_TODAY_COUPON = CACHE_KEY_PREFIX+"TODAYCOUPON-";
	
	@Value("${campaign.start}")			public String campaignStart;
	@Value("${campaign.end}")			public String campaignEnd;
	@Value("${static.source.root.url}") private String staticSourceUrl;
	@Value("${ws.proxy.use}")			private String proxyUse;
	@Value("${ws.proxy.host}")			private String proxyHost;
	@Value("${ws.proxy.port}")			private String proxyPort;
	
	@Value("${wechat.client_code}")				private String client_code;
	@Value("${wechat.client_secret}")			private String client_secret;
	@Value("${wechat.interface}")				private String interface_;
	@Value("${wechat.action}")					private String action_;
	@Value("${wechat.action2}")					private String action_2;
	@Value("${wechat.home.url}")				private String wechatHomeUrl;
	@Value("${wechat.sendReward.interface}")	private String sendRewardInterface;
	@Value("${wechat.sendReward.action}")		private String sendRewardAction;
	@Value("${wechat.sendReward.eventId}")		private String sendRewardEventId;
	@Value("${wechat.sendReward.itemId}")		private String sendRewardItemId;
	@Value("${wechat.setCardBag.interface}")	private String setCardBagInterface;
	@Value("${wechat.setCardBag.action}")		private String setCardBagAction;
	@Value("${wechat.setCardBag.mainColor}")	private String setCardBagMainColor;
	@Value("${wechat.action.http.url}")			private String actionHttpUrl;
	
	@Autowired
	private MemCachedClient memcachedClient;
	@Autowired
	private Bnbjc1508Service service;
	@Autowired
	private Bnbjc1508TaskJob taskJob;

	@Override
	public Result open(Parameter parameter, HttpServletRequest request) {
		logger.info("\n====open input params: {}", JSON.toJSONString(parameter));
		//检验活动时间
		Date now = new Date();
		if (now.before(DateUtil.parseDate(campaignStart)) || now.after(DateUtil.parseDate(campaignEnd))) {
			logger.info("\n====campaign not start or finished");
			return new Result(ApiErrorCode.CAMPAIGN_NOT_START, "campaign not start or finished");
		}
		//检查用户
		if(null == parameter.getUserId()){
			logger.info("\n====User Id is empty");
			return new Result(ApiErrorCode.USERID_ISNULL, "User Id is empty");
		}
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
		parameter.setIpAddr(WebUtil.getRealRemoteAddr(request));
		parameter.setSid(super.newUUID());
		//从缓存中取出Tag，如果Tag存在，说明用户点击的分享进入的，获得发起者的信息
		TagInfo tagInfo = this.getTagInfo(parameter.getTag());
		if(null != tagInfo){
			parameter.setInviterId(tagInfo.getUserId());
			parameter.setInviteChannelType(tagInfo.getChannelType());
		}
		//判断是否从分享链接进入
		boolean shareCome = false, other = false, drawCoupon = false;
		String promoCode = null;
		if(null != tagInfo){
			//是否从分享链接进入
			shareCome = true;
			//是否是其他人点击分享链接
			other = parameter.getUserId().equals(parameter.getInviterId()) ? false : true;
			//如果是其他用户，则领半价券
			if(other){
				//是否被其他人已领取
				Date midNight = DateUtil.getMidNight();
				drawCoupon = this.isShouldDrawCoupon(parameter, midNight);
				//判断受邀人是否可以中奖
				if(drawCoupon){
					try{
//						promoCode = this.getTodayCoupon(parameter, midNight);
//						//如果今天没有领券
//						if(StringUtils.isBlank(promoCode)){
							//受邀人可以领到半价券
							promoCode = this.callRewardCardService(parameter.getUserId());
							logger.info("\n====get promoCode: {}", promoCode);
							if(StringUtils.isNotBlank(promoCode)){
								this.saveCoupon(promoCode, tagInfo, parameter);	//保存半价券
							}else{
								drawCoupon = false;
							}
//						}else{
//							promoCode = null;//设置为空，表明当天已经领取过了
//						}
					} catch (Exception e) {
						logger.error("call feirui service failure, error describe: []",e.getMessage(),  e);
						return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "call feirui service failure");
					}
				}
			}
		}
		final Bnbjc1508Open open = new Bnbjc1508Open();
		BeanUtils.copyProperties(parameter, open);
		open.setId(parameter.getSid());
		open.setOpenTime(new Date());
		//设置tag
		TagInfo newTagInfo = new TagInfo();
		BeanUtils.copyProperties(open, newTagInfo);
		String tagInfoStr = new Gson().toJson(newTagInfo);
		open.setTagContent(tagInfoStr);
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, open);
			RabbitMQHelper.publish(Bnbjc1508Open.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		if(StringUtils.isBlank(promoCode)){
			promoCode = this.getLastCoupon(parameter);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sid", open.getId());
		data.put("shareCome", shareCome);
		data.put("other", other);
		data.put("drawCoupon", drawCoupon);
		data.put("promoCode", promoCode);
		String tag = SaltUtil.newSalt(SALT_LENGTH)+open.getId();
		//添加tag，放入缓存
		data.put("tag", SaltUtil.encryption(tag, ASK_ENC_KEY));
		//非正常分享的tag
		memcachedClient.set(CACHE_KEY_TAG + open.getId(), tagInfoStr, 0);
		result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("\n====open ouput params: {}", JSON.toJSONString(result));
		return result;
	}	
	
	private void saveCoupon(final String promoCode, final TagInfo tagInfo, final Parameter parameter) throws Exception{
		Date midNight = DateUtil.getMidNight();
		//设置已经被别人领取
		memcachedClient.set(CACHE_KEY_DRAW_COUPON+parameter.getInviteChannelType()+"-"+parameter.getInviterId(),  "0" , midNight);
		//设置最近的半价券
		memcachedClient.set(CACHE_KEY_LAST_COUPON+parameter.getChannelType()+"-"+parameter.getUserId(), promoCode);
		//受邀人领取的半价券
		final Bnbjc1508Coupon inviteCoupon = new Bnbjc1508Coupon();
		BeanUtils.copyProperties(parameter, inviteCoupon);
		inviteCoupon.setId(newUUID());
		inviteCoupon.setOpenId(parameter.getSid());
		inviteCoupon.setPromoCode(promoCode);
		inviteCoupon.setCouponTime(new Date(System.currentTimeMillis()));
		final String inviteName = this.getNickName(parameter.getUserId());
		final String sendName = this.getNickName(parameter.getInviterId());
		//发起人自动获得半价券
		Runnable t = new Runnable() {
			@Override
			public void run() {
				taskJob.setService(service);
				taskJob.setTagInfo(tagInfo);
				//保存受邀人的半价券
				taskJob.saveCoupon(inviteCoupon);
				//受邀人领奖通知
				taskJob.callSendMsgService(parameter.getUserId(), sendName, promoCode, false);
				//发起者领取半价券
				String promoCode2 = taskJob.callRewardCardService(parameter.getInviterId());
				if(StringUtils.isNotBlank(promoCode2)){
					//保存发起者半价券
					taskJob.saveCoupon(promoCode2, parameter.getSid());
					//自动放入卡包
					FeiRuiResult feiRuiResult = taskJob.callCardBagService(promoCode2);
					if(feiRuiResult.getRetcode() == 200 && null != feiRuiResult.getData()){
						//给发起者发送模板消息
						taskJob.callSendMsgService(parameter.getInviterId(), inviteName, promoCode2, true);
					}
				}
			}
		};
		this.asynExecute(new AsynJob(t));
	}
	
	
	private boolean isShouldDrawCoupon(final Parameter param, Date midNight){
		Object clickValue = getCache(CACHE_KEY_DRAW_COUPON+param.getInviteChannelType()+"-"+param.getInviterId(), midNight, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				boolean hasDrawCoupon = service.isDrawCoupon(param.getInviterId(), param.getInviteChannelType());
				return hasDrawCoupon ? "1" : "0";
			}
		});
		return clickValue.toString().equals("1") ? true : false;
	}
	
	
	private String getLastCoupon(final Parameter param){
		Object clickValue = getCache(CACHE_KEY_LAST_COUPON+param.getChannelType()+"-"+param.getUserId(), null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				String promoCode = service.getLastCoupon(param.getUserId(), param.getChannelType());
				return StringUtils.isNotBlank(promoCode) ? promoCode : "";
			}
		});
		return clickValue.toString();
	}
	
//	private String getTodayCoupon(final Parameter param, Date midNight){
//		Object clickValue = getCache(CACHE_KEY_TODAY_COUPON+param.getChannelType()+"-"+param.getUserId(), midNight, CacheOp.ADD, new DataGenerator(){
//			@Override
//			public Object generate() {
//				String promoCode = service.getTodayCoupon(param.getChannelType(), param.getUserId());
//				return StringUtils.isNotBlank(promoCode) ? promoCode : "";
//			}
//		});
//		return clickValue.toString();
//	}
	
	private String callRewardCardService(String userId) throws Exception{
		if(Boolean.parseBoolean(proxyUse)){
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
			logger.info("remote kbs api : using proxy {}:{}", proxyHost, proxyPort);
		}
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", sendRewardInterface);
		getParams.put("action", sendRewardAction);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		postParams.put("open_id", userId);
		postParams.put("event_id", sendRewardEventId);
		postParams.put("item_id", sendRewardItemId);
		String httpUrl = super.packHttpUrl(actionHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", sendRewardAction, httpUrl);
		logger.info("\n===={} post params: {}", sendRewardAction, JSON.toJSONString(postParams));
		String result = HttpClientUtil.sendPostRequestByJava(httpUrl, postParams);
		logger.info("\n===={} return result: {}", sendRewardAction, result);
		JSONObject resultJSON = JSONObject.fromObject(result);
		String promoCode = "";
		if(null != resultJSON.get("retcode") && resultJSON.get("retcode").toString().equals("200")){
			promoCode  = (String)JSONObject.fromObject(resultJSON.get("data")).get("promo_code");
		}
		return promoCode;
	}
	
	
	@Override
	public Result collect(Parameter parameter) {
		//检验活动时间
		logger.info("\n====collect input params: {}", JSON.toJSONString(parameter));
		if(Boolean.parseBoolean(proxyUse)){
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
			logger.info("remote kbs api : using proxy {}:{}", proxyHost, proxyPort);
		}
		//检查抽奖有效期
		Date now = new Date();
		if (now.before(DateUtil.parseDate(campaignStart)) || now.after(DateUtil.parseDate(campaignEnd))) {
			return new Result(ApiErrorCode.CAMPAIGN_NOT_START, "campaign not start or finished");
		}
		Result result =  null;
		FeiRuiResult feiRuiResult;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String promoCode = parameter.getPromoCode();
			if(StringUtils.isNotBlank(promoCode)){
				feiRuiResult = this.callCardBagService(promoCode);
				if(feiRuiResult.getRetcode() == 200 && null != feiRuiResult.getData()){
					data.put("data", feiRuiResult.getData().toString());
				}else{
					logger.error("\n====call setDwBdCardInfo service failure, error describe: ",feiRuiResult.getMsg());
					data.put("data", "");
				}
			}else{
				data.put("data", "");
			}
			result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		} catch (Exception e) {
			logger.error("call setDwBdCardInfo service failure", e);
			result = new Result(ApiErrorCode.GENERAL_SERVER_ERROR, e.getMessage());
		}
		logger.info("\n====collect return value: {}", JSON.toJSONString(result));
		return  result;
	}
	
	
	private FeiRuiResult callCardBagService(String promoCode)  throws Exception{
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", setCardBagInterface);
		getParams.put("action", setCardBagAction);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		postParams.put("code", promoCode);
		postParams.put("redirect_uri", wechatHomeUrl);
		postParams.put("main_color", setCardBagMainColor);
		String httpUrl = super.packHttpUrl(actionHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", setCardBagAction, httpUrl);
		FeiRuiResult feiRuiResult = super.callFeiRuiService(httpUrl, postParams);
		logger.info("\n===={} post params: {}", setCardBagAction, JSON.toJSONString(postParams));
		logger.info("\n===={} return result: {}", setCardBagAction, JSON.toJSONString(feiRuiResult));
		return feiRuiResult;
	}
	
	
	@Override
	public Result share(Parameter parameter) {
		logger.info("\n====share input params: {}", JSON.toJSONString(parameter));
		//检验活动时间
		Date now = new Date();
		if (now.before(DateUtil.parseDate(campaignStart)) || now.after(DateUtil.parseDate(campaignEnd))) {
			logger.info("\n====campaign not start or finished");
			return new Result(ApiErrorCode.CAMPAIGN_NOT_START, "campaign not start or finished");
		}
		//检查用户
		if(null == parameter.getUserId()){
			logger.info("\n====User Id is empty");
			return new Result(ApiErrorCode.USERID_ISNULL, "User Id is empty");
		}
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
		//检查分享媒介
		if(StringUtils.isBlank(parameter.getMediaType())){
			return new Result(ApiErrorCode.MEDIA_TYPE_ISNULL, "Media Type is empty");
		}else if(!MEDIA_TYPE_LIST.contains(parameter.getMediaType())){
			return new Result(ApiErrorCode.MEDIA_TYPE_INCORRECT, "Media Type is incorrect");
		}
		//检查分享结果
		if(null == parameter.getShareResult()){
			return new Result(ApiErrorCode.SHARE_RESULT_ISNULL, "result is empty");
		}else if(!SHARE_RESULT_LIST.contains(parameter.getShareResult())){
			return new Result(ApiErrorCode.SHARE_RESULT_INCORRECT, "result is incorrect");
		}
		final Bnbjc1508Share share = new Bnbjc1508Share();
		BeanUtils.copyProperties(parameter, share);
		share.setId(newUUID());
		share.setOpenId(parameter.getSid());
		share.setShareTime(new Date(System.currentTimeMillis()));
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, share);
			RabbitMQHelper.publish(Bnbjc1508Share.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		//分享后判断是否可以中半价券
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null);
		logger.info("\n====share output params: {}", JSON.toJSONString(result));
		return result;
	}
	
	
	private TagInfo getTagInfo(String tag) {
		TagInfo tagInfo = null;
		if (StringUtils.isBlank(tag))
			return null;
		tag = SaltUtil.decryption(tag, ASK_ENC_KEY);
		final String openId = tag.substring(SALT_LENGTH);
		Object oTagInfo = getCache(CACHE_KEY_TAG + openId, null, CacheOp.SET, new DataGenerator(){
			@Override
			public Object generate(){
				String tagStr = service.getTagContent(openId);
				return tagStr;
			}
		});
		if (oTagInfo==null){
			logger.error("failed to get TagInfo for openId[{}], {}", openId);
			return null;
		}else{
			tagInfo = (oTagInfo instanceof TagInfo)? (TagInfo)oTagInfo : new Gson().fromJson(oTagInfo.toString(), TagInfo.class);
		}
		return tagInfo;
	}
	
	
	public void addOrIncr(String key, long inc, Date expiry){
		if(!memcachedClient.add(key, inc, expiry)){
			memcachedClient.incr(key);
		}
	}
	
	public void decr(String key, Integer dec, Date expiry){
		if(null == dec){//如果选择消费的半价券为空，这默认消费所有的，重置为0
			memcachedClient.set(key, Long.valueOf(0), expiry);
		}else{
			memcachedClient.decr(key, Long.valueOf(dec));
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
	public void wxRedirect(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		String key = request.getParameter("key");
//		String openId = getOpenId(key);
		Map<String, String> map = getWechatUserInfo(key);
		String openId = map.get("openid");
		String subscribe = map.get("subscribe");
		logger.info("\n====share_redirect params: channelType: {}, deviceType: {}, openId: {},subscribe: {}", channelType, deviceType, openId,subscribe);
		response.sendRedirect(staticSourceUrl+"/pages/bnbjc1508/transfer.html?openid="+openId+"&channelType="+channelType+"&deviceType="+deviceType);
//		response.sendRedirect("/"+urlHead+"/pages/bnbjc1508/transfer.html?openid="+openId+"&channelType="+channelType+"&deviceType="+deviceType);
	}

	/**
	 * 即时获取微信用户信息
	 * @param key
	 * @return
	 */
	public Map<String, String> getWechatUserInfo(String key) {
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", interface_);
		getParams.put("action", action_2);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		postParams.put("key", key);
		String httpUrl = super.packHttpUrl(actionHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", action_2, httpUrl);
		logger.info("\n===={} post params: {}", action_2, JSON.toJSONString(postParams));
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String result = HttpClientUtil.sendPostRequestByJava(httpUrl, postParams);
			logger.info("\n===={} return result: {}", action_2, result);
			JSONObject resultJSON = JSONObject.fromObject(result);
			if(null == resultJSON.get("data") || StringUtils.equals(resultJSON.get("data").toString(), "[]")){
				resultMap.put("openid", "");
				resultMap.put("subscribe", "");
				return resultMap;
			}
			Object obj = JSONObject.fromObject(resultJSON.get("data")).get("openid");
			if(null == obj || StringUtils.isBlank(obj.toString())){
				resultMap.put("openid", "");
				resultMap.put("subscribe", "");
				return resultMap;
			}
			String openid = (String) obj;
			String subscribe = (String) JSONObject.fromObject(resultJSON.get("data")).get("subscribe");
			String nickname = (String) JSONObject.fromObject(resultJSON.get("data")).get("nickname");
			nickname = null == nickname ? "" : nickname;
			String sex = (String) JSONObject.fromObject(resultJSON.get("data")).get("sex");
			String province = (String) JSONObject.fromObject(resultJSON.get("data")).get("province");
			String city = (String) JSONObject.fromObject(resultJSON.get("data")).get("city");
			resultMap.put("subscribe", subscribe);
			resultMap.put("openid", openid);
			resultMap.put("nickname", nickname);
			resultMap.put("sex", sex);
			resultMap.put("province", province);
			resultMap.put("city", city);
			logger.info("\n====openid: {}, nickname: {},subscribe: {}", openid, nickname,subscribe);
			if(StringUtils.isNotBlank(openid)){
				memcachedClient.set(CACHE_KEY_NICKNAME+"-"+openid,  nickname, 0);
				this.saveUser(openid, nickname, sex, province, city);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
	private void saveUser(String userId, String nickName, String sex, String province, String city) {
		final Bnbjc1508User user = new Bnbjc1508User(userId, nickName, sex, province, city, new Date(System.currentTimeMillis()));
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, user);
			RabbitMQHelper.publish(Bnbjc1508User.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveUser(user);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
			});
		} else {
			service.saveUser(user);
		}
	}
	
	private String getNickName(final String userId) {
		Object retValue = getCache(CACHE_KEY_NICKNAME+"-"+userId, null, CacheOp.SET, new DataGenerator(){
			@Override
			public Object generate() {
				String name = service.getNickName(userId);
				return name;
			}
		});
		return null == retValue ? "" : retValue.toString();
	}
	
	/**
	 * 即时获取 ThirdWechatState 方法
	 * @return
	 * 
	 */
	@Override
	public Result getThirdWechatState(Parameter parameter) {
		logger.info("\n====getThirdWechatState input params: {}", JSON.toJSONString(parameter));
		//检查设备类型是否正确
		if(StringUtils.isEmpty(parameter.getRedirectUrl())){
			return new Result(ApiErrorCode.REDIRECT_URL_ISNULL, "redirect url isnull");
		}
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", interface_);
		getParams.put("action", action_);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		postParams.put("redirect_uri", parameter.getRedirectUrl());
		String httpUrl = super.packHttpUrl(actionHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", action_,  httpUrl);
		logger.info("\n===={} post params: {}", action_, JSON.toJSONString(postParams));
		Map<String, String> resultMap = null;
		try {
			String result = HttpClientUtil.sendPostRequestByJava(httpUrl, postParams);
			logger.info("\n===={} return result: {}", action_, result);
			resultMap = JSON.parseObject(result, new TypeReference<Map<String, String>>(){}.getType());
			//resultMap = JacksonUtil.jsonToObject(result, HashMap.class);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, e.getMessage());
		}
		logger.info("\n====getThirdWechatState output params: {}", resultMap.toString());
		return new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, resultMap);
	}
}
