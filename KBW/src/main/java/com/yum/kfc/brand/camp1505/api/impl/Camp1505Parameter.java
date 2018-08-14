package com.yum.kfc.brand.camp1505.api.impl;

import java.util.Date;

import com.yum.kfc.brand.common.pojo.Parameter;

/**
 * 参数对象
 * 
 * @author luolix
 * 
 */
public class Camp1505Parameter extends Parameter {

	private static final long serialVersionUID = 1146949984566635596L;

	private Integer awardType;	//奖品类型
	private Integer tasteOption; // 尝新选项(share)
	private String otherContent; // 另外的内容
	private String name; // 姓名(darw)
	private String identityNum; // 身份证号码
	private String address; // 地址
	private Date postCode; // 邮政编码
	private String redirectUrl; //回转地址
	
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Integer getTasteOption() {
		return tasteOption;
	}

	public void setTasteOption(Integer tasteOption) {
		this.tasteOption = tasteOption;
	}

	public String getOtherContent() {
		return otherContent;
	}

	public void setOtherContent(String otherContent) {
		this.otherContent = otherContent;
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
