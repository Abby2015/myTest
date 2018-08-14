package com.yum.kfc.brand.wxdgb1508.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Coupon;


public interface Wxdgb1508CouponMapper {
	
	public int insert(Wxdgb1508Coupon coupon);
	
	public String getTodayCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	
	public String getLastCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	
	public List<Wxdgb1508Coupon> isDrawCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	

	
}
