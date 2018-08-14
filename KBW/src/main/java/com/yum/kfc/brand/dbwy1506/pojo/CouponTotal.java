package com.yum.kfc.brand.dbwy1506.pojo;

import java.util.Date;

/**
 * 我的半价券
 * @author luolix
 *
 */
public class CouponTotal {

	private String inviterName; // 跟谁拼
	private Integer askType; // 求友记录(0:求闺蜜；1：求ji友)
	private String promoCode; // 微信卡券序号
	private Date couponTime; // 领取时间

	public String getInviterName() {
		return inviterName;
	}

	public void setInviterName(String inviterName) {
		this.inviterName = inviterName;
	}

	public Integer getAskType() {
		return askType;
	}

	public void setAskType(Integer askType) {
		this.askType = askType;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public Date getCouponTime() {
		return couponTime;
	}

	public void setCouponTime(Date couponTime) {
		this.couponTime = couponTime;
	}

}
