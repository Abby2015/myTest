package com.yum.kfc.brand.camp1504.pojo;


/**
 * 愚人节活动--问题
 * 
 * @author luolix
 * 
 */
public class FooldayQuestion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // ID
	private String code; // 问题编号
	private String desc; // 问题描述
	private Integer correct; // 正确答案(0:不靠谱; 1:靠谱)
	private Long yesCount; // 选择靠谱人次
	private Long noCount; // 选择不靠谱次次
	private Long naCount; // 放弃选择的人次
	
	
	//临时变量:人次占有百分比
	private Integer yesPercent;
	private Integer noPercent;
	private Integer naPercent;
	private Integer recycleCount;	//问题轮次

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getCorrect() {
		return correct;
	}

	public void setCorrect(Integer correct) {
		this.correct = correct;
	}

	public Long getYesCount() {
		return yesCount;
	}

	public void setYesCount(Long yesCount) {
		this.yesCount = yesCount;
	}

	public Long getNoCount() {
		return noCount;
	}

	public void setNoCount(Long noCount) {
		this.noCount = noCount;
	}

	public Long getNaCount() {
		return naCount;
	}

	public void setNaCount(Long naCount) {
		this.naCount = naCount;
	}

	public Integer getYesPercent() {
		return yesPercent;
	}

	public void setYesPercent(Integer yesPercent) {
		this.yesPercent = yesPercent;
	}

	public Integer getNoPercent() {
		return noPercent;
	}

	public void setNoPercent(Integer noPercent) {
		this.noPercent = noPercent;
	}

	public Integer getNaPercent() {
		return naPercent;
	}

	public void setNaPercent(Integer naPercent) {
		this.naPercent = naPercent;
	}

	public Integer getRecycleCount() {
		return recycleCount;
	}

	public void setRecycleCount(Integer recycleCount) {
		this.recycleCount = recycleCount;
	}


}
