package com.yum.kfc.brand.camp1504.pojo;

import java.util.Date;

/**
 * 愚人节活动--打开记录
 * @author luolix
 *
 */
public class FooldayOpen implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String userId; // 用户ID
	private String inviterId; // 发起人ID
	private String nickName; // 昵称
	private String age; // 年龄
	private String gender; // 性别
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	private String deviceId; // 设备号
	private Integer deviceType; // 设备类型(0: android; 1:ios; 2:browser)
	private String token; // 访问的token
	private Integer inviteChannelType; // 分享渠道类型(0: Brand App; 1: 微信; 2:浏览器)
	private String ipAddr; // 客户端IP
	private Date openTime; // 打开时间
	
	//临时变量：tag键
	private String tag;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInviterId() {
		return inviterId;
	}

	public void setInviterId(String inviterId) {
		this.inviterId = inviterId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getChannelType() {
		return channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getInviteChannelType() {
		return inviteChannelType;
	}

	public void setInviteChannelType(Integer inviteChannelType) {
		this.inviteChannelType = inviteChannelType;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
