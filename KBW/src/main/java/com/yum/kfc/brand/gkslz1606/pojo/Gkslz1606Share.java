package com.yum.kfc.brand.gkslz1606.pojo;

import java.io.Serializable;
import java.util.Date;

public class Gkslz1606Share implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String sid; // 打开页面ID(open表的id)
	private String openId; // 微信等系统的openid
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; )
	private Integer deviceType; // 设备类型(0: android; 1:ios; )
	private String deviceId; // 设备号
	private String ipAddr; // 客户端IP
	private Date shareTime; // 打开时间
	private Integer shareResult; // 分享结果：1：成功，0：失败
	private String shareUrl;
	private String mediaType; // 分享媒介(QQ|WX|TWB|WB|RR|DB|TB|ZFB)

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
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

	public Date getShareTime() {
		return shareTime;
	}

	public void setShareTime(Date shareTime) {
		this.shareTime = shareTime;
	}

	public Integer getShareResult() {
		return shareResult;
	}

	public void setShareResult(Integer shareResult) {
		this.shareResult = shareResult;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

}
