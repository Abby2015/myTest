package com.yum.kfc.brand.bmh1506.service;

import java.util.List;

import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Ask;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Coupon;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Draw;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Open;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Share;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506User;

/**
 * @author luolix
 */
public interface Bmh1506Service {
	
	/**
	 * 保存用户打开活动记录
	 * @param Bmh1506Open
	 * @return
	 */
	public boolean saveOpen(Bmh1506Open open);
	
	
	/**
	 * 保存用户求友信息
	 * @param ask
	 * @return
	 */
	public boolean saveAsk(Bmh1506Ask ask);
	
	/**
	 * 保存用户分享信息
	 * @param Bmh1506Share
	 * @return
	 */
	public boolean saveShare(Bmh1506Share share);

	
	/**
	 * 保存半价券信息
	 * @param Bmh1506Coupon
	 * @return
	 */
	public boolean saveCoupon(Bmh1506Coupon coupon);
	
	
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
	 * @param Bmh1506User
	 * @return
	 */
	public boolean saveUser(Bmh1506User user);
	
	/**
	 * @param userId
	 * @return
	 */
	public String getNickName(String userId);
	
	
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
	 * 判断当前用户 当前渠道是否已经中奖watch
	 * @param parameter
	 * @return
	 */
	public boolean queryIsWonWatch(String userId, Integer channelType, Integer awardType);
	
	/**
	 * 保存抽奖记录
	 * @param param
	 * @return
	 */
	public boolean saveDraw(Bmh1506Draw draw);
	
	
	
	/**
	 * 保存中奖信息
	 * @param parameter
	 * @return
	 */
	public boolean saveWinAward(Bmh1506Draw draw);	
	
	
	/**
	 * 改号码是否已经中奖
	 * @param phone
	 * @return
	 */
	public boolean isPhoneUsed(String phone);
	
	
	/**
	 * 判断该用户是否已经填写手机号码
	 * @param phone
	 * @return
	 */
	public boolean isUserWrited(String userId);
	
	
	/**
	 * 查询所有的用户列表
	 * @return
	 */
	public List<Bmh1506Draw> getAllNotPhoneWins();
}
