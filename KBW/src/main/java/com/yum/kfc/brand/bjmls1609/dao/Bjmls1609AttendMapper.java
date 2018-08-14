package com.yum.kfc.brand.bjmls1609.dao;

import com.yum.kfc.brand.bjmls1609.pojo.Bjmls1609Attend;

public interface Bjmls1609AttendMapper {
	int insert(Bjmls1609Attend attend);
	
	Long queryIsUserAttend(String userId);
}
