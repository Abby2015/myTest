package com.yum.kfc.brand.dbwy1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Open;


public interface Dbwy1506OpenMapper {
	
	public int insert(Dbwy1506Open open);
	
	public String getTagContent(@Param(value="openId") String openId);
	
}
