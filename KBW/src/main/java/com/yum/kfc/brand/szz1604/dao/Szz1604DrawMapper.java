package com.yum.kfc.brand.szz1604.dao;


import com.yum.kfc.brand.szz1604.pojo.Szz1604Draw;

public interface Szz1604DrawMapper {
	int insert(Szz1604Draw draw);
	
	public Long queryIsUserDraw(String phone);
}
