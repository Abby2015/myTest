package com.yum.kfc.brand.wowhy1705.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.wowhy1705.pojo.Wowhy1705Draw;

public interface Wowhy1705DrawMapper {

	Long getUserDrawCount(@Param(value="ssoUserId") String ssoUserId, 
						 @Param(value="beginTime") Date beginTime, 
						 @Param(value="endTime") Date endTime);

	Long getUserAwardWinCount(@Param(value="ssoUserId") String ssoUserId, @Param(value="awardType") int awardType);

	void saveDraw(Wowhy1705Draw draw);

	List<Wowhy1705Draw> getUserDrawRecord(@Param(value="ssoUserId") String ssoUserId);

}
