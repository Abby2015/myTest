package com.yum.kfc.brand.dbwy1506.pojo;


/**
 * 微信用户
 * @author luolix
 * 
 */
public class Dbwy1506User implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;	//微信的openId
	private String nickName;	//微信昵称
	private String sex;
	private String province;
	private String city;
	
	public Dbwy1506User(){}
	
	public Dbwy1506User(String userId, String nickName, String sex, String province, String city){
		this.userId = userId;
		this.nickName = nickName;
		this.sex = sex;
		this.province = province;
		this.city = city;
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
}
