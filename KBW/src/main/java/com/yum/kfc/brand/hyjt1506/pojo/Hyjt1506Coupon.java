package com.yum.kfc.brand.hyjt1506.pojo;

import java.util.Date;

/**
 * 半价券记录
 * @author luolix
 * 
 */
public class Hyjt1506Coupon implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String openId;	//打开ID
	private String userId; // 用户ID
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; // 设备类型(0: android; 1:ios; 2:browser)
	private String inviterId; // 发起人ID
	private Integer inviteChannelType; // 分享渠道类型(0: Brand App; 1: 微信;)
	private String promoCode;	//微信卡券序号
	private Boolean isPutCardBag = false; // 是否已经使用过（0：没有被使用；1：已被使用）
	private Date couponTime; // 领取时间
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getInviterId() {
		return inviterId;
	}

	public void setInviterId(String inviterId) {
		this.inviterId = inviterId;
	}

	public Integer getInviteChannelType() {
		return inviteChannelType;
	}

	public void setInviteChannelType(Integer inviteChannelType) {
		this.inviteChannelType = inviteChannelType;
	}


	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public Date getCouponTime() {
		return couponTime;
	}

	public void setCouponTime(Date couponTime) {
		this.couponTime = couponTime;
	}

	public Boolean getIsPutCardBag() {
		return isPutCardBag;
	}

	public void setIsPutCardBag(Boolean isPutCardBag) {
		this.isPutCardBag = isPutCardBag;
	}
}
