package com.yum.kfc.brand.bjmls1609.pojo;

import java.io.Serializable;
import java.util.Date;

public class Bjmls1609Attend implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String sid; // 打开页面ID(open表的id)
	private String userId;
	private String token;
	private String openId; // 微信等系统的openid
	private Integer channelType; // 用户渠道(0: supperApp; 1: 微信; 2：支付宝; 3:其他 )
	private String deviceType; // 设备类型
	private String deviceId; // 设备号
	private String ipAddr; // 客户端IP
	private Date attendTime; // 参加时间
	private String activityId; //活动ID
	private String tags; //提交的答案

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Date getAttendTime() {
		return attendTime;
	}

	public void setAttendTime(Date attendTime) {
		this.attendTime = attendTime;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

}
