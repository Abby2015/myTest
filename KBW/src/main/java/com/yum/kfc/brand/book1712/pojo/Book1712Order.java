package com.yum.kfc.brand.book1712.pojo;

import java.io.Serializable;
import java.util.Date;

public class Book1712Order implements Serializable {

	private static final long serialVersionUID = 1296447407787686267L;
	
	private String orderId;
	private Date createTime;
	private String status;
	private String crmUserCode;
	private String brandUserId;
	private String ssoUserId;
	private String phone;
	private String openId;
	private String unionId;
	private String nickname;
	private String headImg;
	private String activityId;
	private String categoryName;
	private int count;
	private String payChannel;
	private String subTotal; // 折扣前总金额
	private String grandTotal; // 折扣后总金额
	private String payType; // 支付方式
	private String payUrl; // 支付连接
	private String order;
	private String order2;
	private Date updateTime;
	private String status2;
	private String fromShareId;
	private boolean isValid=true;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCrmUserCode() {
		return crmUserCode;
	}

	public void setCrmUserCode(String crmUserCode) {
		this.crmUserCode = crmUserCode;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}

	public String getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(String grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getOrder2() {
		return order2;
	}

	public void setOrder2(String order2) {
		this.order2 = order2;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getStatus2() {
		return status2;
	}

	public void setStatus2(String status2) {
		this.status2 = status2;
	}

	public String getFromShareId() {
		return fromShareId;
	}

	public void setFromShareId(String fromShareId) {
		this.fromShareId = fromShareId;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
}
