package com.yum.kfc.brand.hyjt1506.service;

import java.util.List;

import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Ask;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Coupon;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Draw;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Open;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Share;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506User;

/**
 * @author luolix
 */
public interface Hyjt1506Service {
	
	/**
	 * 保存用户打开活动记录
	 * @param Bnbjc1508Open
	 * @return
	 */
	public boolean saveOpen(Hyjt1506Open open);
	
	/**
	 * 保存用户分享信息
	 * @param Bnbjc1508Share
	 * @return
	 */
	public boolean saveShare(Hyjt1506Share share);

	
	/**
	 * 保存半价券信息
	 * @param Bnbjc1508Coupon
	 * @return
	 */
	public boolean saveCoupon(Hyjt1506Coupon coupon);
	
	
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
	 * 保存用户信息
	 * @param Bnbjc1508User
	 * @return
	 */
	public boolean saveUser(Hyjt1506User user);
	
	/**
	 * @param userId
	 * @return
	 */
	public String getNickName(String userId);
	
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
	public boolean saveDraw(Hyjt1506Draw draw);
	
	
	/**
	 * 保存中奖信息
	 * @param parameter
	 * @return
	 */
	public boolean saveWinAward(Hyjt1506Draw draw);	
	
	
	/**
	 * 该号码是否已经中奖
	 * @param phone
	 * @return
	 */
	public boolean isPhoneUsed(String phone);
	
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
	public boolean saveAsk(Hyjt1506Ask ask);
	
	/**
	 * 
	 * @param ask
	 * @return
	 */
	public List<Hyjt1506Draw> queryWinUsers();
	
	/**
	 * @param ask
	 * @return
	 */
	public boolean queryWinNotPhone(String userId);
}
