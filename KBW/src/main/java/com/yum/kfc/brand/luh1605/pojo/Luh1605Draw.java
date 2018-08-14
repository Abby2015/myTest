package com.yum.kfc.brand.luh1605.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 鹿晗520--用户抽奖记录
 * 
 */
public class Luh1605Draw implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId; //用户ID
	private String openId; //打开页面ID(open表的id)
	private String wxOpenId; //微信openid
	private Integer channelType; //用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; //设备类型(0: android; 1:ios; 2:browser)
	private String deviceId; // 设备号
	private String ibeaconSN; // ibeacon设备号
	private Integer winAward; //是否中奖(0:否;1:是)
	private Integer awardType;	//奖品类型
	private String phone; //手机号码
	private String ipAddr; // 客户端IP
	private Date drawTime; //抽奖时间
	private String awardCodeId; //奖品编码ID

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

	public String getWxOpenId() {
		return wxOpenId;
	}

	public void setWxOpenId(String wxOpenId) {
		this.wxOpenId = wxOpenId;
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

	public String getIbeaconSN() {
		return ibeaconSN;
	}

	public void setIbeaconSN(String ibeaconSN) {
		this.ibeaconSN = ibeaconSN;
	}

	public Integer getWinAward() {
		return winAward;
	}

	public void setWinAward(Integer winAward) {
		this.winAward = winAward;
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

	public Integer getAwardType() {
		return awardType;
	}

	public void setAwardType(Integer awardType) {
		this.awardType = awardType;
	}

	public String getAwardCodeId() {
		return awardCodeId;
	}

	public void setAwardCodeId(String awardCodeId) {
		this.awardCodeId = awardCodeId;
	}

}
