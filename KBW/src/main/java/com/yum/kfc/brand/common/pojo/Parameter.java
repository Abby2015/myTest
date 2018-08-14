package com.yum.kfc.brand.common.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 参数对象
 * @author luolix
 * 
 */
public class Parameter implements Serializable {

	private static final long serialVersionUID = 1146949984566635596L;

	private String sid; // 会话标记（打开活动的主键：openId）
	private String ipAddr;	//客户端IP地址
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private String userId; // 用户Id
	private String token; // token
	private Integer deviceType; // 设备类型(0: android; 1:ios; 2:browser)
	private String inviterId; // 发起人ID
	private Integer inviteChannelType; // 发起渠道(0: Brand App; 1: 微信; 2:浏览器)
	private String deviceId; // 设备号
	private String mediaType;	//分享媒介(QQ|WX|TWB|WB|RR|DB|TB|ZFB)
	private String phone;
	private Integer shareResult; // 分享结果(0:false, 1:true)
	private String shareUrl;	//分享地址
	private String tag; //分享出去的tag,解密后为分享的相关信息
	private String sceneId;	//场景ID
	private String promoCode; // 优惠券号码
	private String redirectUrl; //回转地址
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getInviterId() {
		return inviterId;
	}

	public void setInviterId(String inviterId) {
		this.inviterId = inviterId;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public Integer getShareResult() {
		return shareResult;
	}

	public void setShareResult(Integer shareResult) {
		this.shareResult = shareResult;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getChannelType() {
		return channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public Integer getInviteChannelType() {
		return inviteChannelType;
	}

	public void setInviteChannelType(Integer inviteChannelType) {
		this.inviteChannelType = inviteChannelType;
	}
	
	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public String getSceneId() {
		return sceneId;
	}

	public void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}
	

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public static String newUUID(){
		return String.format("%X%S", new Date().getTime(), UUID.randomUUID().toString().replace("-", ""));
	}
	
}
