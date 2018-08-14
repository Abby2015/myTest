package com.yum.kfc.brand.dsg1506.api;

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
import com.yum.kfc.brand.dsg1506.api.impl.Dsg1506Parameter;

/**
 * 2015年6月爆米花活动
 * @author luolix
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Dsg1506Api {
	
	/**
	 * A01: 打开活动首页
	 * @param {userId, channelType, deviceType, deviceId}
	 * @param request
	 * @return {sid}
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Dsg1506Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A02: 弹弓游戏结束
	 * @param {sid, userId, channelType, deviceType, hitCount}
	 * @param request
	 * @return {result：结果(0: 游戏成功，1：没券；2：游戏失败)}
	 */
	@WebMethod
	@POST
	@Path("/ask")
	public Result ask(Dsg1506Parameter parameter);
	
	
	
	/**
	 * A04: 收入囊中 / 保存至卡包
	 * @param {promoCode}
	 * @param request
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/collect")
	public Result collect(Dsg1506Parameter parameter);
	
	
	/**
	 * A05: 分享
	 * @param {sid, userId、token、menuType、channelType、deviceType, mediaType, shareUrl, shareResult}
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/share")
	public Result share(Dsg1506Parameter parameter);

	
	
	/**
	 * A08: 获取第三方微信State
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/getThirdWechatState")
	public Result getThirdWechatState(Dsg1506Parameter parameter);
	
	
	/**
	 * A09：登陆首页
	 * @param request
	 * @param response
	 */
	@WebMethod
	@GET
	@Path("/login.do")
	public void login(@Context HttpServletRequest request, @Context HttpServletResponse response);
	
	
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

	
	/**
	 * A11：受邀人微信分享跳转页面
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	@GET
	@Path("/share_redirect.do")
	public void share_redirect(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception;

}
