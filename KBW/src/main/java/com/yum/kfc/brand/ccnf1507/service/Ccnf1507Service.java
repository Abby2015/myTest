package com.yum.kfc.brand.ccnf1507.service;

import java.util.List;

import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507Ask;
import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507Coupon;
import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507Open;
import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507Share;
import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507User;

/**
 * @author luolix
 */
public interface Ccnf1507Service {
	
	/**
	 * 保存用户打开活动记录
	 * @param Ccnf1507Open
	 * @return
	 */
	public boolean saveOpen(Ccnf1507Open open);
	
	
	/**
	 * 保存用户求友信息
	 * @param ask
	 * @return
	 */
	public boolean saveAsk(Ccnf1507Ask ask);
	
	/**
	 * 保存用户分享信息
	 * @param Ccnf1507Share
	 * @return
	 */
	public boolean saveShare(Ccnf1507Share share);

	
	/**
	 * 保存半价券信息
	 * @param Ccnf1507Coupon
	 * @return
	 */
	public boolean saveCoupon(Ccnf1507Coupon coupon);
	
	
	/**
	 * 该分享链接是否被第其他用户第一次点击过
	 * @param channelType
	 * @param userId
	 * @return
	 */
	public String isShouldDrawCoupon(Integer channelType, String userId);
	
	
	/**
	 * 获得某一个用户所用的半价券
	 * @param userId
	 * @param channelType
	 * @return
	 */
	public List<String> getAllCoupons(String userId, Integer channelType);
	
	/**
	 * 获得某一个用户所用的半价券
	 * @param userId
	 * @param channelType
	 * @return
	 */
	public String getLastCoupon(String userId, Integer channelType);
	
	
	/**
	 * 保存用户信息
	 * @param Ccnf1507User
	 * @return
	 */
	public boolean saveUser(Ccnf1507User user);
	
	/**
	 * @param userId
	 * @return
	 */
	public String getNickName(String userId);
	
	
	
	
}
