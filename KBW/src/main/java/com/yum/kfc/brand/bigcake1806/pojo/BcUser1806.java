package com.yum.kfc.brand.bigcake1806.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author yidequan@cloudwalk.cn 2018年5月28日上午11:39:34
 *
 */
public class BcUser1806 implements Serializable {
	private static final long serialVersionUID = 2916346879240363723L;
	
	private String userCode;
	private String phone;
	private String storeCode;
	private String storeName;
	private String cityName;
	private Date consumeTime;

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Date getConsumeTime() {
		return consumeTime;
	}

	public void setConsumeTime(Date consumeTime) {
		this.consumeTime = consumeTime;
	}

}
