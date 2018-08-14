package com.yum.kfc.brand.xrbj1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506User;


public interface Xrbj1506UserMapper {
	
	public int insert(Xrbj1506User user);

	public String getNickName(@Param(value="userId") String userId);
	
	public int getUserCount(@Param(value="userId") String userId);
	
}
