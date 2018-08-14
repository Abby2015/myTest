package com.yum.kfc.brand.bmh1506.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Coupon;


public interface Bmh1506CouponMapper {
	
	public int insert(Bmh1506Coupon coupon);
	
	public String getValidCount(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	
	public List<String> getAllCoupons(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	
	public String getLastCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	

	
}
