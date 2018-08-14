package com.yum.kfc.brand.bnbjc1508.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.bnbjc1508.dao.Bnbjc1508CouponMapper;
import com.yum.kfc.brand.bnbjc1508.dao.Bnbjc1508OpenMapper;
import com.yum.kfc.brand.bnbjc1508.dao.Bnbjc1508ShareMapper;
import com.yum.kfc.brand.bnbjc1508.dao.Bnbjc1508UserMapper;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Coupon;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Open;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Share;
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508User;
import com.yum.kfc.brand.bnbjc1508.service.Bnbjc1508Service;
import com.yum.kfc.brand.common.utils.DateUtil;

/**
 * 
 * @author luolix
 */
@Service
public class Bnbjc1508ServiceImpl implements Bnbjc1508Service {

	private static final Logger logger = LoggerFactory.getLogger(Bnbjc1508ServiceImpl.class);

	@Autowired
	private Bnbjc1508OpenMapper openMapper;
	@Autowired
	private Bnbjc1508CouponMapper couponMapper;
	@Autowired
	private Bnbjc1508ShareMapper shareMapper;
	@Autowired
	private Bnbjc1508UserMapper userMapper;

	@Override
	public boolean saveOpen(Bnbjc1508Open open) {
		boolean isSuccess = false;
		try {
			int result = openMapper.insert(open);
			isSuccess = result > 0 ? true : false;
		}catch (Exception e){
			logger.error(" ==============================saveOpen occur exception", e);
		}
		return isSuccess;
	}
	

	@Override
	public boolean saveShare(Bnbjc1508Share share) {
		boolean isSuccess = false;
		try {
			int result = shareMapper.insert(share);
			isSuccess = result > 0 ? true : false;
		}catch (Exception e){
			logger.error(" ==============================saveShare occur exception", e);
		}
		return isSuccess;
	}

	@Override
	public String getTodayCoupon(Integer channelType, String userId) {
		Date fromTime = DateUtil.getBeforeDawn();
		Date toTime = DateUtil.getMidNight();
		String promoCode = couponMapper.getTodayCoupon(userId, channelType, fromTime, toTime);
		return promoCode;
	}


	@Override
	public boolean saveCoupon(Bnbjc1508Coupon coupon) {
		boolean isSuccess = false;
		try {
			int result = couponMapper.insert(coupon);
			isSuccess = result > 0 ? true : false;
		}catch (Exception e){
			logger.error(" ==============================saveCoupon occur exception", e);
		}
		return isSuccess;
	}
	
	
	@Override
	public String getLastCoupon(String userId, Integer channelType) {
		String promoCode = couponMapper.getLastCoupon(userId, channelType);
		return promoCode;
	}

	@Override
	public boolean saveUser(Bnbjc1508User user) {
		boolean isSuccess = false;
		try {
			int count = userMapper.getUserCount(user.getUserId());
			logger.info("userId :{}, count: {}", user.getUserId(), count);
			if(count <= 0){
				userMapper.insert(user);
			}
			isSuccess = true;
		}catch (Exception e){
			logger.error(" ==============================saveUser occur exception", e);
		}
		return isSuccess;
	}

	@Override
	public String getNickName(String userId) {
		String nickName = userMapper.getNickName(userId);
		return nickName;
	}

	@Override
	public String getTagContent(String openId) {
		String tagContent = openMapper.getTagContent(openId);
		return tagContent;
	}
	
	@Override
	public boolean isDrawCoupon(String userId, Integer channelType){
		Date fromTime = DateUtil.getBeforeDawn();
		Date toTime = DateUtil.getMidNight();
		int count = couponMapper.isDrawCoupon(userId, channelType, fromTime, toTime);
		return count == 0 ? true : false;
	}


}
