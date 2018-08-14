package com.yum.kfc.brand.camp1505.api;

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

import org.springframework.web.bind.annotation.ResponseBody;

import com.yum.kfc.brand.camp1505.api.impl.Camp1505Parameter;
import com.yum.kfc.brand.common.pojo.Result;

/**
 * 2015.5母亲节活动
 * @author luolix
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Camp1505Api {
	
	/**
	 * A01: 打开母亲节活动首页
	 * @param {userId, token, channelType[0: Brand App||1: 微信||2:浏览器], 
	 * deviceType[0: android||1:ios||2:browser], deviceId}
	 * @param request
	 * @return {会话标记：sid}
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Camp1505Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A02: 分享
	 * @param {sid, userId、token、channelType、deviceType、tasteOption, otherContent
	 * mediaType[分享媒介(QQ|WX|TWB|WB|RR|DB|TB|ZFB)]、shareUrl, shareResult[分享结果:0失败||1成功]}
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/share")
	public Result share(Camp1505Parameter parameter);
	
	
	/**
	 * A03: 抽奖
	 * @param {sid, userId, token, channelType, deviceType}
	 * @param request
	 * @return{win:false, code:''}
	 */
	@WebMethod
	@POST
	@Path("/draw")
	public Result draw(Camp1505Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A04: 保存中奖人信息
	 * @param {sid, userId, token, channelType, name,phone, identityNum, address, postCode}
	 * @param request
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/winInfo")
	public Result winInfo(Camp1505Parameter parameter);
	
	
	/**
	 * A07: 检查微信的回转地址
	 * @param state
	 * @param request
	 * @return
	 */
	@WebMethod
	@GET
	@Path("/getWechatCode")
	public Result getWechatCode(@QueryParam("state") String state);
	
	
	
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
	 * A09: 获取第三方微信State
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/getThirdWechatState")
	public Result getThirdWechatState(Camp1505Parameter parameter);
}
