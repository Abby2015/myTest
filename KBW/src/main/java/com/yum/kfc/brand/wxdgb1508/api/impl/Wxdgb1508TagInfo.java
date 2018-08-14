package com.yum.kfc.brand.wxdgb1508.api.impl;


/**
 * tag信息
 * @author luolix
 * 
 */
public class Wxdgb1508TagInfo {

	private String askId;	//响应ID
	private Integer loveOption; // 非原创内容选项
	private String loveContent; // 原创内容
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

	public Integer getLoveOption() {
		return loveOption;
	}

	public void setLoveOption(Integer loveOption) {
		this.loveOption = loveOption;
	}

	public String getLoveContent() {
		return loveContent;
	}

	public void setLoveContent(String loveContent) {
		this.loveContent = loveContent;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public String getAskId() {
		return askId;
	}

	public void setAskId(String askId) {
		this.askId = askId;
	}

	
	
}
