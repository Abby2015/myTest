package com.yum.kfc.brand.hyjt1506.pojo;

import java.util.Date;

/**
 * 爆米花--用户抽奖记录
 * 
 * @author luolix
 * 
 */
public class Hyjt1506Draw implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String userId; // 用户ID
	private String openId; // 打开页面ID
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; // 设备类型(0: android; 1:ios; 2:browser)
	private Boolean winAward = false; // 是否中奖(0:否;1:是)
	private String awardCodeId; //奖品编码ID
	private String phone; // 手机号码
	private String ipAddr; // 客户端地址
	private Date drawTime; // 抽奖时间

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Boolean getWinAward() {
		return winAward;
	}

	public void setWinAward(Boolean winAward) {
		this.winAward = winAward;
	}

	public String getAwardCodeId() {
		return awardCodeId;
	}

	public void setAwardCodeId(String awardCodeId) {
		this.awardCodeId = awardCodeId;
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

}
