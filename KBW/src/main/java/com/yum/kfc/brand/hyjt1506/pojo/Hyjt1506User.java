package com.yum.kfc.brand.hyjt1506.pojo;

import java.util.Date;


/**
 * 微信用户
 * @author luolix
 * 
 */
public class Hyjt1506User implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;	//微信的openId
	private String nickName;	//微信昵称
	private String sex;	//性别（0,1）
	private String province;	//省份
	private String city;	//城市
	private Date firstTime;	//第一次打开活动使用的时间
	
	public Hyjt1506User(){}
	
	public Hyjt1506User(String userId, String nickName, String sex, String province, String city, Date firstTime){
		this.userId = userId;
		this.nickName = nickName;
		this.sex = sex;
		this.province = province;
		this.city = city;
		this.firstTime = firstTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}
	
}
