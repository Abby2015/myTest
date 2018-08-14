package com.yum.kfc.brand.gkslz1606.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.gkslz1606.dao.Gkslz1606DrawMapper;
import com.yum.kfc.brand.gkslz1606.dao.Gkslz1606OpenMapper;
import com.yum.kfc.brand.gkslz1606.dao.Gkslz1606ShareMapper;
import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Draw;
import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Open;
import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Share;
import com.yum.kfc.brand.gkslz1606.service.Gkslz1606Service;

@Service
public class Gkslz1606ServiceImpl implements Gkslz1606Service {
	
	private static final Logger logger = LoggerFactory.getLogger(Gkslz1606ServiceImpl.class);
	
	@Autowired
	Gkslz1606OpenMapper openMapper;
	
	@Autowired
	Gkslz1606DrawMapper drawMapper;
	
	@Autowired
	Gkslz1606ShareMapper shareMapper;

	@Override
	public boolean saveOpen(Gkslz1606Open open) {
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
	public boolean saveDraw(Gkslz1606Draw draw) {
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
			logger.error(" =====================queryIsUserDraw occur exception", e);
		}
		return isDraw;
	}


	@Override
	public boolean saveShare(Gkslz1606Share share) {
		boolean isSuccess = false;
		try {
			int result = shareMapper.insert(share);
			isSuccess = result > 0 ? true : false;
		} catch (Exception e) {
			logger.error("======================saveShare exception", e);
		}

		return isSuccess;
	}
}
