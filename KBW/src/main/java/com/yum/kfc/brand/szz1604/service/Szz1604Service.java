package com.yum.kfc.brand.szz1604.service;

import com.yum.kfc.brand.szz1604.pojo.Szz1604Draw;
import com.yum.kfc.brand.szz1604.pojo.Szz1604Open;

public interface Szz1604Service {
	/**
	 * 保存用户打开活动记录
	 */
	public boolean saveOpen(Szz1604Open open);
	
	
	/**
	 * 保存抽奖记录
	 * @param param
	 * @return
	 */
	public boolean saveDraw(Szz1604Draw draw);
	
	/**
	 * 判断该用户是否已经抽过奖
	 * @param phone
	 * @return
	 */

	public boolean queryIsUserDraw(String phone);	
}
