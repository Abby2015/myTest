package com.yum.kfc.brand.gkslz1606.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 高考胜利周--抽奖记录
 * 
 * @author Yi Dequan
 *
 */
public class Gkslz1606Draw implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String userId; // 用户ID
	private String sid; // 打开页面ID(open表的id)
	private String openId; // 微信openid
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; // 设备类型(0: android; 1:ios; 2:browser)
	private String deviceId; // 设备号
	private Integer winAward; // 是否中奖(0:否;1:是)
	private Integer awardType; // 奖品类型
	private String phone; // 手机号码
	private String ipAddr; // 客户端IP
	private Date drawTime; // 抽奖时间
	private String awardCodeId; // 奖品编码ID

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

	public String getAwardCodeId() {
		return awardCodeId;
	}

	public void setAwardCodeId(String awardCodeId) {
		this.awardCodeId = awardCodeId;
	}
}
