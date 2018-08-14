package com.yum.kfc.brand.camp1505.service;

import com.yum.kfc.brand.camp1505.pojo.Camp1505Draw;
import com.yum.kfc.brand.camp1505.pojo.Camp1505Open;
import com.yum.kfc.brand.camp1505.pojo.Camp1505Share;


/**
 * @author luolix
 */
public interface Camp1505Service {
	
	/**
	 * 保存用户打开活动记录
	 * @param fooldayOpen
	 * @return
	 */
	public boolean saveOpen(Camp1505Open open);
	
	/**
	 * 获得tag的详细信息
	 * @param answerId
	 * @return
	 */
	public String getTagContent(String shareId);
	
	/**
	 * 保存用户分享信息
	 * @param fooldayShare
	 * @return
	 */
	public boolean saveShare(Camp1505Share share);
	
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
	 * 保存抽奖记录
	 * @param param
	 * @return
	 */
	public boolean saveDraw(Camp1505Draw draw);
	
	
	
	/**
	 * 保存中奖信息
	 * @param parameter
	 * @return
	 */
	public boolean saveWinAward(Camp1505Draw draw);	
	
}
