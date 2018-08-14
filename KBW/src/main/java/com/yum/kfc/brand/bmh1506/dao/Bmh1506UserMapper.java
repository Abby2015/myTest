package com.yum.kfc.brand.bmh1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.bmh1506.pojo.Bmh1506User;


public interface Bmh1506UserMapper {
	
	public int insert(Bmh1506User user);

	public String getNickName(@Param(value="userId") String userId);
	
	public int getUserCount(@Param(value="userId") String userId);
	
}
