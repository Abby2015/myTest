package com.yum.kfc.brand.wxdgb1508.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Open;


public interface Wxdgb1508OpenMapper {
	
	public int insert(Wxdgb1508Open open);
	
	public String getTagContent(@Param(value="openId") String openId);
	
}
