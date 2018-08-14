package com.yum.kfc.brand.camp1504.service;

import com.yum.kfc.brand.camp1504.pojo.FooldayAnswer;
import com.yum.kfc.brand.camp1504.pojo.FooldayDraw;
import com.yum.kfc.brand.camp1504.pojo.FooldayOpen;
import com.yum.kfc.brand.camp1504.pojo.FooldayQuestion;
import com.yum.kfc.brand.camp1504.pojo.FooldayShare;


/**
 * @author luolix
 */
public interface FooldayService {
	
	/**
	 * 保存用户打开活动记录
	 * @param fooldayOpen
	 * @return
	 */
	public boolean saveOpen(FooldayOpen fooldayOpen);
	
	/**
	 * 获得一个问题
	 * @param questionId
	 * @return
	 */
	public FooldayQuestion getQuestion(String userId, String openId, String shareQuestionId, Integer recycleCount);
	
	
	/**
	 * 保存用户提交的选择记录
	 * @param fooldayAnswer
	 * @return
	 */
	public boolean saveAnswer(FooldayAnswer fooldayAnswer);
	
	/**
	 * 获得tag的详细信息
	 * @param answerId
	 * @return
	 */
	public String getTagContent(String answerId);
	
	/**
	 * 保存用户分享信息
	 * @param fooldayShare
	 * @return
	 */
	public boolean saveShare(FooldayShare fooldayShare);
	
	/**
	 * 判断当前IP是否已经中奖
	 * @param ipAddr
	 * @return
	 */
	public Long queryDrawTimesByIpToday(String ipAddr);
	
	/**
	 * 判断当前用户 当前渠道是否已经中奖
	 * @param userId
	 * @param channelType
	 * @return
	 */
	public Long queryDrawTimesByUserToday(String userId, Integer channelType);
	
	/**
	 * 判断当前用户 当前渠道是否已经中奖
	 * @param parameter
	 * @return
	 */
	public boolean queryIsUserWon(String userId, Integer channelType);
	
	/**
	 * 保存中奖记录
	 * @param param
	 * @return
	 */
	public boolean saveWinInfo(FooldayDraw fooldayDraw);
	
	
	/**
	 * 是否中奖没有填写手机号码
	 * @param param
	 * @return
	 */
	public FooldayDraw isWinNotPhone(String userId, Integer channelType);
	
	/**
	 * 保存中奖手机号码
	 * @param parameter
	 * @return
	 */
	public boolean saveWinPhone(FooldayDraw fooldayDraw);
	
	/**
	 * 改号码是否已经中奖
	 * @param phone
	 * @return
	 */
	public boolean isPhoneUsed(String phone);
	
	
}
