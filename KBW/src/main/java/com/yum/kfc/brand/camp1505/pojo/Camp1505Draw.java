package com.yum.kfc.brand.camp1505.pojo;

import java.util.Date;

/**
 * 母亲节--用户抽奖记录
 * @author luolix
 * 
 */
public class Camp1505Draw implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; //ID
	private String userId; //用户ID
	private String openId; //打开页面ID
	private Integer channelType; //用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; //设备类型(0: android; 1:ios; 2:browser)
	private Integer winAward; //是否中奖(0:否;1:是)
	private Integer awardType;	//奖品类型
	private String awardCodeId; //奖品编码ID
	private String name; //姓名
	private String phone; //手机号码
	private String identityNum; //身份证号码
	private String address; //地址
	private Date postCode; //邮政编码
	private String ipAddr; //客户端地址
	private Date drawTime; //抽奖时间

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

	public Integer getWinAward() {
		return winAward;
	}

	public void setWinAward(Integer winAward) {
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

	public Date getDrawTime() {
		return drawTime;
	}

	public void setDrawTime(Date drawTime) {
		this.drawTime = drawTime;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentityNum() {
		return identityNum;
	}

	public void setIdentityNum(String identityNum) {
		this.identityNum = identityNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getPostCode() {
		return postCode;
	}

	public void setPostCode(Date postCode) {
		this.postCode = postCode;
	}

	public Integer getAwardType() {
		return awardType;
	}

	public void setAwardType(Integer awardType) {
		this.awardType = awardType;
	}

}
