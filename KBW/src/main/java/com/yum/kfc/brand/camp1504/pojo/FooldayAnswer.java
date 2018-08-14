package com.yum.kfc.brand.camp1504.pojo;

import java.util.Date;

/**
 * 愚人节活动--用户提交答案
 * 
 * @author luolix
 * 
 */
public class FooldayAnswer implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String openId; // 打开页面ID
	private String userId; // 受邀人ID
	private String questionId; // 问题ID
	private Integer isCorrect;	//选择是否正确（1：正确，0：不正确）
	private Integer choice; // 选择(1靠谱，0不靠谱，-1未选择)
	private Date answerTime; // 响应时间
	private String tagContent; // tag内容
	private Integer recycleCount;	//循环轮次

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

	public Date getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(Date answerTime) {
		this.answerTime = answerTime;
	}

	public String getTagContent() {
		return tagContent;
	}

	public void setTagContent(String tagContent) {
		this.tagContent = tagContent;
	}

	public Integer getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Integer isCorrect) {
		this.isCorrect = isCorrect;
	}

	public Integer getRecycleCount() {
		return recycleCount;
	}

	public void setRecycleCount(Integer recycleCount) {
		this.recycleCount = recycleCount;
	}
	
	
}
