package com.yum.kfc.brand.bjmls1609.service;

import com.yum.kfc.brand.bjmls1609.pojo.Bjmls1609Attend;
import com.yum.kfc.brand.bjmls1609.pojo.Bjmls1609Open;


public interface Bjmls1609Service {
	/**
	 * 保存打开记录
	 * @param open
	 * @return
	 */
	public boolean saveOpen(Bjmls1609Open open);
	
	/**
	 * 保存参加记录
	 * @param attend
	 * @return
	 */
	public boolean saveAttend(Bjmls1609Attend attend);

	public boolean queryIsUserAttend(String userId);
}
