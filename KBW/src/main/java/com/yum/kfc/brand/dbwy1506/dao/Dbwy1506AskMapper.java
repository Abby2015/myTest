package com.yum.kfc.brand.dbwy1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506Ask;


public interface Dbwy1506AskMapper {
	
	public int insert(Dbwy1506Ask ask);

	public String getTagContent(@Param(value="askId") String askId);
	
}
