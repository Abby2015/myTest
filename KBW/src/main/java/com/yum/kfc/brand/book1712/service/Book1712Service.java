package com.yum.kfc.brand.book1712.service;

import java.util.List;
import java.util.Map;

import com.yum.kfc.brand.book1712.pojo.Book1712Order;
import com.yum.kfc.brand.book1712.pojo.Book1712Share;

public interface Book1712Service {

	void saveBookOrder(Book1712Order bo);

	int getAllCompletedCount();

	List<Book1712Share> getBookShares(String shareId);

	Book1712Share getUserBookShare(String shareId, String userId);

	Map<String, String> getUserWxInfo(String userId, String orderId);

	void saveBookShare(Book1712Share share);

	void updateBookOrder(Book1712Order order);

	List<Book1712Order> getUserBookOrder(String userId);

	List<Book1712Order> getNotPaiedBookOrdersByShareId(String shareId);

}
