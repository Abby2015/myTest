package com.yum.kfc.brand.hyjt1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506User;


public interface Hyjt1506UserMapper {
	
	public int insert(Hyjt1506User user);

	public String getNickName(@Param(value="userId") String userId);
	
	public int getUserCount(@Param(value="userId") String userId);
	
}
