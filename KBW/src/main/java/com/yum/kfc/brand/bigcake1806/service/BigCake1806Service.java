package com.yum.kfc.brand.bigcake1806.service;

import com.yum.kfc.brand.bigcake1806.pojo.BcUser1806;

public interface BigCake1806Service {

	BcUser1806 getUserInfo(String crmUserCode);

	String getTasteStoreName(String cityName);

}
