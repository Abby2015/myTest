package com.yum.kfc.brand.gkslz1606.dao;

import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Draw;


public interface Gkslz1606DrawMapper {
	int insert(Gkslz1606Draw draw);
	
	public Long queryIsUserDraw(String phone);
}
