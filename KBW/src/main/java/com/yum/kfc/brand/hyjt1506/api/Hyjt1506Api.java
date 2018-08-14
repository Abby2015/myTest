package com.yum.kfc.brand.hyjt1506.api;

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
import com.yum.kfc.brand.hyjt1506.api.impl.Hyjt1506Parameter;

/**
 * 2015年6月焦盐海糖活动
 * @author luolix
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Hyjt1506Api {
	
	/**
	 * A01: 打开活动首页
	 * @param {userId, channelType, deviceType, deviceId}
	 * @param request
	 * @return {sid}
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Hyjt1506Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A02: 游戏结束
	 * @param {sid, userId, channelType, deviceType, hitCount}
	 * @param request
	 * @return {result：结果(0: 游戏成功，1：没券；2：游戏失败)}
	 */
	@WebMethod
	@POST
	@Path("/ask")
	public Result ask(Hyjt1506Parameter parameter);
	
	
	/**
	 * A04: 收入囊中 / 保存至卡包
	 * @param {promoCode}
	 * @param request
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/collect")
	public Result collect(Hyjt1506Parameter parameter);
	
	
	/**
	 * A04: 分享
	 * @param {sid, userId、token、menuType、channelType、deviceType, mediaType, shareUrl, shareResult}
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/share")
	public Result share(Hyjt1506Parameter parameter);
	
	
	/**
	 * A05: 抽奖
	 * @param {sid, userId, channelType, deviceType}
	 * @param request
	 * @return{win:false, code:''}
	 */
	@WebMethod
	@POST
	@Path("/draw")
	public Result draw(Hyjt1506Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A06: 保存中奖人信息
	 * @param {sid, userId, token, channelType, name,phone, identityNum, address, postCode}
	 * @param request
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/winInfo")
	public Result winInfo(Hyjt1506Parameter parameter);
	
	
	/**
	 * 下载中奖名单
	 * @param request
	 * @param response
	 */
	@WebMethod
	@GET
    @Path("/downloadWins")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void downloadWins(@Context HttpServletRequest request, @Context HttpServletResponse response);

	
	
	/**
	 * A08: 获取第三方微信State
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/getThirdWechatState")
	public Result getThirdWechatState(Hyjt1506Parameter parameter);
	
	
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


}
