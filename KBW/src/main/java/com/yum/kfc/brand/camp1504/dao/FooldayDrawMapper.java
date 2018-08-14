package com.yum.kfc.brand.camp1504.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.camp1504.pojo.FooldayDraw;



public interface FooldayDrawMapper {
	
	public Long queryDrawTimesByIp(@Param(value="ipAddr") String ipAddr,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);

	public Long queryDrawTimesByUser(@Param(value="userId") String userId,
				@Param(value="channelType") Integer channelType,
				@Param(value="fromTime") Date fromTime,
				@Param(value="toTime") Date toTime);
	
	public FooldayDraw isWinNotPhone(@Param(value="userId") String userId,
			@Param(value="channelType") Integer channelType);
	
	public Long queryIsUserWon(@Param(value="userId") String userId,
			@Param(value="channelType") Integer channelType);
	
	public int saveWinPhone(FooldayDraw fooldayDraw);

	public int insertWinInfo(FooldayDraw fooldayDraw);
	
	public Long queryWinPhoneCount(String phone);
	
	
}
