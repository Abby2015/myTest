package com.yum.kfc.brand.dzp1807.pojo;

import java.util.Date;

public class Dzp1807Draw {
	private String orderId; // 订单号
	private String userId; // 用户ID
	private String ssoUserId;
	private String crmUserCode;
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; // 设备类型(0: android; 1:ios; 2:browser)
	private String deviceId; // 设备号
	private Integer winAward; // 是否中奖(0:否;1:是)
	private Integer awardType; // 奖品类型
	private String awardCode; // 奖品编码ID
	private String phone; // 手机号码
	private String ipAddr; // 客户端IP
	private Date drawTime; // 抽奖时间
	private Integer awardLevel; //奖品级别
	private String awardName; //奖品名称
	private String winImg; //中奖大图
	private String personalImg; //中奖小图
	private boolean isFree; //是否来自免费抽奖
	private Integer couponType; //优惠券类型
	private String url;

	public String getAwardName() {
		return awardName;
	}

	public void setAwardName(String awardName) {
		this.awardName = awardName;
	}

	public String getWinImg() {
		return winImg;
	}

	public void setWinImg(String winImg) {
		this.winImg = winImg;
	}

	public String getPersonalImg() {
		return personalImg;
	}

	public void setPersonalImg(String personalImg) {
		this.personalImg = personalImg;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getWinAward() {
		return winAward;
	}

	public void setWinAward(Integer winAward) {
		this.winAward = winAward;
	}

	public Integer getAwardType() {
		return awardType;
	}

	public void setAwardType(Integer awardType) {
		this.awardType = awardType;
	}

	public String getAwardCode() {
		return awardCode;
	}

	public void setAwardCode(String awardCode) {
		this.awardCode = awardCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Date getDrawTime() {
		return drawTime;
	}

	public void setDrawTime(Date drawTime) {
		this.drawTime = drawTime;
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

	public Integer getAwardLevel() {
		return awardLevel;
	}

	public void setAwardLevel(Integer awardLevel) {
		this.awardLevel = awardLevel;
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
