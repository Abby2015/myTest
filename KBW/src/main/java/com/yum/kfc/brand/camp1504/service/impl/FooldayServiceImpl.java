package com.yum.kfc.brand.camp1504.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.camp1504.dao.FooldayAnswerMapper;
import com.yum.kfc.brand.camp1504.dao.FooldayDrawMapper;
import com.yum.kfc.brand.camp1504.dao.FooldayOpenMapper;
import com.yum.kfc.brand.camp1504.dao.FooldayQuestionMapper;
import com.yum.kfc.brand.camp1504.dao.FooldayShareMapper;
import com.yum.kfc.brand.camp1504.pojo.FooldayAnswer;
import com.yum.kfc.brand.camp1504.pojo.FooldayDraw;
import com.yum.kfc.brand.camp1504.pojo.FooldayOpen;
import com.yum.kfc.brand.camp1504.pojo.FooldayQuestion;
import com.yum.kfc.brand.camp1504.pojo.FooldayShare;
import com.yum.kfc.brand.camp1504.service.FooldayService;

/**
 * 
 * @author luolix
 */
@Service
public class FooldayServiceImpl implements FooldayService {
	
	private static final Logger logger = LoggerFactory.getLogger(FooldayServiceImpl.class);

	@Autowired
	private FooldayOpenMapper fooldayOpenMapper;
	@Autowired
	private FooldayQuestionMapper fooldayQuestionMapper;
	@Autowired
	private FooldayAnswerMapper fooldayAnswerMapper;
	@Autowired
	private FooldayShareMapper fooldayShareMapper;
	@Autowired
	private FooldayDrawMapper fooldayDrawMapper;
	
	@Value("${campaign.question.count}")			
	public int QUESTION_COUNT =3;

	@Override
	public boolean saveOpen(FooldayOpen fooldayOpen) {
		boolean isSuccess = false;
		try {
			int result = fooldayOpenMapper.insert(fooldayOpen);
			isSuccess = result > 0 ? true : false;
		}catch (Exception e){
			logger.error(" ==============================saveOpen occur exception", e);
		}
		return isSuccess;
	}

	@Override
	public FooldayQuestion getQuestion(String userId, String openId, String shareQuestionId, Integer recycleCount) {
		FooldayQuestion question = null;
		int answerCount = fooldayAnswerMapper.getAnswerCount(openId, userId, recycleCount);
		//如果是初始化的首页问题
		if(answerCount == 0){
			//如果是用户点击发起者分享进来的首页问题
			question = fooldayQuestionMapper.getRandomQuestion(shareQuestionId);
		}else if(answerCount >= QUESTION_COUNT){//如果回答的问题等于题库中的总数，则再次轮循
			question = fooldayQuestionMapper.getCycleQuestion(openId, userId, recycleCount);
			recycleCount ++;
		}else{
			//随机得到没有答过的问题
			question = fooldayQuestionMapper.getRandomNotAnswerQuestion(openId, userId, recycleCount);
			//如果都答过，则取出一个最老的问题
//			if(null == question){
//				question = fooldayQuestionMapper.getCycleQuestion(openId, userId, recycleCount);
//				recycleCount ++;
				//则随机取出一个没有答对的问题
				//question = fooldayQuestionMapper.getRandomNotRightQuestion(openId, userId, recycleCount);
//			}
			//如果都答对了，则进行下一次轮循
//			if(null == question){	
//				question = fooldayQuestionMapper.getRandomQuestion(null);
//				recycleCount ++;
//			}
		}
		question.setRecycleCount(recycleCount);
		return question;
	}
	

	@Override
	public boolean saveAnswer(FooldayAnswer fooldayAnswer) {
		boolean isSuccess = false;
		try {
			int result = fooldayAnswerMapper.insert(fooldayAnswer);
			isSuccess = result > 0 ? true : false;
			//保存成功后,刷新问题统计
			if(isSuccess){
				fooldayQuestionMapper.updateQuestionTotal(fooldayAnswer.getQuestionId(), fooldayAnswer.getChoice());
			}
		}catch (Exception e){
			logger.error(" ==============================saveAnswer occur exception", e);
		}
		return isSuccess;
	}
	
	@Override
	public boolean saveShare(FooldayShare fooldayShare) {
		boolean isSuccess = false;
		try {
			int result = fooldayShareMapper.insert(fooldayShare);
			isSuccess = result > 0 ? true : false;
		}catch (Exception e){
			logger.error(" ==============================saveShare occur exception", e);
		}
		return isSuccess;
	}
	
	@Override
	public String getTagContent(String answerId) {
		String tagContent = fooldayAnswerMapper.getTagContent(answerId);
		return tagContent;
	}

	@Override
	public Long queryDrawTimesByIpToday(String ipAddr) {
		Date[] ds = getDays();
		Date todayBegin = ds[0], nextDayBegin=ds[1];
		return fooldayDrawMapper.queryDrawTimesByIp(ipAddr, todayBegin, nextDayBegin);
	}
	
	@Override
	public Long queryDrawTimesByUserToday(String userId, Integer channelType) {
		Date[] ds = getDays();
		Date todayBegin = ds[0], nextDayBegin=ds[1];
		return fooldayDrawMapper.queryDrawTimesByUser(userId, channelType, todayBegin, nextDayBegin);
	}
	
	private Date[] getDays(){
		Date todayBegin, nextDayBegin;
		String fmt = "yyyy-MM-dd";
		SimpleDateFormat formatter = new SimpleDateFormat(fmt);
		
		long ms = new Date().getTime();
		long ms2 = ms + 24L*60*60*1000;
		try {
			todayBegin = DateUtils.parseDate(formatter.format(new Date(ms)), fmt);
			nextDayBegin = DateUtils.parseDate(formatter.format(new Date(ms2)), fmt);
		} catch (ParseException e) {
			logger.error("failed to get next day", e);
			throw new RuntimeException("Internal Server Error: failed to get next day: "+ e.getMessage(), e);
		}
		return new Date[]{todayBegin, nextDayBegin};
	}

	@Override
	public boolean queryIsUserWon(String userId, Integer channelType) {
		boolean isWon = false;
		try {
			Long num = fooldayDrawMapper.queryIsUserWon(userId, channelType);
			if(num > 0){
				isWon = true;
			}
		}catch (Exception e) {
			logger.error(" ==============================queryIsUserWon occur exception", e);
		}
		return isWon;
	}

	@Override
	public boolean saveWinInfo(FooldayDraw fooldayDraw) {
		logger.info("==============================start save win award recod");
		boolean isSuccess = false;
		try {
			int result = fooldayDrawMapper.insertWinInfo(fooldayDraw);
			isSuccess = result > 0 ? true : false;
		}catch (Exception e) {
			logger.error("==============================save win award occur exception", e);
		}
		return isSuccess;
	}

	@Override
	public boolean saveWinPhone(FooldayDraw fooldayDraw) {
		logger.info("==============================start save win phone");
		boolean isSuccess = false;
		try {
			fooldayDrawMapper.saveWinPhone(fooldayDraw);
			isSuccess = true;
		}catch (Exception e) {
			logger.error("==============================save win phone occur exception", e);
		}
		return isSuccess;
	}
	
	@Override
	public boolean isPhoneUsed(String phone) {
		Long count = fooldayDrawMapper.queryWinPhoneCount(phone);
		return count > 0;
	}
	
	@Override
	public FooldayDraw isWinNotPhone(String userId, Integer channelType) {
		FooldayDraw fooldayDraw = fooldayDrawMapper.isWinNotPhone(userId, channelType);
		return fooldayDraw;
	}
	

}
