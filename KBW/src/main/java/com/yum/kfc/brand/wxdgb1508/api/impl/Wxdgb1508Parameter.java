package com.yum.kfc.brand.wxdgb1508.api.impl;

import com.yum.kfc.brand.common.pojo.Parameter;

/**
 * 参数对象
 * 
 * @author luolix
 * 
 */
public class Wxdgb1508Parameter extends Parameter {

	private static final long serialVersionUID = 1146949984566635596L;

	private Integer loveOption; // 非原创内容选项
	private String loveContent; // 原创内容
	private Integer normalShare; // 是否正常分享：normalShare （0：假（非正常分享）；1：真（正常分享））
	private Boolean fansCouponCome;

	public Integer getLoveOption() {
		return loveOption;
	}

	public void setLoveOption(Integer loveOption) {
		this.loveOption = loveOption;
	}

	public String getLoveContent() {
		return loveContent;
	}

	public void setLoveContent(String loveContent) {
		this.loveContent = loveContent;
	}

	public Integer getNormalShare() {
		return normalShare;
	}

	public void setNormalShare(Integer normalShare) {
		this.normalShare = normalShare;
	}

	public Boolean getFansCouponCome() {
		return fansCouponCome;
	}

	public void setFansCouponCome(Boolean fansCouponCome) {
		this.fansCouponCome = fansCouponCome;
	}
	
	

}
