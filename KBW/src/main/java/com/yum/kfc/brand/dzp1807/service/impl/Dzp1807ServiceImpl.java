package com.yum.kfc.brand.dzp1807.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.jdf.ssm.dbs.ms.DbRoute;
import com.yum.kfc.brand.BrandDB;
import com.yum.kfc.brand.dzp1807.dao.Dzp1807DrawMapper;
import com.yum.kfc.brand.dzp1807.dao.Dzp1807OrderMapper;
import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Draw;
import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Order;
import com.yum.kfc.brand.dzp1807.service.Dzp1807Service;

@Service
public class Dzp1807ServiceImpl implements Dzp1807Service {
	@Autowired
	private Dzp1807OrderMapper orderMapper;

	@Autowired
	private Dzp1807DrawMapper drawMapper;

	@DbRoute(BrandDB.SOCIAL_MASTER.class)
	@Override
	public void saveOrder(Dzp1807Order order) {
		orderMapper.saveOrder(order);
	}

	@DbRoute(BrandDB.SOCIAL_MASTER.class)
	@Override
	public void saveDraw(Dzp1807Draw draw) {
		drawMapper.saveDraw(draw);
	}

	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	@Override
	public Long getUserDrawCount(String ssoUserId, Date beginTime, Date endTime) {
		return drawMapper.getUserDrawCount(ssoUserId, beginTime, endTime);
	}

	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	@Override
	public Long getUserAwardWinCount(String ssoUserId, int awardType) {
		return drawMapper.getUserAwardWinCount(ssoUserId, awardType);
	}

	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	@Override
	public List<Dzp1807Draw> getUserDrawRecord(String ssoUserId, Date beginTime) {
		List<Dzp1807Draw> list = drawMapper.getUserDrawRecord(ssoUserId,
				beginTime);
		if (list != null && list.size() > 0) {
			return list;
		}

		return null;
	}

}
