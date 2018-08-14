package com.yum.kfc.brand.camp1504.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.camp1504.pojo.FooldayAnswer;


public interface FooldayAnswerMapper {
	
	public int insert(FooldayAnswer fooldayAnswer);
	
	public int getAnswerCount(@Param(value = "openId") String openId, 
			@Param(value = "userId") String userId, @Param(value = "recycleCount") Integer recycleCount);
	
	public String getTagContent(String answerId);
	
}
