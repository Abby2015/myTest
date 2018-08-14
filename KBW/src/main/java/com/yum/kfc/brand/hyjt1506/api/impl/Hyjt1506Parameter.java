package com.yum.kfc.brand.hyjt1506.api.impl;

import com.yum.kfc.brand.common.pojo.Parameter;

/**
 * 参数对象
 * 
 * @author luolix
 * 
 */
public class Hyjt1506Parameter extends Parameter {

	private static final long serialVersionUID = 1146949984566635596L;

	private Boolean guessRight; // 是否成功猜中
	private Integer normalShare; // 是否正常分享：normalShare （0：假（非正常分享）；1：真（正常分享））
	private String name;
	private String address;

	public Boolean getGuessRight() {
		return guessRight;
	}

	public void setGuessRight(Boolean guessRight) {
		this.guessRight = guessRight;
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

	public Integer getNormalShare() {
		return normalShare;
	}

	public void setNormalShare(Integer normalShare) {
		this.normalShare = normalShare;
	}

}
