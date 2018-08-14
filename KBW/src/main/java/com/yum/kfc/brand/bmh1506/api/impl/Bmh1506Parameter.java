package com.yum.kfc.brand.bmh1506.api.impl;

import com.yum.kfc.brand.common.pojo.Parameter;

/**
 * 参数对象
 * 
 * @author luolix
 * 
 */
public class Bmh1506Parameter extends Parameter {

	private static final long serialVersionUID = 1146949984566635596L;

	private Integer hitCount; // 击中个数
	private String promoCode; // 优惠券号码
	private String redirectUrl; //回转地址
	private Integer awardType;	//奖品类型
	private String name;
	private String address;
	
	

	public Integer getAwardType() {
		return awardType;
	}

	public void setAwardType(Integer awardType) {
		this.awardType = awardType;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Integer getHitCount() {
		return hitCount;
	}

	public void setHitCount(Integer hitCount) {
		this.hitCount = hitCount;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	

}
