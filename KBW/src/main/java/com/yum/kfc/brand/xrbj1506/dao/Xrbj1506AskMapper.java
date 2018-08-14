package com.yum.kfc.brand.xrbj1506.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.xrbj1506.pojo.Xrbj1506Ask;


public interface Xrbj1506AskMapper {
	
	public int insert(Xrbj1506Ask ask);

	public String getTagContent(@Param(value="askId") String askId);
	
}
