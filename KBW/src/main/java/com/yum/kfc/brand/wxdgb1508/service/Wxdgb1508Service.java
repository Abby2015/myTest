package com.yum.kfc.brand.wxdgb1508.service;

import java.util.List;

import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Ask;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Coupon;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Open;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Share;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508User;

/**
 * @author luolix
 */
public interface Wxdgb1508Service {
	
	/**
	 * 保存用户打开活动记录
	 * @param fooldayOpen
	 * @return
	 */
	public boolean saveOpen(Wxdgb1508Open open);
	
	/**
	 * 获得tag的详细信息
	 * @param answerId
	 * @return
	 */
	public String getTagContent(String askId, Integer normalShare);
	
	/**
	 * 获得tag的详细信息
	 * @param answerId
	 * @return
	 */
	public Wxdgb1508Ask getLoveContent(String askId);
	
	/**
	 * 是否已经发送告白
	 * @param answerId
	 * @return
	 */
	public boolean isSendLove(String userId);
	
	
	/**
	 * 保存用户求友信息
	 * @param ask
	 * @return
	 */
	public boolean saveAsk(Wxdgb1508Ask ask);
	
	/**
	 * 保存用户分享信息
	 * @param fooldayShare
	 * @return
	 */
	public boolean saveShare(Wxdgb1508Share share);
	
	
	/**
	 * 保存半价券信息
	 * @param Wxdgb1508Coupon
	 * @return
	 */
	public boolean saveCoupon(Wxdgb1508Coupon coupon);
	
	/**
	 * 该分享链接是否被第其他用户第一次点击过
	 * @param shareId
	 * @return
	 */
	public String isShouldDrawCoupon(Integer channelType, String userId);	
	
	/**
	 * 保存用户信息
	 * @param Wxdgb1508User
	 * @return
	 */
	public boolean saveUser(Wxdgb1508User user);
	
	/**
	 * @param userId
	 * @return
	 */
	public String getNickName(String userId);
	
	/**
	 * @param userId
	 * @return
	 */
	public List<Wxdgb1508User> getAddCouponUserList();
	
	/**
	 * @param userId
	 * @return
	 */
	public List<String> getMissCouponUserList();
	
	
	/**
	 * 获得某一个用户所用的半价券
	 * @param userId
	 * @param channelType
	 * @return
	 */
	public String getLastCoupon(String userId, Integer channelType);
	
}
