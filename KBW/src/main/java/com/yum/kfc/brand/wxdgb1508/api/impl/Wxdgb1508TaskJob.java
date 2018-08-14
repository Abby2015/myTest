package com.yum.kfc.brand.wxdgb1508.api.impl;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.danga.MemCached.MemCachedClient;
import com.yum.kfc.brand.common.pojo.FeiRuiResult;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.HttpClientUtil;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Coupon;
import com.yum.kfc.brand.wxdgb1508.service.Wxdgb1508Service;

@Component
public class Wxdgb1508TaskJob {
	
	private static Logger logger = LoggerFactory.getLogger(Wxdgb1508TaskJob.class);
	
	@Value("${wechat.client_code}")				private String client_code;
	@Value("${wechat.client_secret}")			private String client_secret;
	@Value("${wechat.sendReward.interface}")	private String sendRewardInterface;
	@Value("${wechat.sendReward.action}")		private String sendRewardAction;
	@Value("${wechat.sendReward.eventId}")		private String sendRewardEventId;
	@Value("${wechat.sendReward.itemId}")		private String sendRewardItemId;
	@Value("${wechat.action.http.url}")			private String sendMsgHttpUrl;
	@Value("${wechat.sendMsg.interface}")		private String sendMsgInterface;
	@Value("${wechat.sendMsg.action}")			private String sendMsgAction;
	@Value("${wechat.setCardBag.interface}")	private String setCardBagInterface;
	@Value("${wechat.setCardBag.action}")		private String setCardBagAction;
	@Value("${wechat.setCardBag.mainColor}")	private String setCardBagMainColor;
	@Value("${wechat.home.url}")				private String wechatHomeUrl;
	@Value("${wechat.sendMsg.ask.content}")		private String askContent;
	@Value("${wechat.sendMsg.ack.content}")		private String ackContent;
	@Value("${wechat.sendMsg.tplData}")			private String ptlData;
	@Value("${wechat.sendMsg.tplData.miss}")	private String ptlDataMiss;
	@Value("${wechat.miss.reward.eventId}")		private String sendMissEventId;
	@Value("${wechat.miss.reward.itemId}")		private String sendMissItemId;
	@Value("${wechat.miss.ask.content}")		private String askMissContent;
	@Value("${wechat.miss.ack.content}")		private String ackMissContent;
	
	private  Wxdgb1508Service service;
//	private  Wxdgb1508TagInfo tagInfo;
//	private  Wxdgb1508Parameter parameter;
	private MemCachedClient memcachedClient;

	
	//保存受邀人的半价券
//	public boolean saveCoupon(Wxdgb1508Coupon coupon){
//		boolean success = service.saveCoupon(coupon);
//		return success;
//	}
	
	public boolean saveFanCoupon(Wxdgb1508Parameter parameter, String promoCode){
		final Wxdgb1508Coupon inviteCoupon = new Wxdgb1508Coupon();
		BeanUtils.copyProperties(parameter, inviteCoupon);
		inviteCoupon.setId(newUUID());
		inviteCoupon.setOpenId(parameter.getSid());
		inviteCoupon.setPromoCode(promoCode);
		inviteCoupon.setCouponTime(new Date(System.currentTimeMillis()));
		boolean success = service.saveCoupon(inviteCoupon);
		memcachedClient.set(Wxdgb1508ApiImpl.CACHE_KEY_LAST_COUPON+inviteCoupon.getChannelType()+"-"+inviteCoupon.getUserId(), promoCode);
		return success;
	}
	
	public static String newUUID(){
		return String.format("%X%S", new Date().getTime(), UUID.randomUUID().toString().replace("-", ""));
	}
	
	public boolean saveSendCoupon(Wxdgb1508TagInfo tagInfo, String promoCode, String openId){
		//封装发起用户的半价券
		Wxdgb1508Coupon initiatorCoupon = new Wxdgb1508Coupon();
		BeanUtils.copyProperties(tagInfo, initiatorCoupon);
		initiatorCoupon.setInviterId(tagInfo.getUserId());
		initiatorCoupon.setInviteChannelType(tagInfo.getChannelType());
		initiatorCoupon.setId(BaseApiImpl.newUUID());
		initiatorCoupon.setOpenId(openId);
		initiatorCoupon.setPromoCode(promoCode);
		initiatorCoupon.setCouponTime(new Date(System.currentTimeMillis()));
		boolean success = service.saveCoupon(initiatorCoupon);
		memcachedClient.set(Wxdgb1508ApiImpl.CACHE_KEY_LAST_COUPON+initiatorCoupon.getChannelType()+"-"+initiatorCoupon.getUserId(), promoCode);
		return success;
	}
	
