package com.yum.kfc.brand.camp1505.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.camp1505.pojo.Camp1505Share;


public interface Camp1505ShareMapper {
	
	public int insert(Camp1505Share share);
	
	public String getTagContent(@Param(value="shareId") String shareId);
	
}
