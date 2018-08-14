package com.yum.kfc.brand.bmh1506.api;

import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.yum.kfc.brand.bmh1506.api.impl.Bmh1506Parameter;
import com.yum.kfc.brand.camp1504.api.impl.Camp1504Parameter;
import com.yum.kfc.brand.common.pojo.Result;

/**
 * 2015年6月爆米花活动
 * @author luolix
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Bmh1506Api {
	
	/**
	 * A01: 打开活动首页
	 * @param {userId, channelType, deviceType, deviceId}
	 * @param request
	 * @return {sid}
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Bmh1506Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A02: 弹弓游戏结束
	 * @param {sid, userId, channelType, deviceType, hitCount}
	 * @param request
	 * @return {result：结果(0: 游戏成功，1：没券；2：游戏失败)}
	 */
	@WebMethod
	@POST
	@Path("/ask")
	public Result ask(Bmh1506Parameter parameter);
	
	
	/**
	 * A03: 我的半价券
	 * @param {userId、channelType}
	 * @return 
	 */
	@WebMethod
	@GET
	@Path("/myCoupon")
	public Result myCoupon(@QueryParam("userId") String userId, @QueryParam("channelType") Integer channelType);
	
	
	/**
	 * A04: 收入囊中 / 保存至卡包
	 * @param {promoCode}
	 * @param request
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/collect")
	public Result collect(Bmh1506Parameter parameter);
	
	
	/**
	 * A05: 分享
	 * @param {sid, userId、token、menuType、channelType、deviceType, mediaType, shareUrl, shareResult}
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/share")
	public Result share(Bmh1506Parameter parameter);
	
	
	/**
	 * A06: 抽奖
	 * @param {sid, userId, channelType, deviceType}
	 * @param request
	 * @return{win:false, code:''}
	 */
	@WebMethod
	@POST
	@Path("/draw")
	public Result draw(Bmh1506Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A07: 保存中奖人信息
	 * @param {sid, userId, token, channelType, name,phone, identityNum, address, postCode}
	 * @param request
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/winInfo")
	public Result winInfo(Bmh1506Parameter parameter);
	
	
	/**
	 * 发送用户充值手机号消息
	 * @param {}
	 * @param request
	 * @return {result：结果(0: 游戏成功，1：没券；2：游戏失败)}
	 */
	@WebMethod
	@GET
	@Path("/sendWinNotPhoneMsg")
	public Result sendWinNotPhoneMsg();
	
	
	/**
	 * A08: 获取第三方微信State
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/getThirdWechatState")
	public Result getThirdWechatState(Bmh1506Parameter parameter);
	
	
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
