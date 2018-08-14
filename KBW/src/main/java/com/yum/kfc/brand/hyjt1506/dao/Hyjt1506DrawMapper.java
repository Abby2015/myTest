package com.yum.kfc.brand.hyjt1506.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.hyjt1506.pojo.Hyjt1506Draw;



public interface Hyjt1506DrawMapper {

	public Long queryDrawTimesByUser(@Param(value="userId") String userId,
				@Param(value="channelType") Integer channelType,
				@Param(value="fromTime") Date fromTime,
				@Param(value="toTime") Date toTime);
	
	public Long queryIsUserWon(@Param(value="userId") String userId,
			@Param(value="channelType") Integer channelType);
	
	public int saveWinAward(Hyjt1506Draw draw);

	public int insertDraw(Hyjt1506Draw draw);
	
	public Long queryWinPhoneCount(@Param(value="phone") String phone);
	
	public List<Hyjt1506Draw> queryWinUsers();
	
	public Long queryWinNotPhone(@Param(value="userId") String userId);

	
}
