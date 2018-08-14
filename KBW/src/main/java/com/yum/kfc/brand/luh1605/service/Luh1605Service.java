package com.yum.kfc.brand.luh1605.service;

import com.yum.kfc.brand.luh1605.pojo.Luh1605Draw;
import com.yum.kfc.brand.luh1605.pojo.Luh1605Open;

public interface Luh1605Service {
	/**
	 * 保存用户打开活动记录
	 */
	public boolean saveOpen(Luh1605Open open);
	
	
	/**
	 * 保存抽奖记录
	 * @param param
	 * @return
	 */
	public boolean saveDraw(Luh1605Draw draw);
	
	/**
	 * 判断该用户是否已经抽过奖
	 * @param phone
	 * @return
	 */

	public boolean queryIsUserDraw(String phone);	
}
