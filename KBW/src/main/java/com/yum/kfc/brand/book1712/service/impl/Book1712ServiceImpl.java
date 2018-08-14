package com.yum.kfc.brand.book1712.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.jdf.ssm.dbs.ms.DbRoute;
import com.yum.kfc.brand.BrandDB;
import com.yum.kfc.brand.book1712.dao.Book1712OrderMapper;
import com.yum.kfc.brand.book1712.dao.Book1712ShareMapper;
import com.yum.kfc.brand.book1712.pojo.Book1712Order;
import com.yum.kfc.brand.book1712.pojo.Book1712Share;
import com.yum.kfc.brand.book1712.service.Book1712Service;

@Service
public class Book1712ServiceImpl implements Book1712Service {
	@Autowired Book1712OrderMapper orderMapper;
	@Autowired Book1712ShareMapper shareMapper;
	
	@DbRoute(BrandDB.SOCIAL_MASTER.class)
	@Override
	public void saveBookOrder(Book1712Order bo) {
		orderMapper.saveOrder(bo);
		
//		Calendar now = Calendar.getInstance();
//		now.setTime(bo.getCreateTime());
//		int hour = now.get(Calendar.HOUR_OF_DAY);
//		orderMapper.saveOrderByHour(bo.getOrderId(), bo.getBrandUserId(), bo.getCreateTime(), hour);
	}

	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	@Override
	public int getAllCompletedCount() {
		return shareMapper.getTotalBookShares();
	}

	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	@Override
	public List<Book1712Share> getBookShares(String shareId) {
		return shareMapper.getBookShares(shareId);
	}

	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	@Override
	public Book1712Share getUserBookShare(String shareId, String userId) {
		return shareMapper.getUserBookShare(shareId, userId);
	}

	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	@Override
	public Map<String, String> getUserWxInfo(String userId, String orderId) {
		return orderMapper.getUserWxInfo(userId, orderId);
	}

	@DbRoute(BrandDB.SOCIAL_MASTER.class)
	@Override
	public void saveBookShare(Book1712Share share) {
		shareMapper.saveShare(share);
	}

	@DbRoute(BrandDB.SOCIAL_MASTER.class)
	@Override
	public void updateBookOrder(Book1712Order order) {
		orderMapper.updateOrder(order);
	}

	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	@Override
	public List<Book1712Order> getUserBookOrder(String userId) {
		return orderMapper.getUserBookOrder(userId);
	}

	@Override
	public List<Book1712Order> getNotPaiedBookOrdersByShareId(String shareId) {
		return orderMapper.getNotPaiedOrdersByShareId(shareId);
	}


}
