package com.yum.kfc.brand.bnbjc1508.service;

import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Coupon;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Open;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Share;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508User;

/**
 * @author luolix
 */
public interface Bnbjc1508Service {
	
	/**
	 * 保存用户打开活动记录
	 * @param Bnbjc1508Open
	 * @return
	 */
	public boolean saveOpen(Bnbjc1508Open open);
	
	/**
	 * 保存用户分享信息
	 * @param Bnbjc1508Share
	 * @return
	 */
	public boolean saveShare(Bnbjc1508Share share);

	
	/**
	 * 保存半价券信息
	 * @param Bnbjc1508Coupon
	 * @return
	 */
	public boolean saveCoupon(Bnbjc1508Coupon coupon);
	
	
	/**
	 * 该分享链接是否被第其他用户第一次点击过
	 * @param channelType
	 * @param userId
	 * @return
	 */
	public String getTodayCoupon(Integer channelType, String userId);
	
	/**
	 * 获得某一个用户所用的半价券
	 * @param userId
	 * @param channelType
	 * @return
	 */
	public String getLastCoupon(String userId, Integer channelType);
	
	/**
	 * 判断是否应该中半价券
	 * @param userId
	 * @param channelType
	 * @return
	 */
	public boolean isDrawCoupon(String userId, Integer channelType);
	
	
	/**
	 * 保存用户信息
	 * @param Bnbjc1508User
	 * @return
	 */
	public boolean saveUser(Bnbjc1508User user);
	
	/**
	 * @param userId
	 * @return
	 */
	public String getNickName(String userId);
	
	/**
	 * 获得tag的详细信息
	 * @param answerId
	 * @return
	 */
	public String getTagContent(String openId);
	
	
}
