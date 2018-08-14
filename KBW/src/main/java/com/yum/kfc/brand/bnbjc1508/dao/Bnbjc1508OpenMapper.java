package com.yum.kfc.brand.bnbjc1508.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Open;


public interface Bnbjc1508OpenMapper {
	
	public int insert(Bnbjc1508Open open);
	
	public String getTagContent(@Param(value="openId") String openId);
	
}
