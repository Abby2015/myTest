package com.yum.kfc.brand.xrbj1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506Open;


public interface Xrbj1506OpenMapper {
	
	public int insert(Xrbj1506Open open);
	
	public String getTagContent(@Param(value="openId") String openId);
	
}
