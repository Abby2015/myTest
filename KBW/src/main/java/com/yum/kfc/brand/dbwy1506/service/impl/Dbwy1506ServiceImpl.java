package com.yum.kfc.brand.dbwy1506.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.dbwy1506.dao.Dbwy1506AskMapper;
import com.yum.kfc.brand.dbwy1506.dao.Dbwy1506CouponMapper;
import com.yum.kfc.brand.dbwy1506.dao.Dbwy1506OpenMapper;
import com.yum.kfc.brand.dbwy1506.dao.Dbwy1506ShareMapper;
import com.yum.kfc.brand.dbwy1506.dao.Dbwy1506UserMapper;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Ask;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Coupon;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Open;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Share;
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506User;
import com.yum.kfc.brand.dbwy1506.service.Dbwy1506Service;

/**
 * 
 * @author luolix
 */
@Service
public class Dbwy1506ServiceImpl implements Dbwy1506Service {

	private static final Logger logger = LoggerFactory.getLogger(Dbwy1506ServiceImpl.class);

	@Autowired
	private Dbwy1506CouponMapper couponMapper;
	@Autowired
	private Dbwy1506ShareMapper shareMapper;
	@Autowired
	private Dbwy1506OpenMapper openMapper;
	@Autowired
	private Dbwy1506AskMapper askMapper;
	@Autowired
	private Dbwy1506UserMapper userMapper;

	@Override
	public boolean saveOpen(Dbwy1506Open open) {
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
	public boolean saveAsk(Dbwy1506Ask ask) {
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
	public String getTagContent(String askId, Integer normalShare) {
		String tagContent = "";
		if(normalShare == 1){
			tagContent = askMapper.getTagContent(askId);
		}else{
			tagContent = openMapper.getTagContent(askId);
		}
		return tagContent;
	}

	@Override
	public boolean saveShare(Dbwy1506Share share) {
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
	public boolean isShouldDrawCoupon(Integer channelType, String userId, Integer askType) {
		Date fromTime = DateUtil.getBeforeDawn();
		Date toTime = DateUtil.getMidNight();
		int count = couponMapper.getValidCount(userId, channelType, askType, fromTime, toTime);
		return count == 0 ? true : false;
	}
	
	@Override
	public boolean saveCoupon(Dbwy1506Coupon coupon) {
		boolean isSuccess = false;
		try {
			couponMapper.insert(coupon);
			isSuccess = true;
		}catch (Exception e){
			logger.error(" ==============================saveCoupon occur exception", e);
		}
		return isSuccess;
	}
	
	
	@Override
	public boolean saveUser(Dbwy1506User user){
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
	public String getNickName(String userId){
		String nickName = userMapper.getNickName(userId);
		return nickName;
	}
	
	@Override
	public String getLastCoupon(String userId, Integer channelType) {
		String promoCode = couponMapper.getLastCoupon(userId, channelType);
		return promoCode;
	}


}
