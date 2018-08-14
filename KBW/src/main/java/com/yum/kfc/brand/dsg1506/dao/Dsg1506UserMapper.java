package com.yum.kfc.brand.dsg1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.dsg1506.pojo.Dsg1506User;


public interface Dsg1506UserMapper {
	
	public int insert(Dsg1506User user);

	public String getNickName(@Param(value="userId") String userId);
	
	public int getUserCount(@Param(value="userId") String userId);
	
}
