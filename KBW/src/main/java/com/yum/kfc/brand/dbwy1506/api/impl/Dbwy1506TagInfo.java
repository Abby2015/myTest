package com.yum.kfc.brand.dbwy1506.api.impl;


/**
 * tag信息
 * @author luolix
 * 
 */
public class Dbwy1506TagInfo {

	private Integer askType;	//求类型
	private Integer channelType; // 发起者渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; // 设备类型(0: android; 1:ios; 2:browser)
	private String userId; // 发起者Id
	
	public Integer getChannelType() {
		return channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getAskType() {
		return askType;
	}

	public void setAskType(Integer askType) {
		this.askType = askType;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	
	
}
