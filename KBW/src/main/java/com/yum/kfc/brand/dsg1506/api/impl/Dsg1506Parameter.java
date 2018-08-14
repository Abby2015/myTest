package com.yum.kfc.brand.dsg1506.api.impl;

import com.yum.kfc.brand.common.pojo.Parameter;

/**
 * 参数对象
 * 
 * @author luolix
 * 
 */
public class Dsg1506Parameter extends Parameter {

	private static final long serialVersionUID = 1146949984566635596L;

	private String promoCode; // 优惠券号码
	private String redirectUrl; // 回转地址
	private String firstSelect; // 第一个选择结果(A, B, C)
	private String secondSelect; // 第二个选择结果(A, B, C)
	private String thirdSelect; // 第三个选择结果(A, B, C)

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getFirstSelect() {
		return firstSelect;
	}

	public void setFirstSelect(String firstSelect) {
		this.firstSelect = firstSelect;
	}

	public String getSecondSelect() {
		return secondSelect;
	}

	public void setSecondSelect(String secondSelect) {
		this.secondSelect = secondSelect;
	}

	public String getThirdSelect() {
		return thirdSelect;
	}

	public void setThirdSelect(String thirdSelect) {
		this.thirdSelect = thirdSelect;
	}

}
