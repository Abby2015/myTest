package com.yum.kfc.brand.bnbjc1508.api.impl;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Coupon;
import com.yum.kfc.brand.bnbjc1508.service.Bnbjc1508Service;
import com.yum.kfc.brand.common.pojo.FeiRuiResult;
import com.yum.kfc.brand.common.pojo.TagInfo;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.HttpClientUtil;

@Component
public class Bnbjc1508TaskJob {
	
	private static Logger logger = LoggerFactory.getLogger(Bnbjc1508TaskJob.class);
	
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
	
	private Bnbjc1508Service service;
	private TagInfo tagInfo;

	
	//保存受邀人的半价券
	public boolean saveCoupon(Bnbjc1508Coupon coupon){
		boolean success = service.saveCoupon(coupon);
		return success;
	}
	
	public boolean saveCoupon(String promoCode, String openId){
		//封装发起用户的半价券
		Bnbjc1508Coupon initiatorCoupon = new Bnbjc1508Coupon();
		BeanUtils.copyProperties(tagInfo, initiatorCoupon);
		initiatorCoupon.setInviterId(tagInfo.getUserId());
		initiatorCoupon.setInviteChannelType(tagInfo.getChannelType());
		initiatorCoupon.setId(BaseApiImpl.newUUID());
		initiatorCoupon.setOpenId(openId);
		initiatorCoupon.setPromoCode(promoCode);
		initiatorCoupon.setCouponTime(new Date(System.currentTimeMillis()));
		boolean success = service.saveCoupon(initiatorCoupon);
		return success;
	}
	
	public String callRewardCardService(String userId) {
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

	
	public FeiRuiResult callSendMsgService(String userId, String nickName, String promoCode, boolean isAskUser){
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", sendMsgInterface);
		getParams.put("action", sendMsgAction);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		nickName = StringUtils.isNotBlank(nickName) ? nickName : "TA";
		String content = isAskUser ? askContent : ackContent;
		String contentNew = content.replace("{1}", nickName);
		String tpl_data = ptlData.replace("{0}", userId).replace("{1}", contentNew).replace("{2}", promoCode);
		postParams.put("tpl_data", tpl_data);
		String httpUrl = BaseApiImpl.packHttpUrl(sendMsgHttpUrl, getParams, postParams);
		logger.info("\n===={} http url: {}", sendMsgAction, httpUrl);
		postParams = new HashMap<String, String>();
		FeiRuiResult feiRuiResult = new FeiRuiResult();
		try {
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
	
	
	public void setService(Bnbjc1508Service service) {
		this.service = service;
	}

	public void setTagInfo(TagInfo tagInfo) {
		this.tagInfo = tagInfo;
	}
	
	

}
