package com.yum.kfc.brand.szz1604.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 时装周--用户抽奖记录
 * 
 */
public class Szz1604Draw implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId; //用户ID
	private String openId; //打开页面ID
	private Integer channelType; //用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; //设备类型(0: android; 1:ios; 2:browser)
	private Integer winAward; //是否中奖(0:否;1:是)
	private Integer awardType;	//奖品类型
	private String phone; //手机号码
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
