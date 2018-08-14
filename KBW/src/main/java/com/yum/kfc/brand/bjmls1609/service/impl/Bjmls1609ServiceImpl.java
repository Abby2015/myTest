package com.yum.kfc.brand.bjmls1609.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.bjmls1609.dao.Bjmls1609AttendMapper;
import com.yum.kfc.brand.bjmls1609.dao.Bjmls1609OpenMapper;
import com.yum.kfc.brand.bjmls1609.pojo.Bjmls1609Attend;
import com.yum.kfc.brand.bjmls1609.pojo.Bjmls1609Open;
import com.yum.kfc.brand.bjmls1609.service.Bjmls1609Service;

@Service
public class Bjmls1609ServiceImpl implements Bjmls1609Service {
	private static final Logger logger = LoggerFactory.getLogger(Bjmls1609ServiceImpl.class);
	
	@Autowired
	Bjmls1609OpenMapper openMapper;
	
	@Autowired
	Bjmls1609AttendMapper attendMapper;

	@Override
	public boolean saveOpen(Bjmls1609Open open) {
		boolean isSuccess = false;
		try {
			int result = openMapper.insert(open);
			isSuccess = result > 0 ? true : false;
		} catch (Exception e) {
			logger.error("======================saveOpen exception", e);
		}

		return isSuccess;
	}

	@Override
	public boolean saveAttend(Bjmls1609Attend attend) {
		boolean isSuccess = false;
		try {
			int result = attendMapper.insert(attend);
			isSuccess = result > 0 ? true : false;
		} catch (Exception e) {
			logger.error("======================saveAttend exception", e);
		}

		return isSuccess;
	}

	@Override
	public boolean queryIsUserAttend(String userId) {
		boolean isAttend = false;
		try {
			Long num = attendMapper.queryIsUserAttend(userId);
			if(num > 0){
				isAttend = true;
			}
		}catch (Exception e) {
			logger.error(" =====================queryIsUserAttend occur exception", e);
		}
		return isAttend;
	}

}
