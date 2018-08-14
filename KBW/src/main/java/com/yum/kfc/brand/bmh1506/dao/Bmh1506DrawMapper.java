package com.yum.kfc.brand.bmh1506.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Draw;



public interface Bmh1506DrawMapper {
	
	public Long queryDrawTimesByIp(@Param(value="ipAddr") String ipAddr,
			@Param(value="fromTime") Date fromTime,
			@Param(value="toTime") Date toTime);

	public Long queryDrawTimesByUser(@Param(value="userId") String userId,
				@Param(value="channelType") Integer channelType,
				@Param(value="fromTime") Date fromTime,
				@Param(value="toTime") Date toTime);
	
	public Long queryIsUserWon(@Param(value="userId") String userId,
			@Param(value="channelType") Integer channelType);
	
	public Long queryIsWonWatch(@Param(value="userId") String userId,
			@Param(value="channelType") Integer channelType,
			@Param(value="awardType") Integer awardType);
	
	public int saveWinAward(Bmh1506Draw draw);

	public int insertDraw(Bmh1506Draw draw);
	
	public Long queryWinPhoneCount(@Param(value="phone") String phone);
	
	public Long queryUserWriteCount(@Param(value="userId") String userId);
	
	public List<String> queryWinAwardPersons();
	
	public List<Bmh1506Draw> queryWinAwardNotPhones();
	
	
}
