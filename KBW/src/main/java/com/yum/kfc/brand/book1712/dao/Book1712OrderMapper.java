package com.yum.kfc.brand.book1712.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.book1712.pojo.Book1712Order;

public interface Book1712OrderMapper {

	void saveOrder(Book1712Order bo);

	void saveOrderByHour(@Param("orderId") String orderId, @Param("createTime") Date createTime, @Param("hour") int hour);

	void updateOrder(Book1712Order order);

	List<Book1712Order> getUserBookOrder(@Param("brandUserId") String brandUserId);

	Map<String, String> getUserWxInfo(@Param("brandUserId") String brandUserId, @Param("orderId") String orderId);

	List<Book1712Order> getNotPaiedOrdersByShareId(@Param("shareId")String shareId);

}
