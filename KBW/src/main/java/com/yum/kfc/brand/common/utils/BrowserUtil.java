package com.yum.kfc.brand.common.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 浏览器通用工具类
 * 
 * @author Hoperun
 * 
 */
public class BrowserUtil {

	/**
	 * 判断是否为微信浏览器
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isMicroMessageBrowser(HttpServletRequest request) {
		return (request.getHeader("User-Agent").indexOf("MicroMessenger") != -1);
	}
	
	/**
	 * 获取设备类型
	 * 
	 * @param request
	 * @return
	 */
	public static String queryChannelType(HttpServletRequest request) {
		String channelType = "";
		if((request.getHeader("User-Agent").indexOf("MicroMessenger") != -1)){
			channelType = "1";
		}else{
			if(queryDeviceType(request).equals("1") || queryDeviceType(request).equals("0")){
				channelType = "0";
			}else{
				channelType = "2";
			}
		}
		return channelType;
	}
	
	/**
	 * 获取设备类型
	 * 
	 * @param request
	 * @return
	 */
	public static String queryDeviceType(HttpServletRequest request) {
		String deviceType = "";
		if(request.getHeader("User-Agent").indexOf("iPhone") != -1 || request.getHeader("User-Agent").indexOf("iPad") != -1){
			deviceType = "1";
		}else if(request.getHeader("User-Agent").indexOf("Android") != -1){
			deviceType = "0";
		}else{
			deviceType = "2";
		}
		return deviceType;
	}
	
}
