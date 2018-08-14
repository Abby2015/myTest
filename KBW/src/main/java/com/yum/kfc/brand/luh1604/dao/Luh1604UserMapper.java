package com.yum.kfc.brand.luh1604.dao;

import com.yum.kfc.brand.luh1604.pojo.Luh1604User;

public interface Luh1604UserMapper {
	Long getTotalNumber();
	int insert(Luh1604User user);
	Long getIdByUserId(String userId);
	String getUserPhoto(String userId);
}
