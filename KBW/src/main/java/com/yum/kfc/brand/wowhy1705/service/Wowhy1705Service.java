package com.yum.kfc.brand.wowhy1705.service;

import java.util.Date;
import java.util.List;

import com.yum.kfc.brand.wowhy1705.pojo.Wowhy1705Draw;
import com.yum.kfc.brand.wowhy1705.pojo.Wowhy1705Order;

public interface Wowhy1705Service {

	Long getUserDrawCount(String userId, Date beginTime, Date endTime);

	Long getUserAwardWinCount(String ssoUserId, int awardType);

	void saveOrder(Wowhy1705Order order);

	void saveDraw(Wowhy1705Draw draw);

	List<Wowhy1705Draw> getUserDrawRecord(String ssoUserId);
}
