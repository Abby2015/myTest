package com.yum.kfc.brand.camp1504.dao;

import org.apache.ibatis.annotations.Param;

import com.yum.kfc.brand.camp1504.pojo.FooldayQuestion;


public interface FooldayQuestionMapper {
	
	public FooldayQuestion getShareQuestion(@Param(value = "questionId") String questionId);
	
	public FooldayQuestion getRandomQuestion(@Param(value = "questionId") String questionId);
	
	public FooldayQuestion getCycleQuestion(@Param(value = "openId") String openId, 
			@Param(value = "userId") String userId, @Param(value = "recycleCount") Integer recycleCount);
	
	public FooldayQuestion getRandomNotAnswerQuestion(@Param(value = "openId") String openId, 
			@Param(value = "userId") String userId, @Param(value = "recycleCount") Integer recycleCount);
	
	public FooldayQuestion getRandomNotRightQuestion(@Param(value = "openId") String openId, 
			@Param(value = "userId") String userId, @Param(value = "recycleCount") Integer recycleCount);
	
	public int updateQuestionTotal(@Param(value = "questionId") String questionId,
			@Param(value = "choice") Integer choice);
	
}
