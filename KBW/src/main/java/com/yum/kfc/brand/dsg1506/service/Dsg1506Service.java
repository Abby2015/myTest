package com.yum.kfc.brand.dsg1506.service;

import java.util.List;

import com.yum.kfc.brand.dsg1506.pojo.Dsg1506Ask;
import com.yum.kfc.brand.dsg1506.pojo.Dsg1506Coupon;
import com.yum.kfc.brand.dsg1506.pojo.Dsg1506Open;
import com.yum.kfc.brand.dsg1506.pojo.Dsg1506Share;
import com.yum.kfc.brand.dsg1506.pojo.Dsg1506User;

/**
 * @author luolix
 */
public interface Dsg1506Service {
	
	/**
	 * 保存用户打开活动记录
	 * @param Dsg1506Open
	 * @return
	 */
	public boolean saveOpen(Dsg1506Open open);
	
	
	/**
	 * 保存用户求友信息
	 * @param ask
	 * @return
	 */
	public boolean saveAsk(Dsg1506Ask ask);
	
	/**
	 * 保存用户分享信息
	 * @param Dsg1506Share
	 * @return
	 */
	public boolean saveShare(Dsg1506Share share);

	
	/**
	 * 保存半价券信息
	 * @param Dsg1506Coupon
	 * @return
	 */
	public boolean saveCoupon(Dsg1506Coupon coupon);
	
	
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
	 * @param Dsg1506User
	 * @return
	 */
	public boolean saveUser(Dsg1506User user);
	
	/**
	 * @param userId
	 * @return
	 */
	public String getNickName(String userId);

}