	public boolean saveMissCoupon(String userId, String promoCode){
		//封装发起用户的半价券
		Wxdgb1508Coupon coupon = new Wxdgb1508Coupon();
		coupon.setInviterId(userId);
		coupon.setInviteChannelType(1);
		coupon.setChannelType(1);
		coupon.setDeviceType(1);
		coupon.setId(BaseApiImpl.newUUID());
		coupon.setOpenId(BaseApiImpl.newUUID());
		coupon.setUserId(userId);
		coupon.setPromoCode(promoCode);
		coupon.setCouponTime(new Date(System.currentTimeMillis()));
		boolean success = service.saveCoupon(coupon);
		memcachedClient.set(Wxdgb1508ApiImpl.CACHE_KEY_LAST_COUPON+"1-"+userId, promoCode);
		return success;
	}
	
	public String callRewardCardService(String userId, boolean isMissCoupon) {
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", sendRewardInterface);
		getParams.put("action", sendRewardAction);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		postParams.put("open_id", userId);
		if(isMissCoupon){
			postParams.put("event_id", sendMissEventId);
			postParams.put("item_id", sendMissItemId);
		}else{
			postParams.put("event_id", sendRewardEventId);
			postParams.put("item_id", sendRewardItemId);
		}
		String httpUrl = BaseApiImpl.packHttpUrl(sendMsgHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", sendRewardAction, httpUrl);
		logger.info("\n===={} post params: {}", sendRewardAction, JSON.toJSONString(postParams));
		String promoCode = "";
		try {
			String result = HttpClientUtil.sendPostRequestByJava(httpUrl, postParams);
			logger.info("\n===={} return result: {}", sendRewardAction, result);
			JSONObject resultJSON = JSONObject.fromObject(result);
			if(null != resultJSON.get("retcode") && resultJSON.get("retcode").toString().equals("200")){
				promoCode  = (String)JSONObject.fromObject(resultJSON.get("data")).get("promo_code");
			}
		} catch (Exception e) {
			logger.error("call getTypeSendRewardItem failure, error describe: []", e.getMessage(),  e);
		}
		return promoCode;
	}
	
	public FeiRuiResult callCardBagService(String promoCode){
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
		String httpUrl = BaseApiImpl.packHttpUrl(sendMsgHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", setCardBagAction, httpUrl);
		logger.info("\n===={} post params: {}", setCardBagAction, JSON.toJSONString(postParams));
		FeiRuiResult feiRuiResult = new FeiRuiResult();
		try {
			feiRuiResult = BaseApiImpl.callFeiRuiService(httpUrl, postParams);
			logger.info("\n===={} return result: {}", setCardBagAction, JSON.toJSONString(feiRuiResult));
		} catch (Exception e) {
			logger.error("call getTypeSendRewardItem failure, error describe: []", e.getMessage(),  e);
		}
		logger.info("====setDwBdCardInfo return result: {}", JSON.toJSONString(feiRuiResult));
		return feiRuiResult;
	}

	
	public FeiRuiResult callSendMsgService(String userId, String nickName, Integer loveOption,String loveContent, String promoCode, boolean isAskUser, boolean isMissCoupon){
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", sendMsgInterface);
		getParams.put("action", sendMsgAction);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		nickName = StringUtils.isNotBlank(nickName) ? nickName : "TA";
		String contentNew = "";
		if(isMissCoupon){
			contentNew = isAskUser ? askMissContent : ackMissContent;
		}else{
			String content = isAskUser ? askContent : ackContent;
			contentNew = content.replace("{0}", nickName);
		}
		String option = null == loveOption ? "" : loveOption.toString();
		loveContent = null == loveContent ? "" : loveContent;
		FeiRuiResult feiRuiResult = new FeiRuiResult();
		try {
			String tpl_data = (isMissCoupon?ptlDataMiss:ptlData).replace("{0}", userId).replace("{1}", promoCode).replace("{2}", String.valueOf(isAskUser)).replace("{3}", 
					option).replace("{4}", loveContent).replace("{5}", contentNew);
			postParams.put("tpl_data", tpl_data);
			String httpUrl = BaseApiImpl.packHttpUrl(sendMsgHttpUrl, getParams, postParams);
			logger.info("\n===={} http url: {}", sendMsgAction, httpUrl);
			postParams = new HashMap<String, String>();
			tpl_data=URLEncoder.encode(tpl_data,"utf-8");
			postParams.put("tpl_data", tpl_data);
			logger.info("\n===={} post params: {}", sendMsgAction, JSON.toJSONString(postParams));
			feiRuiResult = BaseApiImpl.callFeiRuiService(httpUrl, postParams);
			logger.info("\n===={} return result: {}", sendMsgAction, JSON.toJSONString(feiRuiResult));
		}catch (Exception e) {
			logger.error("call tpl_data failure, error describe: []", e.getMessage(),  e);
		}
		logger.info("====raw_tpl return result: {}", JSON.toJSONString(feiRuiResult));
		return feiRuiResult;
	}
	
	public void setService(Wxdgb1508Service service) {
		this.service = service;
	}

//	public void setTagInfo(Wxdgb1508TagInfo tagInfo) {
//		this.tagInfo = tagInfo;
//	}
//
//	public void setParameter(Wxdgb1508Parameter parameter) {
//		this.parameter = parameter;
//	}

	public void setMemcachedClient(MemCachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
	

}
