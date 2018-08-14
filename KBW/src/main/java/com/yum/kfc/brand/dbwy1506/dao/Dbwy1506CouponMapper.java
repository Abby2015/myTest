package com.yum.kfc.brand.dbwy1506.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Coupon;


public interface Dbwy1506CouponMapper {
	
	public int insert(Dbwy1506Coupon coupon);
	
	public int getValidCount(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="askType") Integer askType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	
	
	public String getLastCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	
}
