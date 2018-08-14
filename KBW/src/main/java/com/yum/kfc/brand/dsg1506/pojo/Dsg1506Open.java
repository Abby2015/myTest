package com.yum.kfc.brand.dsg1506.pojo;

import java.util.Date;

/**
 * 打开记录
 * @author luolix
 *
 */
public class Dsg1506Open implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String userId; // 用户ID
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信;)
	private Integer deviceType; // 设备类型(0: android; 1:ios; )
	private String deviceId; // 设备号
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
