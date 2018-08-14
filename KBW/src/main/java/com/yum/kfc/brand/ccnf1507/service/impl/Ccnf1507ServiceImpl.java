package com.yum.kfc.brand.ccnf1507.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.ccnf1507.dao.Ccnf1507AskMapper;
import com.yum.kfc.brand.ccnf1507.dao.Ccnf1507CouponMapper;
import com.yum.kfc.brand.ccnf1507.dao.Ccnf1507OpenMapper;
import com.yum.kfc.brand.ccnf1507.dao.Ccnf1507ShareMapper;
import com.yum.kfc.brand.ccnf1507.dao.Ccnf1507UserMapper;
import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507Ask;
import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507Coupon;
import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507Open;
import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507Share;
import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507User;
import com.yum.kfc.brand.ccnf1507.service.Ccnf1507Service;
import com.yum.kfc.brand.common.utils.DateUtil;

/**
 * 
 * @author luolix
 */
@Service
public class Ccnf1507ServiceImpl implements Ccnf1507Service {

	private static final Logger logger = LoggerFactory.getLogger(Ccnf1507ServiceImpl.class);

	@Autowired
	private Ccnf1507OpenMapper openMapper;
	@Autowired
	private Ccnf1507AskMapper askMapper;
	@Autowired
	private Ccnf1507CouponMapper couponMapper;
	@Autowired
	private Ccnf1507ShareMapper shareMapper;
	@Autowired
	private Ccnf1507UserMapper userMapper;

	@Override
	public boolean saveOpen(Ccnf1507Open open) {
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
	public boolean saveAsk(Ccnf1507Ask ask) {
		boolean isSuccess = false;
		try {
			int result = askMapper.insert(ask);
			isSuccess = result > 0 ? true : false;
		}catch (Exception e){
			logger.error(" ==============================saveAsk occur exception", e);
		}
		return isSuccess;
	}


	@Override
	public boolean saveShare(Ccnf1507Share share) {
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
	public String isShouldDrawCoupon(Integer channelType, String userId) {
		Date fromTime = DateUtil.getBeforeDawn();
		Date toTime = DateUtil.getMidNight();
		String promoCode = couponMapper.getValidCount(userId, channelType, fromTime, toTime);
		return promoCode;
	}


	@Override
	public boolean saveCoupon(Ccnf1507Coupon coupon) {
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
	public List<String> getAllCoupons(String userId, Integer channelType) {
		List<String> couponList = couponMapper.getAllCoupons(userId, channelType);
		return couponList;
	}
	
	
	@Override
	public String getLastCoupon(String userId, Integer channelType) {
		String promoCode = couponMapper.getLastCoupon(userId, channelType);
		return promoCode;
	}

	@Override
	public boolean saveUser(Ccnf1507User user) {
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


}
