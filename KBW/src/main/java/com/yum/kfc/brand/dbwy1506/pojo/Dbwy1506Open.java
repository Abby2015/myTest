package com.yum.kfc.brand.dbwy1506.pojo;

import java.util.Date;

/**
 * 打开记录
 * @author luolix
 *
 */
public class Dbwy1506Open implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String userId; // 用户ID
	private String inviterId; // 发起人ID
	private Integer channelType; // 用户渠道(0: Brand App; 1: 微信;)
	private String deviceId; // 设备号
	private Integer deviceType; // 设备类型(0: android; 1:ios; )
	private String token; // 访问的token
	private Integer inviteChannelType; // 分享渠道类型(0: Brand App; 1: 微信;)
	private String tagContent; // 加密内容
	private String ipAddr; // 客户端IP
	private Date openTime; // 打开时间
	
	//临时变量：默认的求方式
	private Integer askType;
	

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

	public String getTagContent() {
		return tagContent;
	}

	public void setTagContent(String tagContent) {
		this.tagContent = tagContent;
	}

	public Integer getAskType() {
		return askType;
	}

	public void setAskType(Integer askType) {
		this.askType = askType;
	}
	
	
	
	
}
