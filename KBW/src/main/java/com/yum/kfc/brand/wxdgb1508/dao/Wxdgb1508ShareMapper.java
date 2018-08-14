package com.yum.kfc.brand.wxdgb1508.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508Share;


public interface Wxdgb1508ShareMapper {
	
	public int insert(Wxdgb1508Share share);
	
	public Long isSendLove(@Param(value="userId") String userId);

}
