package com.yum.kfc.brand.luh1604.pojo;

import java.io.Serializable;
import java.util.Date;

public class Luh1604Open implements Serializable{
	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String userId; // 用户ID
	private Integer channelType; // 用户渠道(0: Supper App; 1: 微信; )
	private String deviceId; // 设备号
	private Integer deviceType; // 设备类型(0: android; 1:ios; )
	private String token; // 访问的token
	private String ipAddr; // 客户端IP
	private Date openTime; // 打开时间

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

	public Integer getChannelType() {
		return channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

}
