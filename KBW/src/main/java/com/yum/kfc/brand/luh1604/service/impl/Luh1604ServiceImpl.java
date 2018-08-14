package com.yum.kfc.brand.luh1604.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.luh1604.dao.Luh1604OpenMapper;
import com.yum.kfc.brand.luh1604.dao.Luh1604UserMapper;
import com.yum.kfc.brand.luh1604.pojo.Luh1604Open;
import com.yum.kfc.brand.luh1604.pojo.Luh1604User;
import com.yum.kfc.brand.luh1604.service.Luh1604Service;

@Service
public class Luh1604ServiceImpl implements Luh1604Service {
	private static final Logger logger = LoggerFactory.getLogger(Luh1604ServiceImpl.class);
	
	@Autowired
	private Luh1604UserMapper userMapper;
	
	@Autowired
	private Luh1604OpenMapper openMapper;
	
	@Override
	public Long getNumber() {
		return userMapper.getTotalNumber();
	}

	@Override
	public boolean saveAttendUser(Luh1604User user) {
		boolean isSuccess = false;
		try {
			int result = userMapper.insert(user);
			isSuccess = result > 0 ? true : false;
		} catch (Exception e) {
			logger.error("======================saveUser exception", e);
		}

		return isSuccess;
	}

	@Override
	public Long getIdByUserId(String userId) {
		return userMapper.getIdByUserId(userId);
	}

	@Override
	public boolean saveOpen(Luh1604Open open) {
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
	public String getUserPhoto(String userId) {
		return userMapper.getUserPhoto(userId);
	}

}
