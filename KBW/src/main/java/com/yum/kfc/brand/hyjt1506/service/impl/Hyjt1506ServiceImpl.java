package com.yum.kfc.brand.hyjt1506.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.hyjt1506.dao.Hyjt1506AskMapper;
import com.yum.kfc.brand.hyjt1506.dao.Hyjt1506CouponMapper;
import com.yum.kfc.brand.hyjt1506.dao.Hyjt1506DrawMapper;
import com.yum.kfc.brand.hyjt1506.dao.Hyjt1506OpenMapper;
import com.yum.kfc.brand.hyjt1506.dao.Hyjt1506ShareMapper;
import com.yum.kfc.brand.hyjt1506.dao.Hyjt1506UserMapper;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Ask;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Coupon;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Draw;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Open;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Share;
import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506User;
import com.yum.kfc.brand.hyjt1506.service.Hyjt1506Service;

/**
 * 
 * @author luolix
 */
@Service
public class Hyjt1506ServiceImpl implements Hyjt1506Service {

	private static final Logger logger = LoggerFactory.getLogger(Hyjt1506ServiceImpl.class);

	@Autowired
	private Hyjt1506OpenMapper openMapper;
	@Autowired
	private Hyjt1506CouponMapper couponMapper;
	@Autowired
	private Hyjt1506ShareMapper shareMapper;
	@Autowired
	private Hyjt1506UserMapper userMapper;
	@Autowired
	private Hyjt1506DrawMapper drawMapper;
	@Autowired
	private Hyjt1506AskMapper askMapper;

	@Override
	public boolean saveOpen(Hyjt1506Open open) {
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
	public boolean saveShare(Hyjt1506Share share) {
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
	public boolean saveCoupon(Hyjt1506Coupon coupon) {
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
	public boolean saveUser(Hyjt1506User user) {
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
	public boolean saveDraw(Hyjt1506Draw draw) {
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
	public boolean saveWinAward(Hyjt1506Draw draw) {
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
	public boolean saveAsk(Hyjt1506Ask ask) {
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
	public List<Hyjt1506Draw> queryWinUsers() {
		List<Hyjt1506Draw> drawList = drawMapper.queryWinUsers();
		return drawList;
	}
	
	@Override
	public boolean queryWinNotPhone(String userId) {
		Long count = drawMapper.queryWinNotPhone(userId);
		return count > 0;
	}


}
