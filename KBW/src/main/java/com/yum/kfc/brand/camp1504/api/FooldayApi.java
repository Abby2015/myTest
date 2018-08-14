package com.yum.kfc.brand.camp1504.api;

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

import com.yum.kfc.brand.camp1504.api.impl.Camp1504Parameter;
import com.yum.kfc.brand.common.pojo.Result;

/**
 * 2015愚人节活动
 * @author luolix
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface FooldayApi {
	
	/**
	 * A01: 打开愚人节活动首页
	 * @param {userId, token, channelType[0: Brand App||1: 微信||2:浏览器], 
	 * deviceType[0: android||1:ios||2:browser], deviceId, tag[memo：from /answer get tag]}
	 * @param request
	 * @return {会话标记：sid}
	 */
	@WebMethod
	@POST
	@Path("/open")
	public Result open(Camp1504Parameter parameter, @Context HttpServletRequest request);
	
	
	/**
	 * A02: 随机给出一个问题
	 * @param {sid, userId, token, recycleCount[问题轮循数次：首页问题初始值传递1，下一题轮循次数从该方法的对应的返回值中获得]
	 * tag[点击分享链接的首页问题才传tag，后续问题不需要tag参数]}
	 * @return {问题Id：questionId, 问题编码：code，问题答案：correct[0:不靠谱 || 1:靠谱]，
	 * 答对概率：yesPercent, 答错概率：noPercent，未答概率：naPercent， 
	 * recycleCount[问题目前轮循次数，作为/anwser和/getQuestion方法的输入参数}
	 */
	@WebMethod
	@POST
	@Path("/getQuestion")
	public Result getQuestion(Camp1504Parameter parameter);
	
	
	/**
	 * A03: 用户提交问题答案
	 * @param {sid, userId、token, channelType, questionId, recycleCount[问题轮循次数]
	 * choice[1靠谱||0不靠谱||-1未选择], isCorrect[用户是否答对：1：正确，0：错误，-1：未选择]}
	 * （isCorrect的值需要在前台通过用户的选择choice和/getQuestion中问题答案correct来判断该值）
	 * @return {tag}
	 */
	@WebMethod
	@POST
	@Path("/answer")
	public Result answer(Camp1504Parameter parameter);
	
	
	/**
	 * A04: 分享
	 * @param {sid, userId、token、channelType、deviceType、deviceId、
	 * mediaType[分享媒介(QQ|WX|TWB|WB|RR|DB|TB|ZFB)]、shareUrl, shareResult[分享结果:0失败||1成功]}
	 * (分享地址shareUrl后面需要带tag参数)
	 * @return {}
	 */
	@WebMethod
	@POST
	@Path("/share")
	public Result share(Camp1504Parameter parameter);
	
	
	/**
	 * A05: 领奖
	 * @param {sid, userId, token, channelType, deviceType, deviceId}
	 * @param request
	 * @return{gift: 1话费||0优惠券[优惠券直接页面处理]}
	 */
	@WebMethod
	@POST
	@Path("/draw")
	public Result draw(Camp1504Parameter parameter, @Context HttpServletRequest request);
	
	
	
	/**
	 * A05-1: 验证是中奖用户是否没有填写手机号码
	 * @param {userId, channelType}
	 * @param request
	 * @return{notPhone: true中奖没有手机号码||false中奖有手机号码}
	 */
	@WebMethod
	@GET
	@Path("/isWinNotPhone")
	public Result isWinNotPhone(@QueryParam("userId") String userId, 
			@QueryParam("channelType") Integer channelType);
	
	
	/**
	 * A05-2: 填写手机号码
	 * @param {userId, channelType, phone}
	 * @param request
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/updateWinPhone")
	public Result updateWinPhone(Camp1504Parameter parameter);
	
	/**
	 * A06: 提交中奖手机号码
	 * @param {sid, userId, token, channelType, phone}
	 * @param request
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/winPhone")
	public Result winPhone(Camp1504Parameter parameter);
	
	
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
	 * 受邀人微信分享入口页面
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	@GET
	@Path("/share.do")
	public void share(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception ;
	
	
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
	
	
	/**
	 * wx_jssdk  获取 signature
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	@GET
	@Path("/getTicket.do")
	public @ResponseBody Result getTicket(@Context HttpServletRequest request) throws Exception ;
	
	
	/**
	 * A09: 获取第三方微信State
	 * @return
	 */
	@WebMethod
	@POST
	@Path("/getThirdWechatState")
	public Result getThirdWechatState(Camp1504Parameter parameter);

}
