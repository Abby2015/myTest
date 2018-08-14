package com.yum.kfc.brand.bigcake1806.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.bigcake1806.pojo.BcUser1806;

public interface BcUser1806Mapper {

	BcUser1806 getUserInfo(@Param("userCode") String crmUserCode);

	String getTasteStoreName(@Param("cityName") String cityName);

}
