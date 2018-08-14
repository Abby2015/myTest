package com.yum.kfc.brand.dbwy1506.api;

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
import com.yum.kfc.brand.dbwy1506.api.impl.Dbwy1506Parameter;

/**
 * 2015年6月清凉一夏活动
 * @author luolix
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Dbwy1506Api {
	
	/**
	 * A01: 打开活动首页
	 * @param {userId, channelType[0: Brand App||1: 微信||2:浏览器], deviceType[0: android||1:ios||2:browser],  deviceId, tag[来自A2], normalShare}
	 * @param request
	 * @return {sid, menuType, shareCome, other, drawCoupon, tag}
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Dbwy1506Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A02: 求助闺蜜Or求助ji友  / 我也要发券 / 和闺蜜or基友想半价
	 * @param {sid, userId, channelType, deviceType, tag[分享链接url参数获取], menuType[0:冰激凌; 1凉茶], askType[0:求闺蜜；1：求ji友]}
	 * @param request
	 * @return {会话标记：sid}
	 */
	@WebMethod
	@POST
	@Path("/ask")
	public Result ask(Dbwy1506Parameter parameter, @Context HttpServletRequest request);
	
	/**
	 * A03: 分享
	 * @param {sid, userId、token、menuType、channelType、deviceType
	 * mediaType[分享媒介(QQ|WX|TWB|WB|RR|DB|TB|ZFB)]、shareUrl, shareResult[分享结果:0失败||1成功]}
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/share")
	public Result share(Dbwy1506Parameter parameter);
	
	
	/**
	 * A05: 半价券放入卡包
	 * @param {promoCode}
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/collect")
	public Result collect(Dbwy1506Parameter parameter);
		
	
	/**
	 * A09: 获取第三方微信State
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/getThirdWechatState")
	public Result getThirdWechatState(Dbwy1506Parameter parameter);
	
	/**
	 * 登陆首页
	 * @param request
	 * @param response
	 */
	@WebMethod
	@GET
	@Path("/login.do")
	public void login(@Context HttpServletRequest request, @Context HttpServletResponse response);
	
	/**
	 * 首页微信跳转页面
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	@GET
	@Path("/wx_redirect.do")
	public void wxRedirect(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception;
	
	
	/**
	 * 受邀人微信分享跳转页面
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	@GET
	@Path("/share_redirect.do")
	public void share_redirect(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception;	
}
