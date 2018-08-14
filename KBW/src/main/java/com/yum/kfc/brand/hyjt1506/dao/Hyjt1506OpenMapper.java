package com.yum.kfc.brand.hyjt1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Open;


public interface Hyjt1506OpenMapper {
	
	public int insert(Hyjt1506Open open);
	
	public String getTagContent(@Param(value="openId") String openId);
	
}
