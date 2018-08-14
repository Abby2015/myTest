package com.yum.kfc.brand.ccnf1507.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.ccnf1507.pojo.Ccnf1507User;


public interface Ccnf1507UserMapper {
	
	public int insert(Ccnf1507User user);

	public String getNickName(@Param(value="userId") String userId);
	
	public int getUserCount(@Param(value="userId") String userId);
	
}
