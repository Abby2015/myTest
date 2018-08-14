package com.yum.kfc.brand.wxdgb1508.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.wxdgb1508.dao.Wxdgb1508AskMapper;
import com.yum.kfc.brand.wxdgb1508.dao.Wxdgb1508CouponMapper;
import com.yum.kfc.brand.wxdgb1508.dao.Wxdgb1508OpenMapper;
import com.yum.kfc.brand.wxdgb1508.dao.Wxdgb1508ShareMapper;
import com.yum.kfc.brand.wxdgb1508.dao.Wxdgb1508UserMapper;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Ask;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Coupon;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Open;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Share;
import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508User;
import com.yum.kfc.brand.wxdgb1508.service.Wxdgb1508Service;

/**
 * 
 * @author luolix
 */
@Service
public class Wxdgb1508ServiceImpl implements Wxdgb1508Service {

	private static final Logger logger = LoggerFactory.getLogger(Wxdgb1508ServiceImpl.class);

	@Autowired
	private Wxdgb1508CouponMapper couponMapper;
	@Autowired
	private Wxdgb1508ShareMapper shareMapper;
	@Autowired
	private Wxdgb1508OpenMapper openMapper;
	@Autowired
	private Wxdgb1508AskMapper askMapper;
	@Autowired
	private Wxdgb1508UserMapper userMapper;

	@Override
	public boolean saveOpen(Wxdgb1508Open open) {
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
	public boolean saveAsk(Wxdgb1508Ask ask) {
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
	public boolean isSendLove(String userId) {
		boolean isSuccess = false;
		try {
			Long result = shareMapper.isSendLove(userId);
			isSuccess = result > 0 ? true : false;
		}catch (Exception e){
			logger.error(" ==============================isSendLove occur exception", e);
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
	public Wxdgb1508Ask getLoveContent(String askId) {
		Wxdgb1508Ask ask = askMapper.getLoveContent(askId);
		return ask;
	}

	@Override
	public boolean saveShare(Wxdgb1508Share share) {
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
		boolean isDraw = true;
		String drawUserId = "null";
		Date fromTime = DateUtil.getBeforeDawn();
		Date toTime = DateUtil.getMidNight();
		List<Wxdgb1508Coupon> listCoupon = couponMapper.isDrawCoupon(userId, channelType, fromTime, toTime);
		if(!listCoupon.isEmpty()){	//如果不为空，说明可以中半价券
			isDraw = false;
			drawUserId = listCoupon.get(0).getUserId();
		}
		return String.valueOf(isDraw)+">"+drawUserId;
	}
	
	@Override
	public boolean saveCoupon(Wxdgb1508Coupon coupon) {
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
	public boolean saveUser(Wxdgb1508User user){
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
	
	@Override
	public List<Wxdgb1508User> getAddCouponUserList(){
		List<Wxdgb1508User> userList = userMapper.getAddCouponUserList();
		return userList;
	}
	
	@Override
	public List<String> getMissCouponUserList(){
		List<String> userList = userMapper.getMissCouponUserList();
		return userList;
	}


}
