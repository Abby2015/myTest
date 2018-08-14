package com.yum.kfc.brand.luh1604.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 鹿晗2016.4 活动
 * 
 * @author yide
 *
 */
public class Luh1604User implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId; // 用户ID
	private String headImgPath; // 用户头像
	private String ipAddr; // 客户端IP
	private Date attendTime; // 参加时间
	private String token; 
	private String base64Code; //存放用户头像
	private String photoUrl;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHeadImgPath() {
		return headImgPath;
	}

	public void setHeadImgPath(String headImgPath) {
		this.headImgPath = headImgPath;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Date getAttendTime() {
		return attendTime;
	}

	public void setAttendTime(Date attendTime) {
		this.attendTime = attendTime;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getBase64Code() {
		return base64Code;
	}

	public void setBase64Code(String base64Code) {
		this.base64Code = base64Code;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

}
