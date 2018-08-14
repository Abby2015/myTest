package com.yum.kfc.brand.xrbj1506.service;

import java.util.List;

import com.yum.kfc.brand.xrbj1506.pojo.CouponTotal;
import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506Ask;
import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506Coupon;
import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506Open;
import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506Share;
import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506User;


/**
 * @author luolix
 */
public interface Xrbj1506Service {
	
	/**
	 * 保存用户打开活动记录
	 * @param fooldayOpen
	 * @return
	 */
	public boolean saveOpen(Xrbj1506Open open);
	
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
	public boolean saveAsk(Xrbj1506Ask ask);
	
	/**
	 * 保存用户分享信息
	 * @param fooldayShare
	 * @return
	 */
	public boolean saveShare(Xrbj1506Share share);

	
	/**
	 * 保存半价券信息
	 * @param Xrbj1506Coupon
	 * @return
	 */
	public boolean saveCouponList(List<Xrbj1506Coupon> couponList);
	
	
	/**
	 * 保存半价券信息
	 * @param Xrbj1506Coupon
	 * @return
	 */
	public boolean saveCoupon(Xrbj1506Coupon coupon);
	
	/**
	 * 该分享链接是否被第其他用户第一次点击过
	 * @param shareId
	 * @return
	 */
	public boolean isShouldDrawCoupon(Integer channelType, String userId, Integer askType);
	
	
	/**
	 * 获得我最近的微信卡券号
	 * @param userId
	 * @param menuType
	 * @return String
	 */
	public String getMyCoupon(String userId, Integer channelType);
	
	
	/**
	 * 获得某一个用户所用的半价券
	 * @param userId
	 * @param channelType
	 * @return
	 */
	public List<CouponTotal> getAllCoupons(String userId, Integer channelType);
	
	
	/**
	 * 保存用户信息
	 * @param Xrbj1506User
	 * @return
	 */
	public boolean saveUser(Xrbj1506User user);
	
	/**
	 * @param userId
	 * @return
	 */
	public String getNickName(String userId);
	
	
	
}
