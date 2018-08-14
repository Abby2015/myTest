package com.yum.kfc.brand.common.utils;

import com.hp.jdf.ssm.util.BaseHttpRequestLog;

/**
 * @author DING Weimin (wei-min.ding@hpe.com) Mar 30, 2017 2:27:15 PM
 *
 */
public class BaseBrandHttpRequestLog extends BaseHttpRequestLog {

	private static final long serialVersionUID = 824522570934159189L;
	
	private String targetDbTableName;

	private String kfcBrandIP;

	private String reqFlag;
	private String clientIP;
	private String channel;

	private String openid;

	private String token;
	private String phone;
	private String userId;
	private String ssoUserId;
	private String crmUserCode;

	private String deviceType;
	private String deviceId;
	private String jpushRegId;

	public String getTargetDbTableName() {
		return targetDbTableName;
	}

	public void setTargetDbTableName(String targetDbTableName) {
		this.targetDbTableName = targetDbTableName;
	}

	public String getKfcBrandIP() {
		return kfcBrandIP;
	}

	public void setKfcBrandIP(String kfcBrandIP) {
		this.kfcBrandIP = kfcBrandIP;
	}

	public String getReqFlag() {
		return reqFlag;
	}

	public void setReqFlag(String reqFlag) {
		this.reqFlag = reqFlag;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getJpushRegId() {
		return jpushRegId;
	}

	public void setJpushRegId(String jpushRegId) {
		this.jpushRegId = jpushRegId;
	}

}
