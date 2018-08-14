package com.yum.kfc.brand.dsg1506.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.dsg1506.dao.Dsg1506AskMapper;
import com.yum.kfc.brand.dsg1506.dao.Dsg1506CouponMapper;
import com.yum.kfc.brand.dsg1506.dao.Dsg1506OpenMapper;
import com.yum.kfc.brand.dsg1506.dao.Dsg1506ShareMapper;
import com.yum.kfc.brand.dsg1506.dao.Dsg1506UserMapper;
import com.yum.kfc.brand.dsg1506.pojo.Dsg1506Ask;
import com.yum.kfc.brand.dsg1506.pojo.Dsg1506Coupon;
import com.yum.kfc.brand.dsg1506.pojo.Dsg1506Open;
import com.yum.kfc.brand.dsg1506.pojo.Dsg1506Share;
import com.yum.kfc.brand.dsg1506.pojo.Dsg1506User;
import com.yum.kfc.brand.dsg1506.service.Dsg1506Service;

/**
 * 
 * @author luolix
 */
@Service
public class Dsg1506ServiceImpl implements Dsg1506Service {

	private static final Logger logger = LoggerFactory.getLogger(Dsg1506ServiceImpl.class);

	@Autowired
	private Dsg1506OpenMapper openMapper;
	@Autowired
	private Dsg1506AskMapper askMapper;
	@Autowired
	private Dsg1506CouponMapper couponMapper;
	@Autowired
	private Dsg1506ShareMapper shareMapper;
	@Autowired
	private Dsg1506UserMapper userMapper;

	@Override
	public boolean saveOpen(Dsg1506Open open) {
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
	public boolean saveAsk(Dsg1506Ask ask) {
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
	public boolean saveShare(Dsg1506Share share) {
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
	public boolean saveCoupon(Dsg1506Coupon coupon) {
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
	public boolean saveUser(Dsg1506User user) {
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
