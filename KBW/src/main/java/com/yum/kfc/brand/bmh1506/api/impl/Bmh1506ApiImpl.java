package com.yum.kfc.brand.bmh1506.api.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.yum.kfc.brand.bmh1506.api.Bmh1506Api;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Ask;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Coupon;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Draw;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Open;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Share;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506User;
import com.yum.kfc.brand.bmh1506.service.Bmh1506Service;
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.FeiRuiResult;
import com.yum.kfc.brand.common.pojo.KBSResultWin;
import com.yum.kfc.brand.common.pojo.Parameter;
import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.common.service.AsynJob;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.BrowserUtil;
import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.common.utils.HttpClientUtil;
import com.yum.kfc.brand.common.utils.RestClientUtil;
import com.yum.kfc.brand.common.utils.SaltUtil;

/**
 * @author luolix
 *
 */
@Component
@Path("/bmh1506/")
@Scope("singleton")
public class Bmh1506ApiImpl extends BaseApiImpl implements Bmh1506Api {
	
	private static Logger logger = LoggerFactory.getLogger(Bmh1506ApiImpl.class);
	
	public static final String ASK_ENC_KEY = "h@&*A_+$";
	public static final String BROWSER_TOKEN_KEY = "&$%^/^^!";
	public static final int SALT_LENGTH = 6;
	public static final String CACHE_KEY_PREFIX = "KFC.CAMP.BMH1506.";
	public static final String CACHE_KEY_DRAW_COUPON = CACHE_KEY_PREFIX+"DRAWCOUPON-";
	public static final String CACHE_KEY_LAST_COUPON = CACHE_KEY_PREFIX+"LASTCOUPON-";
	private static final String CACHE_KEY_USERCOUNT = CACHE_KEY_PREFIX+"DRAWUSERCOUNT-";
	private static final String CACHE_KEY_USERWON = CACHE_KEY_PREFIX+"WINUSER-";
	private static final String CACHE_KEY_PHONEUSED = CACHE_KEY_PREFIX+"CREDITPHONE-";
	private static final String CACHE_KEY_WINUSER = CACHE_KEY_PREFIX+"WINUSER-";
	
	@Value("${campaign.start}")			public String campaignStart;
	@Value("${campaign.end}")			public String campaignEnd;
	@Value("${campaign.url.head}")  	private String urlHead;
	@Value("${ws.proxy.use}")			private String proxyUse;
	@Value("${ws.proxy.host}")			private String proxyHost;
	@Value("${ws.proxy.port}")			private String proxyPort;
	@Value("${campaign.win.rate}")		public int winRate;
	@Value("${campaign.limit.user}")	public int maxUserCount;
	@Value("${campaign.svc.kbs.win}")	public String kbsWinUrl;
	
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
	
	public static Random random = new Random();
	public static final int RANDOM_MAX = 1000000;
	public static final int AWARD_PHONE_TYPE = 20;
	
	@Autowired
	private MemCachedClient memcachedClient;

	@Autowired
	private Bmh1506Service service;
	
	@Autowired
	private Bmh1506TaskJob taskJob;

