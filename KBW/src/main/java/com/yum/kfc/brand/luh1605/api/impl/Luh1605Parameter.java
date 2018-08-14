package com.yum.kfc.brand.luh1605.api.impl;

import com.yum.kfc.brand.common.pojo.Parameter;

public class Luh1605Parameter extends Parameter {
	
	private static final long serialVersionUID = 1L;
	
	private String openId; //微信openId
	private String won; // 是否抽中
	private Integer awardType = 41;	//奖品类型
	private String ibeaconSN;//iBeacon设备号

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
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

	public String getIbeaconSN() {
		return ibeaconSN;
	}

	public void setIbeaconSN(String ibeaconSN) {
		this.ibeaconSN = ibeaconSN;
	}
}
