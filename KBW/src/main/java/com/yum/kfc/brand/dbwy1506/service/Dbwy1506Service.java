package com.yum.kfc.brand.dbwy1506.service;

import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Ask;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Coupon;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Open;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Share;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506User;


/**
 * @author luolix
 */
public interface Dbwy1506Service {
	
	/**
	 * 保存用户打开活动记录
	 * @param fooldayOpen
	 * @return
	 */
	public boolean saveOpen(Dbwy1506Open open);
	
	/**
	 * 获得tag的详细信息
	 * @param answerId
	 * @return
	 */
	public String getTagContent(String askId, Integer normalShare);
	
	
	/**
	 * 保存用户求友信息
	 * @param ask
	 * @return
	 */
	public boolean saveAsk(Dbwy1506Ask ask);
	
	/**
	 * 保存用户分享信息
	 * @param fooldayShare
	 * @return
	 */
	public boolean saveShare(Dbwy1506Share share);
	
	
	/**
	 * 保存半价券信息
	 * @param Dbwy1506Coupon
	 * @return
	 */
	public boolean saveCoupon(Dbwy1506Coupon coupon);
	
	/**
	 * 该分享链接是否被第其他用户第一次点击过
	 * @param shareId
	 * @return
	 */
	public boolean isShouldDrawCoupon(Integer channelType, String userId, Integer askType);	
	
	/**
	 * 保存用户信息
	 * @param Dbwy1506User
	 * @return
	 */
	public boolean saveUser(Dbwy1506User user);
	
	/**
	 * @param userId
	 * @return
	 */
	public String getNickName(String userId);
	
	
	/**
	 * 获得某一个用户所用的半价券
	 * @param userId
	 * @param channelType
	 * @return
	 */
	public String getLastCoupon(String userId, Integer channelType);
	
	
	
}
