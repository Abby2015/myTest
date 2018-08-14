package com.yum.kfc.brand.gkslz1606.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 高考胜利周活动--打开记录
 * 
 * @author Yi Dequan
 *
 */
public class Gkslz1606Open implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String openId; // 微信等系统的openid
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; )
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
