package com.yum.kfc.brand.szz1604.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.szz1604.dao.Szz1604DrawMapper;
import com.yum.kfc.brand.szz1604.dao.Szz1604OpenMapper;
import com.yum.kfc.brand.szz1604.pojo.Szz1604Draw;
import com.yum.kfc.brand.szz1604.pojo.Szz1604Open;
import com.yum.kfc.brand.szz1604.service.Szz1604Service;

@Service
public class Szz1604ServiceImpl implements Szz1604Service {

private static final Logger logger = LoggerFactory.getLogger(Szz1604ServiceImpl.class);
	
	@Autowired
	Szz1604OpenMapper openMapper;
	
	@Autowired
	Szz1604DrawMapper drawMapper;

	@Override
	public boolean saveOpen(Szz1604Open open) {
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
	public boolean saveDraw(Szz1604Draw draw) {
		boolean isSuccess = false;
		try {
			int result = drawMapper.insert(draw);
			isSuccess = result > 0 ? true : false;
		} catch (Exception e) {
			logger.error("======================saveDraw exception", e);
		}

		return isSuccess;
	}


	@Override
	public boolean queryIsUserDraw(String phone) {
		boolean isDraw = false;
		try {
			Long num = drawMapper.queryIsUserDraw(phone);
			if(num > 0){
				isDraw = true;
			}
		}catch (Exception e) {
			logger.error(" ==============================queryIsUserDraw occur exception", e);
		}
		return isDraw;
	}

}
