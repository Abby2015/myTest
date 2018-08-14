package com.yum.kfc.brand.bmh1506.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.bmh1506.dao.Bmh1506AskMapper;
import com.yum.kfc.brand.bmh1506.dao.Bmh1506CouponMapper;
import com.yum.kfc.brand.bmh1506.dao.Bmh1506DrawMapper;
import com.yum.kfc.brand.bmh1506.dao.Bmh1506OpenMapper;
import com.yum.kfc.brand.bmh1506.dao.Bmh1506ShareMapper;
import com.yum.kfc.brand.bmh1506.dao.Bmh1506UserMapper;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Ask;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Coupon;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Draw;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Open;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Share;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506User;
import com.yum.kfc.brand.bmh1506.service.Bmh1506Service;
import com.yum.kfc.brand.common.utils.DateUtil;

/**
 * 
 * @author luolix
 */
@Service
public class Bmh1506ServiceImpl implements Bmh1506Service {

	private static final Logger logger = LoggerFactory.getLogger(Bmh1506ServiceImpl.class);

	@Autowired
	private Bmh1506OpenMapper openMapper;
	@Autowired
	private Bmh1506AskMapper askMapper;
	@Autowired
	private Bmh1506CouponMapper couponMapper;
	@Autowired
	private Bmh1506ShareMapper shareMapper;
	@Autowired
	private Bmh1506UserMapper userMapper;
	@Autowired
	private Bmh1506DrawMapper drawMapper;

	@Override
	public boolean saveOpen(Bmh1506Open open) {
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
	public boolean saveAsk(Bmh1506Ask ask) {
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
	public boolean saveShare(Bmh1506Share share) {
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
	public boolean saveCoupon(Bmh1506Coupon coupon) {
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
	public boolean saveUser(Bmh1506User user) {
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
	public Long queryDrawTimesByIpToday(String ipAddr) {
		Date todayBegin = DateUtil.getBeforeDawn();
		Date nextDayBegin = DateUtil.getMidNight();
		return drawMapper.queryDrawTimesByIp(ipAddr, todayBegin, nextDayBegin);
	}

	@Override
	public Long queryDrawTimesByUserToday(String userId, Integer channelType) {
		Date todayBegin = DateUtil.getBeforeDawn();
		Date nextDayBegin = DateUtil.getMidNight();
		return drawMapper.queryDrawTimesByUser(userId, channelType, todayBegin, nextDayBegin);
	}

	@Override
	public boolean queryIsUserWon(String userId, Integer channelType) {
		boolean isWon = false;
		try {
			Long num = drawMapper.queryIsUserWon(userId, channelType);
			if(num > 0){
				isWon = true;
			}
		}catch (Exception e) {
			logger.error(" ==============================queryIsUserWon occur exception", e);
		}
		return isWon;
	}
	
	
	@Override
	public boolean queryIsWonWatch(String userId, Integer channelType, Integer awardType) {
		boolean isWon = false;
		try {
			Long num = drawMapper.queryIsWonWatch(userId, channelType, awardType);
			if(num > 0){
				isWon = true;
			}
		}catch (Exception e) {
			logger.error(" ==============================queryIsWonWatch occur exception", e);
		}
		return isWon;
	}
	
	
	

	@Override
	public boolean saveDraw(Bmh1506Draw draw) {
		logger.info("==============================start save draw recod");
		boolean isSuccess = false;
		try {
			int result = drawMapper.insertDraw(draw);
			isSuccess = result > 0 ? true : false;
		}catch (Exception e) {
			logger.error("==============================save draw occur exception", e);
		}
		return isSuccess;
	}

	@Override
	public boolean saveWinAward(Bmh1506Draw draw) {
		logger.info("==============================start save win award info");
		boolean isSuccess = false;
		try {
			drawMapper.saveWinAward(draw);
			isSuccess = true;
		}catch (Exception e) {
			logger.error("==============================save win award info occur exception", e);
		}
		return isSuccess;
	}
	
	@Override
	public boolean isPhoneUsed(String phone) {
		Long count = drawMapper.queryWinPhoneCount(phone);
		return count > 0;
	}
	
	@Override
	public boolean isUserWrited(String userId) {
		Long count = drawMapper.queryUserWriteCount(userId);
		return count > 0;
	}

	@Override
	public List<Bmh1506Draw> getAllNotPhoneWins() {
		List<Bmh1506Draw> drawList = drawMapper.queryWinAwardNotPhones();
		return drawList;
	}

}
