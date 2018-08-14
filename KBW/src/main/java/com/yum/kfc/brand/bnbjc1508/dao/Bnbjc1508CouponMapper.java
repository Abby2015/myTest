package com.yum.kfc.brand.bnbjc1508.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Coupon;


public interface Bnbjc1508CouponMapper {
	
	public int insert(Bnbjc1508Coupon coupon);
	
	public String getTodayCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	
	public String getLastCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	
	public int isDrawCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	

	
}
