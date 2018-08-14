package com.yum.kfc.brand.hyjt1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Ask;


public interface Hyjt1506AskMapper {
	
	public int insert(Hyjt1506Ask ask);

	public String getTagContent(@Param(value="askId") String askId);
	
}
