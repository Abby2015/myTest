package com.yum.kfc.brand.dzp1807.pojo;

import com.yum.kfc.brand.common.pojo.Parameter;

public class Dzp1807Parameter extends Parameter {
	private static final long serialVersionUID = -2738273215473526151L;
	
	private String ssoUserId;
	private String crmUserCode;
	private boolean won; // 是否抽中
	private Integer awardType; // 奖品类型
	private Integer awardLevel; // 奖品级别
	private String awardName; // 奖品名称
	private String winImg; // 中奖大图
	private String personalImg; // 中奖小图
	private boolean frCrm; // 奖品来源
	private String awardCode; // 奖品code
	private String activityId;
	private Integer couponType; //优惠券类型
	private String url;	

	public String getSsoUserId() {
		return ssoUserId;
	}

	public void setSsoUserId(String ssoUserId) {
		this.ssoUserId = ssoUserId;
	}

	public String getCrmUserCode() {
		return crmUserCode;
	}

	public void setCrmUserCode(String crmUserCode) {
		this.crmUserCode = crmUserCode;
	}

	public boolean isWon() {
		return won;
	}

	public void setWon(boolean won) {
		this.won = won;
	}

	public Integer getAwardType() {
		return awardType;
	}

	public void setAwardType(Integer awardType) {
		this.awardType = awardType;
	}

	public Integer getAwardLevel() {
		return awardLevel;
	}

	public void setAwardLevel(Integer awardLevel) {
		this.awardLevel = awardLevel;
	}

	public String getAwardName() {
		return awardName;
	}

	public void setAwardName(String awardName) {
		this.awardName = awardName;
	}

	public String getWinImg() {
		return winImg;
	}

	public void setWinImg(String winImg) {
		this.winImg = winImg;
	}

	public String getPersonalImg() {
		return personalImg;
	}

	public void setPersonalImg(String personalImg) {
		this.personalImg = personalImg;
	}

	public boolean isFrCrm() {
		return frCrm;
	}

	public void setFrCrm(boolean frCrm) {
		this.frCrm = frCrm;
	}

	public String getAwardCode() {
		return awardCode;
	}

	public void setAwardCode(String awardCode) {
		this.awardCode = awardCode;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
