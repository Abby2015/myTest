package com.yum.kfc.brand.wxdgb1508.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.wxdgb1508.pojo.Wxdgb1508User;


public interface Wxdgb1508UserMapper {
	
	public int insert(Wxdgb1508User user);

	public String getNickName(@Param(value="userId") String userId);
	
	public int getUserCount(@Param(value="userId") String userId);
	
	public List<Wxdgb1508User> getAddCouponUserList();
	
	public List<String> getMissCouponUserList();
	
}
