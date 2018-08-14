package com.yum.kfc.brand.dzp1807.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Draw;

public interface Dzp1807DrawMapper {

	Long getUserDrawCount(@Param(value="ssoUserId") String ssoUserId, 
						 @Param(value="beginTime") Date beginTime, 
						 @Param(value="endTime") Date endTime);

	Long getUserAwardWinCount(@Param(value="ssoUserId") String ssoUserId, @Param(value="awardType") int awardType);

	void saveDraw(Dzp1807Draw draw);

	List<Dzp1807Draw> getUserDrawRecord(@Param(value="ssoUserId") String ssoUserId, @Param(value="beginTime") Date beginTime);
}
