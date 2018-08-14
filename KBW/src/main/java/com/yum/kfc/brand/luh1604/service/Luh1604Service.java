package com.yum.kfc.brand.luh1604.service;

import com.yum.kfc.brand.luh1604.pojo.Luh1604Open;
import com.yum.kfc.brand.luh1604.pojo.Luh1604User;

/**
 * 
 * @author Yi Dequan
 *
 */
public interface Luh1604Service {
	
	/**
	 * 获取总数量
	 * @return
	 */
	public Long getNumber();
	
	/**
	 * 保存参加者信息
	 * @param user
	 * @return
	 */
	public boolean saveAttendUser(Luh1604User user);
	
	/**
	 * 获取参加者排名
	 * @param userId
	 * @return
	 */
	public Long getIdByUserId(String userId);
	
	/**
	 * 保存打开记录
	 * @param open
	 * @return
	 */
	public boolean saveOpen(Luh1604Open open);

	/**
	 * get user photoUrl
	 * @param userId
	 * @return
	 */
	public String getUserPhoto(String userId);
}
