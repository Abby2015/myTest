package com.yum.kfc.brand.camp1504.api.impl;

import com.yum.kfc.brand.common.pojo.Parameter;

/**
 * 参数对象
 * 
 * @author luolix
 * 
 */
public class Camp1504Parameter extends Parameter {

	private static final long serialVersionUID = 1146949984566635596L;

	private Integer isCorrect; // 用户是否答对(0：未答对，1：答对， -1：未做选择)
	private String questionId; // 问题ID
	private Integer choice; // choice:1靠谱，0不靠谱，-1未选择
	private String phone;
	private String won; // 是否抽中
	private Integer recycleCount; // 问题轮次
	private String redirectUrl;

	public Integer getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Integer isCorrect) {
		this.isCorrect = isCorrect;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public Integer getChoice() {
		return choice;
	}

	public void setChoice(Integer choice) {
		this.choice = choice;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWon() {
		return won;
	}

	public void setWon(String won) {
		this.won = won;
	}

	public Integer getRecycleCount() {
		return recycleCount;
	}

	public void setRecycleCount(Integer recycleCount) {
		this.recycleCount = recycleCount;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

}
