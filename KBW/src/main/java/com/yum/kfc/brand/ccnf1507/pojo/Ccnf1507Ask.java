package com.yum.kfc.brand.ccnf1507.pojo;

import java.util.Date;

/**
 * 用户求友记录
 * 
 * @author luolix
 * 
 */
public class Ccnf1507Ask implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String openId; // 打开页面ID
	private String userId; // 用户ID
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; // 设备类型(0: android; 1:ios; 2:browser)
	private Integer hitCount; // 弹进个数
	private Integer result; // 结果(0: 游戏成功，1：没券；2：游戏失败)
	private Date askTime; // 求时间

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getChannelType() {
		return channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public Integer getHitCount() {
		return hitCount;
	}

	public void setHitCount(Integer hitCount) {
		this.hitCount = hitCount;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Date getAskTime() {
		return askTime;
	}

	public void setAskTime(Date askTime) {
		this.askTime = askTime;
	}

}