	@Override
	public Result open(Bmh1506Parameter parameter, HttpServletRequest request) {
		logger.info("\n====open input params: {}", JSON.toJSONString(parameter));
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
		final Bmh1506Open open = new Bmh1506Open();
		BeanUtils.copyProperties(parameter, open);
		open.setId(newUUID());
		open.setOpenTime(new Date());
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, open);
			RabbitMQHelper.publish(Bmh1506Open.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		String promoCode = this.getLastCoupon(parameter);
		promoCode  =  StringUtils.isNotBlank(promoCode) ? promoCode : null;
		data.put("promoCode", promoCode);
		result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("\n====open return value: {}", JSON.toJSONString(result));
		return result;
	}

	
	@Override
	public Result ask(Bmh1506Parameter parameter) {
		logger.info("\n====ask input params: {}", JSON.toJSONString(parameter));
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
		if (now.before(DateUtil.parseDate(campaignStart)) || now.after(DateUtil.parseDate(campaignEnd))) {
			return new Result(ApiErrorCode.CAMPAIGN_NOT_START, "campaign not start or finished");
		}
		int resultValue;
		String promoCode = "";
		if(parameter.getHitCount() >=3){
			promoCode = this.isShouldDrawCoupon(parameter, midNight);
			if(StringUtils.isBlank(promoCode)){
				try{
					//改用户是否可以领到半价券
					promoCode = this.callRewardCardService(parameter.getUserId());
					logger.info("\n====get promoCode: {}", promoCode);
					if(StringUtils.isNotBlank(promoCode)){
						resultValue = 0;
						//在缓存中设置该用户当天的半价券已经领取
						memcachedClient.set(CACHE_KEY_DRAW_COUPON+parameter.getChannelType()+"-"+parameter.getUserId(), promoCode, midNight);
						memcachedClient.set(CACHE_KEY_LAST_COUPON+parameter.getChannelType()+"-"+parameter.getUserId(), promoCode);
						//保存半价券
						this.saveCoupon(promoCode, parameter);
					}else{
						resultValue = 1;
					}
				} catch (Exception e) {
					logger.error("call feirui service failure, error describe: []",e.getMessage(),  e);
					return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "call feirui service failure");
				}
				
			}else{
				resultValue = 0;
			}
		}else{
			resultValue = 2;
		}
		final Bmh1506Ask ask = new Bmh1506Ask();
		BeanUtils.copyProperties(parameter, ask);
		ask.setId(newUUID());
		ask.setResult(resultValue);
		ask.setOpenId(parameter.getSid());
		ask.setAskTime(new Date(System.currentTimeMillis()));
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, ask);
			RabbitMQHelper.publish(Bmh1506Ask.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		data.put("result", ask.getResult());
		data.put("promoCode", promoCode);
		result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("\n====ask return value: {}", JSON.toJSONString(result));
		return result;
	}
	
	private String isShouldDrawCoupon(final Bmh1506Parameter param, Date midNight){
		Object clickValue = getCache(CACHE_KEY_DRAW_COUPON+param.getChannelType()+"-"+param.getUserId(), midNight, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				String promoCode = service.isShouldDrawCoupon(param.getChannelType(), param.getUserId());
				return StringUtils.isNotBlank(promoCode) ? promoCode : "";
			}
		});
		return clickValue.toString();
	}
	
	
	private String getLastCoupon(final Bmh1506Parameter param){
		Object clickValue = getCache(CACHE_KEY_LAST_COUPON+param.getChannelType()+"-"+param.getUserId(), null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				String promoCode = service.getLastCoupon(param.getUserId(), param.getChannelType());
				return StringUtils.isNotBlank(promoCode) ? promoCode : "";
			}
		});
		return clickValue.toString();
	}
	
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
	
	private void saveCoupon(String promoCode, Bmh1506Parameter parameter){
		//受邀人领取的半价券
		final Bmh1506Coupon coupon = new Bmh1506Coupon();
		BeanUtils.copyProperties(parameter, coupon);
		coupon.setId(newUUID());
		coupon.setOpenId(parameter.getSid());
		coupon.setIsUsed(0);
		coupon.setPromoCode(promoCode);
		coupon.setCouponTime(new Date(System.currentTimeMillis()));
		//保存半价券
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, coupon);
			RabbitMQHelper.publish(Bmh1506Coupon.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveCoupon(coupon);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			service.saveCoupon(coupon);
		}
	}
	
	
	@Override
	public Result collect(Bmh1506Parameter parameter) {
		logger.info("\n====collect input params: {}", JSON.toJSONString(parameter));
		if(Boolean.parseBoolean(proxyUse)){
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
			logger.info("remote kbs api : using proxy {}:{}", proxyHost, proxyPort);
		}
		Result result =  null;
		FeiRuiResult feiRuiResult;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String promoCode = parameter.getPromoCode();
			feiRuiResult = this.callCardBagService(promoCode);
			if(feiRuiResult.getRetcode() == 200 && null != feiRuiResult.getData()){
				data.put("data", feiRuiResult.getData().toString());
			}else{
				logger.error("\n====call setDwBdCardInfo service failure, error describe: ",feiRuiResult.getMsg());
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
	public Result share(Bmh1506Parameter parameter) {
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
		final Bmh1506Share share = new Bmh1506Share();
		BeanUtils.copyProperties(parameter, share);
		share.setId(newUUID());
		share.setOpenId(parameter.getSid());
		share.setShareTime(new Date(System.currentTimeMillis()));
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, share);
			RabbitMQHelper.publish(Bmh1506Share.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		logger.info("\n====share return value: {}", JSON.toJSONString(result));
		return result;
	}

	@Override
	public Result myCoupon(String userId, Integer channelType) {
		logger.info("\n====myCoupon input params: userId: {}, channelType: {}", userId, channelType);
		List<String> codeList = service.getAllCoupons(userId, channelType);
		Result result = null;
		if(!codeList.isEmpty()){
			result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, codeList);
		}else{
			result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, null);
			
		}
		logger.info("\n====myCoupon return value: {}", JSON.toJSONString(result));
		return result;
	}
	
	
	@Override
	public Result draw(Bmh1506Parameter parameter, HttpServletRequest request){
		logger.info("\n====draw input params: {}", JSON.toJSONString(parameter));
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
		if (now.before(DateUtil.parseDate(campaignStart)) || now.after(DateUtil.parseDate(campaignEnd))) {
			return new Result(ApiErrorCode.CAMPAIGN_NOT_START, "campaign not start or finished");
		}
//		if(exceedUserDrawTimes(parameter, midNight)) {
//			return new Result(ApiErrorCode.TOOMANY_USER_DRAWTIMES, "You have won today, please come again tomorrow");
//		}
		if(wonAlready(parameter)) {
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("win", false);
			return new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
			//return new Result(ApiErrorCode.USER_HAVE_WON, "User have won the draw");
		}
		int rate = winRate;
		int luck = random.nextInt(RANDOM_MAX);
		boolean gotWinChance = 0<=luck && luck<=RANDOM_MAX/rate;
		boolean won = false;
		String awardCodeId = null;
		if (gotWinChance){
			parameter.setAwardType(AWARD_PHONE_TYPE);
			KBSResultWin result = requestAwardFromKBS(parameter);
			if (result.isSuccess() && result.getData()!=null && result.getData().isWin() && StringUtils.isNotBlank(result.getData().getCode())){
				won = true;
				awardCodeId = result.getData().getCode();
				memcachedClient.set(CACHE_KEY_USERWON+parameter.getChannelType()+"-"+parameter.getUserId(), "1");
				logger.info("got award from KBS, result is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
			} else {
				logger.info("failed request award from KBS, return is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
			}
		}
		final Bmh1506Draw draw = new Bmh1506Draw();
		BeanUtils.copyProperties(parameter, draw);
		draw.setId(newUUID());
		draw.setDrawTime(new Date(System.currentTimeMillis()));
		draw.setWinAward(won ? 1 : 0);
		if(won){
			draw.setAwardCodeId(awardCodeId);
		}
		draw.setOpenId(parameter.getSid());
		//如果中奖了，直接保存到DB，DB失败写到日志，方便人工处理
		if(won){	
			boolean isSuccess = service.saveDraw(draw);
			if(!isSuccess){
				logger.error("win draw save to DB failure, draw data[{}]", JSON.toJSONString(draw));
				new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "general server error, please try it later");
			}
		}else{	//如果没有中奖，走MQ
			if (RabbitMQHelper.RABBIT_MQ_ENABLED){
				Message msg = new Message(Message.Type.CREATE, draw);
				RabbitMQHelper.publish(Bmh1506Draw.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
		addOrIncr(CACHE_KEY_USERCOUNT+parameter.getChannelType()+"-"+parameter.getUserId(), 1, midNight);
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("win", won);
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("\n====draw return value: {}", JSON.toJSONString(result));
		return result;
	}
	
	
	@Override
	public Result winInfo(Bmh1506Parameter parameter){
		logger.info("\n====winInfo input params: {}", JSON.toJSONString(parameter));
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		if(StringUtils.isBlank(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_ISNULL, "手机号码不能为空！");
		}else if(!isCorrectPhone(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_INCORRECT, "该手机号码不正确！");
		}
		if(!wonAlready(parameter)){
			return new Result(ApiErrorCode.SHARE_CONTENT_ISNULL, "您未中奖，填写无效！");
		}
		if(userWrited(parameter.getUserId())){
			return new Result(ApiErrorCode.DRAW_ADDRESS_ISNULL, "您已填过中奖手机号码，再次填写无效！");
		}
		if(phoneUsed(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_WINAWARD, "该手机号已中过奖！");
		}

		Bmh1506Draw draw = new Bmh1506Draw();
		BeanUtils.copyProperties(parameter, draw);
		boolean isSuccess = service.saveWinAward(draw);
		Result result = null;
		if(!isSuccess){
			logger.error("win person info save to DB failure, win persion data[{}]", JSON.toJSONString(draw));
			result = new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "该手机号码已中过奖！");
		}else{
			memcachedClient.set(CACHE_KEY_WINUSER+parameter.getUserId(), "1");
			memcachedClient.set(CACHE_KEY_PHONEUSED+parameter.getPhone(), "1");
			memcachedClient.set(CACHE_KEY_USERWON+parameter.getChannelType()+"-"+parameter.getUserId(), "1");
			result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS);
		}
		logger.info("\n====winInfo return value: {}", JSON.toJSONString(result));
		return result;
	}
	
	@Override
	public Result sendWinNotPhoneMsg(){
		if(Boolean.parseBoolean(proxyUse)){
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
			logger.info("remote kbs api : using proxy {}:{}", proxyHost, proxyPort);
		}
		final List<Bmh1506Draw> drawList = service.getAllNotPhoneWins();
		Result result = null;
		if(drawList.size() > 0){
			//发起人自动获得半价券
			Runnable t = new Runnable() {
				@Override
				public void run() {
					taskJob.callSendMsgService(drawList);
				}
			};
			this.asynExecute(new AsynJob(t));
			result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "检测到共有 "+drawList.size()+" 个用户中奖没有填写手机号码，后台异步线程正在发送消息给这些用户。", null);
		}else{
			result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "检测到共有 "+drawList.size()+" 个用户中奖没有填写手机号码。", null);
		}
		return result;
	}
	
	
	private KBSResultWin requestAwardFromKBS(Bmh1506Parameter param) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("channelType", param.getChannelType());
		p.put("userId", param.getUserId());
		p.put("token", param.getToken());
		p.put("awardType", param.getAwardType());
		String sr = RestClientUtil.callPostService(kbsWinUrl, p, String.class);
		logger.info("request award from kbs, param[{}], result[{}]", param, sr);
		KBSResultWin wr = new Gson().fromJson(sr, KBSResultWin.class);
		return wr;
	}
	

	private boolean phoneUsed(final String phone) {
		Object opu = getCache(CACHE_KEY_PHONEUSED+phone, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				boolean used = service.isPhoneUsed(phone);
				return used ? 1 : 0;
			}
		});
		return "1".equals(String.valueOf(opu));
	}
	
	
	private boolean userWrited(final String userId) {
		Object opu = getCache(CACHE_KEY_WINUSER+userId, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				boolean used = service.isUserWrited(userId);
				return used ? 1 : 0;
			}
		});
		return "1".equals(String.valueOf(opu));
	}
	

	/*private boolean exceedUserDrawTimes(final Parameter param, Date midNight) {
		Object oUserCount = getCache(CACHE_KEY_USERCOUNT+param.getChannelType()+"-"+param.getUserId(), midNight, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				return service.queryDrawTimesByUserToday(param.getUserId(), param.getChannelType());
			}
		});
		
		if (oUserCount!=null){
			try{
				long ucount = Long.parseLong(String.valueOf(oUserCount));
				if (ucount > maxUserCount) return true;
			}catch(Exception e){
			}
		}
		return false;
	}*/
	
	
	private boolean wonAlready(final Parameter param){
		Object owf = getCache(CACHE_KEY_USERWON+param.getChannelType()+"-"+param.getUserId(), null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				boolean isWon = service.queryIsUserWon(param.getUserId(), param.getChannelType());
				return isWon ? 1 : 0;
			}
		});
		
		return "1".equalsIgnoreCase(String.valueOf(owf));
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
		String channelType = "1";//BrowserUtil.queryChannelType(request);
		String deviceType = "1";//BrowserUtil.queryDeviceType(request);
		
		logger.info("===login===\n" + "channelType==" +channelType + "  ||  " + "deviceType==" + deviceType );
		
		if(Integer.parseInt(channelType) == CHANNEL_TYPE_WECHAT){
			logger.info("===login===  weixin");
			try {
				response.sendRedirect("/"+urlHead+"/pages/bmh1506/wx_redirect.html");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}else{
			//对于非微信渠道
			String openid = Parameter.newUUID();
			String token = SaltUtil.encryption(SaltUtil.newSalt(6)+ DigestUtils.md5Hex(openid), Bmh1506ApiImpl.BROWSER_TOKEN_KEY);
			logger.info("===login===  browser");
			try {
				response.sendRedirect("/"+urlHead+"/pages/bmh1506/index.html?&channelType="+channelType+"&deviceType="+deviceType+"&token="+token +"&openid="+openid);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	@Override
	public void wxRedirect(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		String key = request.getParameter("key");
//		String openId = getOpenId(key);
		Map<String, String> map = getWechatUserInfo(key);
		String openId = map.get("openid");
		logger.info("\n====wxRedirect params: channelType: {}, deviceType: {}, openId: {}", channelType, deviceType, openId);
		response.sendRedirect("/"+urlHead+"/pages/bmh1506/transfer.html?openid="+openId+"&channelType="+channelType+"&deviceType="+deviceType);
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
			if(null == resultJSON.get("data") || StringUtils.isBlank(resultJSON.get("data").toString())){
				resultMap.put("openid", "");
				return resultMap;
			}
			String openid = (String) JSONObject.fromObject(resultJSON.get("data")).get("openid");
			String nickname = (String) JSONObject.fromObject(resultJSON.get("data")).get("nickname");
			nickname = null == nickname ? "" : nickname;
			String sex = (String) JSONObject.fromObject(resultJSON.get("data")).get("sex");
			String province = (String) JSONObject.fromObject(resultJSON.get("data")).get("province");
			String city = (String) JSONObject.fromObject(resultJSON.get("data")).get("city");
			resultMap.put("openid", openid);
			resultMap.put("nickname", nickname);
			resultMap.put("sex", sex);
			resultMap.put("province", province);
			resultMap.put("city", city);
			logger.info("\n====openid: {}, nickname: {}", openid, nickname);
			if(StringUtils.isNotBlank(openid)){
				this.saveUser(openid, nickname, sex, province, city);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
	private void saveUser(String userId, String nickName, String sex, String province, String city) {
		final Bmh1506User user = new Bmh1506User(userId, nickName, sex, province, city, new Date(System.currentTimeMillis()));
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, user);
			RabbitMQHelper.publish(Bmh1506User.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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
	
	@Override
	public void share_redirect(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		String key = request.getParameter("key");
//		String openId = getOpenId(key);
		Map<String, String> map = getWechatUserInfo(key);
		String openId = map.get("openid");
		logger.info("\n====share_redirect params: channelType: {}, deviceType: {}, openId: {}", channelType, deviceType, openId);
		response.sendRedirect("/"+urlHead+"/pages/bmh1506/wx_share_redirect.html?coid="+openId+"&channelType="+channelType+"&deviceType="+deviceType);
	}
	
	/**
	 * 即时获取 ThirdWechatState 方法
	 * @return
	 * 
	 */
	@Override
	public Result getThirdWechatState(Bmh1506Parameter parameter) {
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
