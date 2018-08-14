package com.yum.kfc.brand.dbwy1506.api.impl;

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
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.FeiRuiResult;
import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.common.service.AsynJob;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.BrowserUtil;
import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.common.utils.HttpClientUtil;
import com.yum.kfc.brand.common.utils.SaltUtil;
import com.yum.kfc.brand.dbwy1506.api.Dbwy1506Api;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Ask;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Coupon;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Open;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Share;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506User;
import com.yum.kfc.brand.dbwy1506.service.Dbwy1506Service;

/**
 * @author luolix
 *
 */
@Component
@Path("/dbwy1506/")
@Scope("singleton")
public class Dbwy1506ApiImpl extends BaseApiImpl implements Dbwy1506Api {
	
	private static Logger logger = LoggerFactory.getLogger(Dbwy1506ApiImpl.class);
	
	public static final String ASK_ENC_KEY = "V&*@_+h$";
	public static final String BROWSER_TOKEN_KEY = "-!%^_^&_";
	public static final int SALT_LENGTH = 6;
	public static final String CACHE_KEY_PREFIX = "KFC.CAMP.DBWY1506.";
	private static final String CACHE_KEY_DRAW_COUPON = CACHE_KEY_PREFIX+"DRAWCOUPON-";
	private static final String CACHE_KEY_LAST_COUPON = CACHE_KEY_PREFIX+"LASTCOUPON-";
	private final String CACHE_KEY_TAG = CACHE_KEY_PREFIX+"TAG-";
	private final String CACHE_KEY_NICKNAME = CACHE_KEY_PREFIX+"_NICKNAME-";
	
	@Value("${campaign.url.head}")  	private String urlHead;
	@Value("${ws.proxy.use}")			private String proxyUse;
	@Value("${ws.proxy.host}")			private String proxyHost;
	@Value("${ws.proxy.port}")			private String proxyPort;
	@Value("${wechat.home.url}")		private String wechatHomeUrl;
	
	
	@Value("${wechat.client_code}")				private String client_code;
	@Value("${wechat.client_secret}")			private String client_secret;
	@Value("${wechat.interface}")				private String interface_;
	@Value("${wechat.action}")					private String action_;
	@Value("${wechat.action2}")					private String action_2;
	@Value("${wechat.sendReward.interface}")	private String sendRewardInterface;
	@Value("${wechat.sendReward.action}")		private String sendRewardAction;
	@Value("${wechat.sendReward.eventId}")		private String sendRewardEventId;
	@Value("${wechat.sendReward.itemId}")		private String sendRewardItemId;
	@Value("${wechat.setCardBag.interface}")	private String setCardBagInterface;
	@Value("${wechat.setCardBag.action}")		private String setCardBagAction;
	@Value("${wechat.setCardBag.mainColor}")	private String setCardBagMainColor;
	@Value("${wechat.action.http.url}")			private String sendMsgHttpUrl;
	
	@Value("${campaign.start}")			public String campaignStart;
	@Value("${campaign.end}")			public String campaignEnd;
	
	
	@Autowired
	private MemCachedClient memcachedClient;

	@Autowired
	private Dbwy1506Service service;
	
	@Autowired
	private Dbwy1506InitiatorTaskJob initiatorTaskJob;

