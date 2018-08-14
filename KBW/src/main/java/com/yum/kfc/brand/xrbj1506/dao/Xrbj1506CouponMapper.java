package com.yum.kfc.brand.xrbj1506.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.xrbj1506.pojo.CouponTotal;
import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506Coupon;


public interface Xrbj1506CouponMapper {
	
	public int insert(Xrbj1506Coupon coupon);
	
	public void batchInsert(@Param(value = "couponList") List<Xrbj1506Coupon> couponList);
	
	public int getValidCount(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="askType") Integer askType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	
	public int getValidCountBak(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="askType") Integer askType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	
	public List<CouponTotal> getAllCoupons(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	
	public String getMyCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	
}
