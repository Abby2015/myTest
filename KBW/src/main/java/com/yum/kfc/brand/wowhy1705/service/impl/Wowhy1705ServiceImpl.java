package com.yum.kfc.brand.wowhy1705.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yum.kfc.brand.wowhy1705.dao.Wowhy1705DrawMapper;
import com.yum.kfc.brand.wowhy1705.dao.Wowhy1705OrderMapper;
import com.yum.kfc.brand.wowhy1705.pojo.Wowhy1705Draw;
import com.yum.kfc.brand.wowhy1705.pojo.Wowhy1705Order;
import com.yum.kfc.brand.wowhy1705.service.Wowhy1705Service;

@Service
public class Wowhy1705ServiceImpl implements Wowhy1705Service {

	@Autowired
	private Wowhy1705DrawMapper drawMapper;
	
	@Autowired
	private Wowhy1705OrderMapper orderMapper;
			
	@Override
	public Long getUserDrawCount(String ssoUserId, Date beginTime, Date endTime) {
		return drawMapper.getUserDrawCount(ssoUserId, beginTime, endTime);
	}

	@Override
	public Long getUserAwardWinCount(String ssoUserId, int awardType) {
		return drawMapper.getUserAwardWinCount(ssoUserId, awardType);
	}

	@Override
	public void saveOrder(Wowhy1705Order order) {
		orderMapper.saveOrder(order);
	}

	@Override
	public void saveDraw(Wowhy1705Draw draw) {
		drawMapper.saveDraw(draw);
	}

	@Override
	public List<Wowhy1705Draw> getUserDrawRecord(String ssoUserId) {
		return drawMapper.getUserDrawRecord(ssoUserId);
	} 
}
