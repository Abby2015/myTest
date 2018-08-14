package com.yum.kfc.brand.hyjt1506.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Coupon;


public interface Hyjt1506CouponMapper {
	
	public int insert(Hyjt1506Coupon coupon);
	
	public String getTodayCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	
	public String getLastCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	

	
}
