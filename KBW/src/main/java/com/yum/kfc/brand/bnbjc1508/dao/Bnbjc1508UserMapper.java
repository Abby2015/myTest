package com.yum.kfc.brand.bnbjc1508.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508User;


public interface Bnbjc1508UserMapper {
	
	public int insert(Bnbjc1508User user);

	public String getNickName(@Param(value="userId") String userId);
	
	public int getUserCount(@Param(value="userId") String userId);
	
}
