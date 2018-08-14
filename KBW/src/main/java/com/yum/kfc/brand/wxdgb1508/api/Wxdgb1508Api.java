package com.yum.kfc.brand.wxdgb1508.api;

import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.wxdgb1508.api.impl.Wxdgb1508Parameter;

/**
 * 2015年8月拌柠拌桔茶
 * @author luolix
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Wxdgb1508Api {
	
	/**
	 * A01: 打开活动首页
	 * @param {userId, channelType, deviceType, deviceId}
	 * @param request
	 * @return {sid}
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Wxdgb1508Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A02: 发送告白令求半价券
	 * @param {sid, userId, channelType, deviceType}
	 * @param request
	 * @return {会话标记：sid}
	 */
	@WebMethod
	@POST
	@Path("/ask")
	public Result ask(Wxdgb1508Parameter parameter);
	
	
	/**
	 * A03: 破译内容
	 * @param {sid, userId, channelType, deviceType}
	 * @param request
	 * @return 
	 */
	@WebMethod
	@POST
	@Path("/decode")
	public Result decode(Wxdgb1508Parameter parameter);
	
	/**
	 * A03: 破译内容
	 * @param {sid, userId, channelType, deviceType}
	 * @param request
	 * @return 
	 */
	@WebMethod
	@GET
	@Path("/sendLoseCouponMsg")
	public Result sendLoseCouponMsg();
	
	
	/**
	 * A04: 收入囊中 / 保存至卡包
	 * @param {promoCode}
	 * @param request
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/collect")
	public Result collect(Wxdgb1508Parameter parameter);
	
	
	/**
	 * A04: 分享
	 * @param {sid, userId、token、menuType、channelType、deviceType, mediaType, shareUrl, shareResult}
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/share")
	public Result share(Wxdgb1508Parameter parameter);
	
	
	/**
	 * A08: 获取第三方微信State
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/getThirdWechatState")
	public Result getThirdWechatState(Wxdgb1508Parameter parameter);
	
	/**
	 * A10：首页微信跳转页面
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	@GET
	@Path("/wx_redirect.do")
	public void wxRedirect(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception;

}
