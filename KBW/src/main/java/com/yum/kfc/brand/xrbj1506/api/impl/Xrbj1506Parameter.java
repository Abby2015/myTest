package com.yum.kfc.brand.xrbj1506.api.impl;

import com.yum.kfc.brand.common.pojo.Parameter;

/**
 * 参数对象
 * 
 * @author luolix
 * 
 */
public class Xrbj1506Parameter extends Parameter {

	private static final long serialVersionUID = 1146949984566635596L;

	private Integer count;	//选择优惠券个数
	private Integer askType;	//求类型（0:求闺蜜；1：求ji友）
	private Integer menuType;	//产品类型(0：冰激凌；1：凉茶)
	private String redirectUrl; //回转地址
	private Integer normalShare;	//是否正常分享：normalShare （0：假（非正常分享）；1：真（正常分享））
	private String promoCode;	//半价券卡号

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Integer getAskType() {
		return askType;
	}

	public void setAskType(Integer askType) {
		this.askType = askType;
	}

	public Integer getMenuType() {
		return menuType;
	}

	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getNormalShare() {
		return normalShare;
	}

	public void setNormalShare(Integer normalShare) {
		this.normalShare = normalShare;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}
	
	
	
	
}
