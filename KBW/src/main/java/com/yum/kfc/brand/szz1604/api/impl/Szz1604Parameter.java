package com.yum.kfc.brand.szz1604.api.impl;

import com.yum.kfc.brand.common.pojo.Parameter;

public class Szz1604Parameter extends Parameter {
	
	private static final long serialVersionUID = 1L;
	
	private String openId; //微信openId
	private String storeCode; //门店 
	private String won; // 是否抽中
	private Integer awardType;	//奖品类型

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getWon() {
		return won;
	}

	public void setWon(String won) {
		this.won = won;
	}

	public Integer getAwardType() {
		return awardType;
	}

	public void setAwardType(Integer awardType) {
		this.awardType = awardType;
	}
}
