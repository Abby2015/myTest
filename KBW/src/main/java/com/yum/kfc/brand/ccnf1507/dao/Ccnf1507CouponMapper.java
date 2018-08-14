package com.yum.kfc.brand.ccnf1507.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507Coupon;


public interface Ccnf1507CouponMapper {
	
	public int insert(Ccnf1507Coupon coupon);
	
	public String getValidCount(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);
	
	public List<String> getAllCoupons(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	
	public String getLastCoupon(@Param(value="userId") String userId, 
			@Param(value="channelType") Integer channelType);
	

	
}
