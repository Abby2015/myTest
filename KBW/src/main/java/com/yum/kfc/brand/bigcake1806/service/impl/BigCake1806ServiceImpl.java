package com.yum.kfc.brand.bigcake1806.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.jdf.ssm.dbs.ms.DbRoute;
import com.yum.kfc.brand.BrandDB;
import com.yum.kfc.brand.bigcake1806.dao.BcUser1806Mapper;
import com.yum.kfc.brand.bigcake1806.pojo.BcUser1806;
import com.yum.kfc.brand.bigcake1806.service.BigCake1806Service;

@Service
public class BigCake1806ServiceImpl implements BigCake1806Service {
	@Autowired
	private BcUser1806Mapper bcUser1806Mapper;
	
	@Override
	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	public BcUser1806 getUserInfo(String crmUserCode) {
		return bcUser1806Mapper.getUserInfo(crmUserCode);
	}

	@Override
	@DbRoute(BrandDB.SOCIAL_SLAVE.class)
	public String getTasteStoreName(String cityName) {
		return bcUser1806Mapper.getTasteStoreName(cityName);
	}

}
