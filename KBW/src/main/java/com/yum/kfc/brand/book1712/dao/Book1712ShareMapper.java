package com.yum.kfc.brand.book1712.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.book1712.pojo.Book1712Share;

public interface Book1712ShareMapper {
	void saveShare(Book1712Share share);

	Book1712Share getUserBookShare(@Param("shareId") String shareId, @Param("brandUserId") String brandUserId);

	List<Book1712Share> getBookShares(@Param("shareId") String shareId);

	int getTotalBookShares();
}
