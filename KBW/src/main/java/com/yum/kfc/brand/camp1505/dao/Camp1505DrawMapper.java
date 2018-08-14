package com.yum.kfc.brand.camp1505.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.camp1505.pojo.Camp1505Draw;



public interface Camp1505DrawMapper {
	
	public Long queryDrawTimesByIp(@Param(value="ipAddr") String ipAddr,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);

	public Long queryDrawTimesByUser(@Param(value="userId") String userId,
				@Param(value="channelType") Integer channelType,
				@Param(value="fromTime") Date fromTime,
				@Param(value="toTime") Date toTime);
	
	public Long queryIsUserWon(@Param(value="userId") String userId,
			@Param(value="channelType") Integer channelType);
	
	public int saveWinAward(Camp1505Draw draw);

	public int insertDraw(Camp1505Draw draw);
	
	public List<String> queryWinAwardPersons();
	
	
}
