package com.yum.kfc.brand.luh1605.dao;


import com.yum.kfc.brand.luh1605.pojo.Luh1605Draw;

public interface Luh1605DrawMapper {
	int insert(Luh1605Draw draw);
	
	public Long queryIsUserDraw(String phone);
}
