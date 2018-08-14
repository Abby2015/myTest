package com.yum.kfc.brand.xrbj1506.api.impl;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yum.kfc.brand.common.pojo.FeiRuiResult;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.HttpClientUtil;
import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506Coupon;
import com.yum.kfc.brand.xrbj1506.service.Xrbj1506Service;

@Component
public class InitiatorTaskJob {
	
	private static Logger logger = LoggerFactory.getLogger(InitiatorTaskJob.class);
	
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
	@Value("${wechat.sendMsg.tplData}")			private String ptlData;
	
	private Xrbj1506Service service;
	private Xrbj1506TagInfo tagInfo;


	public String callRewardCardService() {
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", sendRewardInterface);
		getParams.put("action", sendRewardAction);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		postParams.put("open_id", tagInfo.getUserId());
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
	
	//保存受邀人的半价券
	public boolean saveCoupon(Xrbj1506Coupon coupon){
		boolean success = service.saveCoupon(coupon);
		return success;
	}
	
	
	public boolean saveCoupon(String promoCode, String openId){
		//封装发起用户的半价券
		Xrbj1506Coupon initiatorCoupon = new Xrbj1506Coupon();
		BeanUtils.copyProperties(tagInfo, initiatorCoupon);
		initiatorCoupon.setInviterId(tagInfo.getUserId());
		initiatorCoupon.setInviteChannelType(tagInfo.getChannelType());
		initiatorCoupon.setId(BaseApiImpl.newUUID());
		initiatorCoupon.setOpenId(openId);
		initiatorCoupon.setIsUsed(0);
		initiatorCoupon.setPromoCode(promoCode);
		initiatorCoupon.setCouponTime(new Date(System.currentTimeMillis()));
		boolean success = service.saveCoupon(initiatorCoupon);
		return success;
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

	
	public FeiRuiResult callSendMsgService(String data, String wxName){
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", client_code);
		getParams.put("client_secret", client_secret);
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", sendMsgInterface);
		getParams.put("action", sendMsgAction);
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		String askFriend = tagInfo.getAskType() == 0 ? "好闺蜜" : "好兄弟";
		String tpl_data = ptlData.replace("{0}", tagInfo.getUserId()).replace("{1}", data).replace("{2}", askFriend).replace("{3}", wxName);
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
	
	
	public void setService(Xrbj1506Service service) {
		this.service = service;
	}

	public void setTagInfo(Xrbj1506TagInfo tagInfo) {
		this.tagInfo = tagInfo;
	}


}
