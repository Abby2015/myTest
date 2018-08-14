package com.yum.kfc.brand.dzp1807.pojo;

/**
 * 
 * @author yidequan@cloudwalk.cn 2018年7月12日下午7:52:01
 *
 */
public class Dzp1807Barrage {
	private String nickname; // 用户昵称
	private boolean winAward; // 是否中奖
	private Integer awardLevel; // 奖品级别
	private String awardName; // 奖品名称

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getAwardLevel() {
		return awardLevel;
	}

	public void setAwardLevel(Integer awardLevel) {
		this.awardLevel = awardLevel;
	}

	public String getAwardName() {
		return awardName;
	}

	public void setAwardName(String awardName) {
		this.awardName = awardName;
	}

	public boolean isWinAward() {
		return winAward;
	}

	public void setWinAward(boolean winAward) {
		this.winAward = winAward;
	}

}
