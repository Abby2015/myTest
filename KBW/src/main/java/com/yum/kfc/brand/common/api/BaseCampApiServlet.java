package com.yum.kfc.brand.common.api;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.yum.kfc.brand.api.BaseBrandApiServlet;
import com.yum.kfc.brand.api.WXFeiruiApiServlet;
import com.yum.kfc.brand.common.utils.RestClientUtil;
import com.yum.kfc.brand.crm.pojo.ClientChannel;

/**
 * @author DING Weimin (wei-min.ding@hpe.com) Feb 1, 2018 6:11:59 PM
 *
 */
public class BaseCampApiServlet extends BaseBrandApiServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(BaseCampApiServlet.class);
	
	protected static final String KBS_GET_USER_URL = ApplicationConfig.getProperty("campaign.svc.kbs.user");
	
	protected UserInfo getUserInfoByToken(final String token, final String kbck) {
		Object o =super.getOrGenCacheableData("CAMP_USER_INFO_"+token, null, Integer.MAX_VALUE, new CacheableDataGenerator(){
				@SuppressWarnings("serial")
				@Override
				public CacheableData generate() {
					Map<String, String> p = new HashMap<String, String>();
					p.put("token", token);
					String str = RestClientUtil.callPostService(KBS_GET_USER_URL, new HashMap<String, String>(){{put("kbck", kbck);}}, p, String.class);
					log.info("request user info from kbs, param[{}], result[{}]", p, str);
					JSONObject object = JSONObject.parseObject(str).getJSONObject("data");
					
					UserInfo ui = null;
					if (object!=null){
						ui = new UserInfo(object.getString("userId"),
											object.getString("ssoUserId"),
											object.getString("crmUserCode"),
											object.getString("phone"),
											object.getString("nickname")
											);
					}
					
					return new CacheableData(ui);
				}
			});
		
		UserInfo userInfo = null;
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) return null;
		userInfo = (UserInfo) (
									(o instanceof UserInfo)
									? o
									: JSON.parseObject(so, UserInfo.class)
									);
		
		return userInfo;
	}
	
	protected void asyncSendWXTextMsg(final String msgContent, final String openId, final String token,
									final int reqHashCode, final String clientIP, final ClientChannel clientChannel) {
		Map<String, String> attenderNotify = new HashMap<String, String>();
		attenderNotify.put("content", msgContent);
		attenderNotify.put("targetOpenId", openId);
		attenderNotify.put("token", token);
		attenderNotify.put("reqFlag", ""+reqHashCode);
		attenderNotify.put("clientIP", clientIP);
		attenderNotify.put("clientChannel", clientChannel==null?null:clientChannel.name());
		
		if(RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, attenderNotify);
			RabbitMQHelper.publish("BRAND.KFC.CAMP.WX.MSG", msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					WXFeiruiApiServlet.sendMsg(openId, msgContent, token, reqHashCode, clientIP, clientChannel);
				}
			});
		} else {
			WXFeiruiApiServlet.sendMsg(openId, msgContent, token, reqHashCode, clientIP, clientChannel);
		}
	}
	
	protected void asyncSendWXTemplateMsg(final String openId, final Map<String, Object> msgNotify, final String token,
										final int reqHashCode, final String clientIP, final ClientChannel clientChannel) {
		final Map<String, Object> mqMsg = new HashMap<String, Object>();
		mqMsg.put("openId", openId);
		mqMsg.put("notifyMsg", msgNotify);
		mqMsg.put("token", token);
		mqMsg.put("reqFlag", reqHashCode);
		mqMsg.put("clientIP", clientIP);
		mqMsg.put("clientChannel", clientChannel);
		
		if(RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.UPDATE, mqMsg);
			RabbitMQHelper.publish("BRAND.KFC.CAMP.WX.MSG", msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					WXFeiruiApiServlet.sendMsg(openId, msgNotify, token, reqHashCode, clientIP, clientChannel);
				}
			});
		} else {
			WXFeiruiApiServlet.sendMsg(openId, msgNotify, token, reqHashCode, clientIP, clientChannel);
		}
	}
	
	public static class UserInfo {
		private String userId;
		private String ssoUserId;
		private String crmUserCode;
		private String phone;
		private String nickname;
		
		public UserInfo(){}
				
		public UserInfo(String userId, String ssoUserId, String crmUserCode, String phone, String nickname) {
			this.userId = userId;
			this.ssoUserId = ssoUserId;
			this.crmUserCode = crmUserCode;
			this.phone = phone;
			this.nickname = nickname;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getSsoUserId() {
			return ssoUserId;
		}

		public void setSsoUserId(String ssoUserId) {
			this.ssoUserId = ssoUserId;
		}

		public String getCrmUserCode() {
			return crmUserCode;
		}

		public void setCrmUserCode(String crmUserCode) {
			this.crmUserCode = crmUserCode;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
	}
}
