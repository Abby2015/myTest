package com.yum.kfc.brand.book1712.pojo;

import java.io.Serializable;
import java.util.Date;

public class Book1712Share implements Serializable {
	
	private static final long serialVersionUID = -4537998941551406218L;
	
	private String orderId;
	private Date createTime;
	private String openId;
	private String unionId;
	private String nickname;
	private String headImg;
	private String brandUserId;
	private String ssoUserId;
	private String crmUserCode;
	private String phone;
	private String shareId;
	private boolean isSharer;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getBrandUserId() {
		return brandUserId;
	}

	public void setBrandUserId(String brandUserId) {
		this.brandUserId = brandUserId;
	}

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public boolean isSharer() {
		return isSharer;
	}

	public void setSharer(boolean isSharer) {
		this.isSharer = isSharer;
	}

}
