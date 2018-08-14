package com.yum.kfc.brand.camp1505.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.camp1505.dao.Camp1505DrawMapper;
import com.yum.kfc.brand.camp1505.dao.Camp1505OpenMapper;
import com.yum.kfc.brand.camp1505.dao.Camp1505ShareMapper;
import com.yum.kfc.brand.camp1505.pojo.Camp1505Draw;
import com.yum.kfc.brand.camp1505.pojo.Camp1505Open;
import com.yum.kfc.brand.camp1505.pojo.Camp1505Share;
import com.yum.kfc.brand.camp1505.service.Camp1505Service;
import com.yum.kfc.brand.common.utils.DateUtil;

/**
 * 
 * @author luolix
 */
@Service
public class Camp1505ServiceImpl implements Camp1505Service {
	
	private static final Logger logger = LoggerFactory.getLogger(Camp1505ServiceImpl.class);

	@Autowired
	private Camp1505OpenMapper openMapper;
	@Autowired
	private Camp1505ShareMapper shareMapper;
	@Autowired
	private Camp1505DrawMapper drawMapper;
	
	@Override
	public boolean saveOpen(Camp1505Open open) {
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
	public boolean saveShare(Camp1505Share share) {
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
	public String getTagContent(String shareId) {
		String tagContent = shareMapper.getTagContent(shareId);
		return tagContent;
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
	public boolean saveDraw(Camp1505Draw draw) {
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
	public boolean saveWinAward(Camp1505Draw draw) {
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

}
