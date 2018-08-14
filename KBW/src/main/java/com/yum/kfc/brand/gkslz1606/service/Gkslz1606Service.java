package com.yum.kfc.brand.gkslz1606.service;

import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Draw;
import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Open;
import com.yum.kfc.brand.gkslz1606.pojo.Gkslz1606Share;


public interface Gkslz1606Service {
	/**
	 * 保存用户打开活动记录
	 */
	public boolean saveOpen(Gkslz1606Open open);
	
	
	/**
	 * 保存抽奖记录
	 * @param param
	 * @return
	 */
	public boolean saveDraw(Gkslz1606Draw draw);
	
	/**
	 * 保存分享记录
	 * @param param
	 * @return
	 */
	public boolean saveShare(Gkslz1606Share share);
	
	/**
	 * 判断该用户是否已经抽过奖
	 * @param phone
	 * @return
	 */

	public boolean queryIsUserDraw(String phone);
}
