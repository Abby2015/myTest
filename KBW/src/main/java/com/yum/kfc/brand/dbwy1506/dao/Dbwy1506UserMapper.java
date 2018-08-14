package com.yum.kfc.brand.dbwy1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506User;


public interface Dbwy1506UserMapper {
	
	public int insert(Dbwy1506User user);

	public String getNickName(@Param(value="userId") String userId);
	
	public int getUserCount(@Param(value="userId") String userId);
	
}
