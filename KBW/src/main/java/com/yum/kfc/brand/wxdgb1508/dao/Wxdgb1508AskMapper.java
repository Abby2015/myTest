package com.yum.kfc.brand.wxdgb1508.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Ask;


public interface Wxdgb1508AskMapper {
	
	public int insert(Wxdgb1508Ask ask);
	

	public String getTagContent(@Param(value="askId") String askId);
	
	public Long isSendLove(@Param(value="userId") String userId);
	
	public Wxdgb1508Ask getLoveContent(@Param(value="askId") String askId);

}
