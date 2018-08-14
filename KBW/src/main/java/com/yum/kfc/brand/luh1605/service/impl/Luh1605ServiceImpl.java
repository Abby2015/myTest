package com.yum.kfc.brand.luh1605.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.luh1605.dao.Luh1605DrawMapper;
import com.yum.kfc.brand.luh1605.dao.Luh1605OpenMapper;
import com.yum.kfc.brand.luh1605.pojo.Luh1605Draw;
import com.yum.kfc.brand.luh1605.pojo.Luh1605Open;
import com.yum.kfc.brand.luh1605.service.Luh1605Service;

@Service
public class Luh1605ServiceImpl implements Luh1605Service {

private static final Logger logger = LoggerFactory.getLogger(Luh1605ServiceImpl.class);
	
	@Autowired
	Luh1605OpenMapper openMapper;
	
	@Autowired
	Luh1605DrawMapper drawMapper;

	@Override
	public boolean saveOpen(Luh1605Open open) {
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
	public boolean saveDraw(Luh1605Draw draw) {
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