	@Override
	public Result open(Dbwy1506Parameter parameter, HttpServletRequest request) {
		logger.info("\n====open input params: {}", JSON.toJSONString(parameter));
		parameter.setIpAddr(WebUtil.getRealRemoteAddr(request));
		String openId = super.newUUID();
		//从缓存中取出Tag，如果Tag存在，说明用户点击的分享进入的，获得发起者的信息
		Dbwy1506TagInfo tagInfo = this.getTagInfo(parameter.getTag(), parameter.getNormalShare());
		if(null != tagInfo){
			parameter.setInviterId(tagInfo.getUserId());
			parameter.setInviteChannelType(tagInfo.getChannelType());
			if(null == parameter.getAskType()){
				parameter.setAskType(tagInfo.getAskType());
			}
		}
		//设置默认的求方式
		if(null == parameter.getAskType()){
			parameter.setAskType(0);
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
		//判断是否从分享链接进入
		boolean shareCome = false, other = false, drawCoupon = false;
		String promoCode = "";
		if(null != tagInfo){
			//是否从分享链接进入
			shareCome = true;
			//是否是其他人点击分享链接
			other = parameter.getUserId().equals(parameter.getInviterId()) ? false : true;
			//如果是其他用户，则领半价券
			if(other){
				//是否领取到半价券
				Date midNight = DateUtil.getMidNight();
				drawCoupon = this.isShouldDrawCoupon(parameter, midNight);
				//判断受邀人是否可以中奖
				if(drawCoupon){
					try{
						//受邀人可以领到半价券
						promoCode = this.callRewardCardService(parameter.getUserId());
						logger.info("\n====get promoCode: {}", promoCode);
						if(StringUtils.isNotBlank(promoCode)){
							this.saveCoupon(openId, promoCode, parameter, tagInfo);	//保存半价券
						}else{
							drawCoupon = false;
						}
					} catch (Exception e) {
						logger.error("call feirui service failure, error describe: []",e.getMessage(),  e);
						return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "call feirui service failure");
					}
				}
			}
		}
		if(StringUtils.isBlank(promoCode)){
			promoCode = this.getLastCoupon(parameter);
		}
		final Dbwy1506Open open = new Dbwy1506Open();
		BeanUtils.copyProperties(parameter, open);
		open.setId(openId);
		open.setOpenTime(new Date());
		//设置tag
		Dbwy1506TagInfo newTagInfo = new Dbwy1506TagInfo();
		BeanUtils.copyProperties(open, newTagInfo);
		String tagInfoStr = new Gson().toJson(newTagInfo);
		open.setTagContent(tagInfoStr);
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, open);
			RabbitMQHelper.publish(Dbwy1506Open.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		//检查抽奖有效期
		Date now = new Date();
		if (now.before(DateUtil.parseDate(campaignStart)) || now.after(DateUtil.parseDate(campaignEnd))) {
			data.put("isOutOfDate", "true");
		}else{
			data.put("isOutOfDate", "false");
		}
		
		data.put("sid", open.getId());
		data.put("shareCome", shareCome);
		data.put("other", other);
		data.put("drawCoupon", drawCoupon);
		data.put("promoCode", promoCode);
		String tag = SaltUtil.newSalt(SALT_LENGTH)+open.getId();
		//添加tag，放入缓存
		data.put("tag", SaltUtil.encryption(tag, ASK_ENC_KEY));
		//非正常分享的tag
		memcachedClient.set(CACHE_KEY_TAG+"0-" + open.getId(), tagInfoStr, 0);
		result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("\n====open ouput params: {}", JSON.toJSONString(result));
		return result;
	}
	
	
	private String getLastCoupon(final Dbwy1506Parameter param){
		Object clickValue = getCache(CACHE_KEY_LAST_COUPON+param.getChannelType()+"-"+param.getUserId(), null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				String promoCode = service.getLastCoupon(param.getUserId(), param.getChannelType());
				return StringUtils.isNotBlank(promoCode) ? promoCode : "";
			}
		});
		return clickValue.toString();
	}
	
	
	private String callRewardCardService(String userId)  throws Exception{
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
		String httpUrl = super.packHttpUrl(sendMsgHttpUrl, getParams, postParams);
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
	
	
	private void saveCoupon(final String openId, String promoCode, Dbwy1506Parameter parameter, final Dbwy1506TagInfo tagInfo) throws Exception{
		//设置已经领取
		Date midNight = DateUtil.getMidNight();
		memcachedClient.set(CACHE_KEY_DRAW_COUPON+parameter.getInviteChannelType()+"-"+parameter.getAskType()+"-"+parameter.getInviterId(),  "0", midNight);
		//设置最近的半价券
		memcachedClient.set(CACHE_KEY_LAST_COUPON+parameter.getChannelType()+"-"+parameter.getUserId(), promoCode);
		//受邀人领取的半价券
		final Dbwy1506Coupon inviteCoupon = new Dbwy1506Coupon();
		BeanUtils.copyProperties(parameter, inviteCoupon);
		inviteCoupon.setId(newUUID());
		inviteCoupon.setAskType(tagInfo.getAskType());
		inviteCoupon.setOpenId(openId);
		inviteCoupon.setPromoCode(promoCode);
		inviteCoupon.setCouponTime(new Date(System.currentTimeMillis()));
		//保存半价券
		//在缓存中设置该用户当天的半价券已经领取
		final String wxName = this.getNickName(parameter.getUserId());
		//发起人自动获得半价券
		Runnable t = new Runnable() {
			@Override
			public void run() {
				initiatorTaskJob.setService(service);
				//保存受邀人的半价券
				initiatorTaskJob.saveCoupon(inviteCoupon);
				//设置发起人的相关对象
				initiatorTaskJob.setTagInfo(tagInfo);
				//发起者领取半价券
				String promoCode = initiatorTaskJob.callRewardCardService();
				if(StringUtils.isNotBlank(promoCode)){
					//保存发起者半价券
					initiatorTaskJob.saveCoupon(promoCode, openId);
					//自动放入卡包
					FeiRuiResult feiRuiResult = initiatorTaskJob.callCardBagService(promoCode);
					if(feiRuiResult.getRetcode() == 200 && null != feiRuiResult.getData()){
						//给发起者发送模板消息
						initiatorTaskJob.callSendMsgService(feiRuiResult.getData().toString(), wxName);
					}
				}
			}
		};
		this.asynExecute(new AsynJob(t));
	}
	
	
	private boolean isShouldDrawCoupon(final Dbwy1506Parameter param, Date midNight){
		Object clickValue = getCache(CACHE_KEY_DRAW_COUPON+param.getInviteChannelType()+"-"+param.getAskType()+"-"+param.getInviterId(), midNight, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				boolean hasCoupon = service.isShouldDrawCoupon(param.getInviteChannelType(), param.getInviterId(), param.getAskType());
				return hasCoupon ? "1" : "0";
			}
		});
		return clickValue.toString().equals("1") ? true : false;
	}
	
	
	
	@Override
	public Result ask(Dbwy1506Parameter parameter, HttpServletRequest request) {
		logger.info("\n====ask input params: {}", JSON.toJSONString(parameter));
		//从缓存中取出Tag，如果Tag存在，说明用户点击的分享进入的，获得发起者的信息
		Dbwy1506TagInfo tagInfo = this.getTagInfo(parameter.getTag(), parameter.getNormalShare());
		if(null != tagInfo){
			if(null == parameter.getAskType()){
				parameter.setAskType(tagInfo.getAskType());
			}
		}
		//如果是点击界面右上角分享前进入，设置默认值
		if(null == parameter.getAskType()){
			parameter.setAskType(0);
		}
		//检查求友类型是否正确
		if(null == parameter.getAskType()){
			return new Result(ApiErrorCode.ACK_TYPE_ISNULL, "Ask Type is empty");
		}else if(!ASK_TYPE_LIST.contains(parameter.getAskType())){
			return new Result(ApiErrorCode.ACK_TYPE_INCORRECT, "Ask Type is incorrect");
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
		final Dbwy1506Ask ask = new Dbwy1506Ask();
		BeanUtils.copyProperties(parameter, ask);
		ask.setId(newUUID());
		ask.setOpenId(parameter.getSid());
		ask.setAskTime(new Date(System.currentTimeMillis()));
		//设置Tag
		Dbwy1506TagInfo newTagInfo = new Dbwy1506TagInfo();
		BeanUtils.copyProperties(parameter, newTagInfo);
		String tagInfoStr = new Gson().toJson(newTagInfo);
		ask.setTagContent(tagInfoStr);
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, ask);
			RabbitMQHelper.publish(Dbwy1506Ask.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveAsk(ask);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			service.saveAsk(ask);
		}
		Map<String,Object> data = new HashMap<String,Object>();
		String tag = SaltUtil.newSalt(SALT_LENGTH)+ask.getId();
		data.put("tag", SaltUtil.encryption(tag, ASK_ENC_KEY));
		//正常分享
		memcachedClient.set(CACHE_KEY_TAG+"1-" + ask.getId(), tagInfoStr, 0);
		result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("\n====ask output params: {}", JSON.toJSONString(result));
		return result;
	}
	
	@Override
	public Result share(Dbwy1506Parameter parameter) {
		logger.info("\n====share input params: {}", JSON.toJSONString(parameter));
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
		final Dbwy1506Share share = new Dbwy1506Share();
		BeanUtils.copyProperties(parameter, share);
		share.setId(newUUID());
		share.setOpenId(parameter.getSid());
		share.setShareTime(new Date(System.currentTimeMillis()));
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, share);
			RabbitMQHelper.publish(Dbwy1506Share.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null);
		logger.info("\n====share output params: {}", JSON.toJSONString(result));
		return result;
	}

	
	@Override
	public Result collect(Dbwy1506Parameter parameter) {
		logger.info("\n====collect input params: {}", JSON.toJSONString(parameter));
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
					data.put("data", "");
					//return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, feiRuiResult.getMsg());
				}
			}else{
				data.put("data", "");
			}
			result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		} catch (Exception e) {
			logger.error("call setDwBdCardInfo service failure, error describe: []",e.getMessage(),  e);
		}
		logger.info("\n====collect output params: {}", JSON.toJSONString(result));
		return  result;
	}
	
	
	private FeiRuiResult callCardBagService(String promoCode)  throws Exception{
		if(Boolean.parseBoolean(proxyUse)){
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
			logger.info("remote kbs api : using proxy {}:{}", proxyHost, proxyPort);
		}
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
		String httpUrl = super.packHttpUrl(sendMsgHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", setCardBagAction, httpUrl);
		FeiRuiResult feiRuiResult = super.callFeiRuiService(httpUrl, postParams);
		logger.info("\n===={} post params: {}", setCardBagAction, JSON.toJSONString(postParams));
		logger.info("\n===={} return result: {}", setCardBagAction, JSON.toJSONString(feiRuiResult));
		return feiRuiResult;
	}
			
	
	private Dbwy1506TagInfo getTagInfo(String tag, final Integer normalShare){
		Dbwy1506TagInfo tagInfo = null;
		if (StringUtils.isBlank(tag) && null == normalShare)
			return null;
		tag = SaltUtil.decryption(tag, ASK_ENC_KEY);
		final String askId = tag.substring(SALT_LENGTH);
		Object oTagInfo = getCache(CACHE_KEY_TAG + normalShare + "-" + askId, null, CacheOp.SET, new DataGenerator(){
			@Override
			public Object generate(){
				String tagStr = service.getTagContent(askId, normalShare);
				return tagStr;
			}
		});
		if (oTagInfo==null){
			logger.error("failed to get TagInfo for askId[{}], {}", askId);
			return null;
		}else{
			tagInfo = (oTagInfo instanceof Dbwy1506TagInfo)? (Dbwy1506TagInfo)oTagInfo : new Gson().fromJson(oTagInfo.toString(), Dbwy1506TagInfo.class);
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
	public void login(HttpServletRequest request, HttpServletResponse response) {
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		logger.info("\n====login params: channelType: {}, deviceType: {}", channelType, deviceType);
		if(Integer.parseInt(channelType) == CHANNEL_TYPE_WECHAT){
			//对于微信渠道
			logger.info("===login===  weixin");
			try {
				response.sendRedirect("/"+urlHead+"/pages/dbwy1506/wx_redirect.html");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}else if(Integer.parseInt(channelType) == CHANNEL_TYPE_APP){
			//对于mobile非微信渠道
			logger.info("===login===  app");
			try {
				response.sendRedirect("/"+urlHead+"/pages/dbwy1506/mobile.html");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}else{
			//对于PC渠道
			logger.info("===login===  browser");
			try {
				response.sendRedirect("/"+urlHead+"/pages/dbwy1506/web.html");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public void wxRedirect(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		String key = request.getParameter("key");
//		String openId = getOpenId(key);
		Map<String, String> map = getWechatUserInfo(key);
		String openId = map.get("openid");
		String subscribe = map.get("subscribe");
//		logger.info("\n====wxRedirect params: channelType: {}, deviceType: {}, openId: {}", channelType, deviceType, openId);
		logger.info("\n====share_redirect params: channelType: {}, deviceType: {}, openId: {},subscribe: {}", channelType, deviceType, openId,subscribe);
		response.sendRedirect("/"+urlHead+"/pages/dbwy1506/transfer.html?openid="+openId+"&channelType="+channelType+"&deviceType="+deviceType);
	}
	
	@Override
	public void share_redirect(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		String key = request.getParameter("key");
//		String openId = getOpenId(key);
		Map<String, String> map = getWechatUserInfo(key);
		String openId = map.get("openid");
		String subscribe = map.get("subscribe");
		logger.info("\n====share_redirect params: channelType: {}, deviceType: {}, openId: {},subscribe: {}", channelType, deviceType, openId,subscribe);
		response.sendRedirect("/"+urlHead+"/pages/dbwy1506/wx_share_redirect.html?coid="+openId+"&channelType="+channelType+"&deviceType="+deviceType);
	}
	
	/**
	 * 即时获取 ThirdWechatState 方法
	 * @return
	 * 
	 */
	@Override
	public Result getThirdWechatState(Dbwy1506Parameter parameter) {
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
		String httpUrl = super.packHttpUrl(sendMsgHttpUrl, getParams, postParams);
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
			return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "GENERAL_SERVER_ERROR");
		}
		logger.info("resultMap="+resultMap.toString());
		return new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, resultMap);
	}
	
	
	/**
	 * 即时获取 openid 方法
	 * @return
	 * 
	 */
	public String getOpenId(String key) {
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", interface_);
		getParams.put("action", action_2);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		postParams.put("key", key);
		String httpUrl = super.packHttpUrl(sendMsgHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", action_2,  httpUrl);
		logger.info("\n===={} post params: {}", action_2, JSON.toJSONString(postParams));
		String openid = "";
		try {
			String result = HttpClientUtil.sendPostRequestByJava(httpUrl, postParams);
			logger.info("\n===={} return result: {}", action_2, result);
			JSONObject resultJSON = JSONObject.fromObject(result);
			openid = (String) JSONObject.fromObject(resultJSON.get("data")).get("openid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("\n====getOpenId: {}", openid);
		return openid;
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
		String httpUrl = super.packHttpUrl(sendMsgHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", action_2, httpUrl);
		logger.info("\n===={} post params: {}", action_2, JSON.toJSONString(postParams));
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String result = HttpClientUtil.sendPostRequestByJava(httpUrl, postParams);
			logger.info("\n===={} return result: {}", action_2, result);
			JSONObject resultJSON = JSONObject.fromObject(result);
			if(StringUtils.isBlank(result) || result == null || result == ""){
				resultMap.put("openid", "");
				logger.info("\n====openid 为空--> result 为空");
				return resultMap;
			}
			if(null == resultJSON.get("data") || StringUtils.isBlank(resultJSON.get("data").toString())){
				resultMap.put("openid", "");
				logger.info("\n====openid 为空--> result 为空");
				return resultMap;
			}
			String subscribe = (String) JSONObject.fromObject(resultJSON.get("data")).get("subscribe");
			String openid = (String) JSONObject.fromObject(resultJSON.get("data")).get("openid");
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
		final Dbwy1506User user = new Dbwy1506User(userId, nickName, sex, province, city);
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, user);
			RabbitMQHelper.publish(Dbwy1506User.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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

}
