package com.yum.kfc.brand.dzp1807.service;

import java.util.Date;
import java.util.List;

import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Draw;
import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Order;

public interface Dzp1807Service {

	void saveOrder(Dzp1807Order order);

	void saveDraw(Dzp1807Draw draw);

	Long getUserDrawCount(String ssoUserId, Date beginTime, Date date);

	Long getUserAwardWinCount(String ssoUserId, int awardType);

	List<Dzp1807Draw> getUserDrawRecord(String ssoUserId,  Date beginTime);

}
