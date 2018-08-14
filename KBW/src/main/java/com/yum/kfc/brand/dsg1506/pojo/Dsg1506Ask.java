package com.yum.kfc.brand.dsg1506.pojo;

import java.util.Date;

/**
 * 用户求友记录
 * 
 * @author luolix
 * 
 */
public class Dsg1506Ask implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String openId; // 打开页面ID
	private String userId; // 用户ID
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private Integer deviceType; // 设备类型(0: android; 1:ios; 2:browser)
	private Integer result;	//(0:获取半价券成功, 1:无券(该用户当天数据库中没有，费睿接口和获取不到))
	private String firstSelect; // 第一个选择结果(A, B, C)
	private String secondSelect; // 第二个选择结果(A, B, C)
	private String thirdSelect; // 第三个选择结果(A, B, C)
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

	public Date getAskTime() {
		return askTime;
	}

	public void setAskTime(Date askTime) {
		this.askTime = askTime;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}
	
	

}
