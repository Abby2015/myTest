package com.yum.kfc.brand.kbm.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.alibaba.fastjson.JSONObject;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.util.StringUtil;
import com.yum.kfc.brand.ErrCode;
import com.yum.kfc.brand.api.HoperunApiServet;
import com.yum.kfc.brand.common.api.BaseCampApiServlet;
import com.yum.kfc.brand.crm.pojo.ClientChannel;

@WebServlet(urlPatterns = "/api/order/*", asyncSupported=true)
public class OrderApiServlet extends BaseCampApiServlet {
	
	private static final long serialVersionUID = 1L;
	
	@POST @Path("transaction")
	protected void transactionList(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject jo = (JSONObject) super.getBodyAsJson(req);
		final String token = jo==null?null:jo.getString("token");
		
		UserInfo ui = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(ui == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}
		final String userId = ui.getUserId();
		final String crmUserCode = ui.getCrmUserCode();
		
		final ClientChannel clientChannel = getClientChannel(req);
		final String clientIP = getClientIP(req);
		final int reqHashCode = req.hashCode();
		final OrderApiServlet thiz = this;
		super.asyncExec(req, resp, 120*1000, new SlowOperation(){
			@Override
			public Object exec() throws Exception {
				final Map<String, Object> result = HoperunApiServet.queryTransactionInfo(token, userId, crmUserCode, req, clientChannel, clientIP, reqHashCode, thiz);
				return result;
			}
		});
	}
	
	@POST @Path("orderDetail")
	protected void orderDetail(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject jo = (JSONObject) super.getBodyAsJson(req);
		final String token = jo==null?null:jo.getString("token");
		final String orderId = jo==null?null:jo.getString("orderId");
		final String activityId = jo==null?null:jo.getString("activityId");
		final String transTime = jo==null?null:jo.getString("transTime");
		
		if (StringUtil.isAnyEmptyWithTrim(orderId, activityId, transTime)) throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("exist empty param:[" + getBodyAsString(req)+"]");
		
		UserInfo ui = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(ui == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}
		final String userId = ui.getUserId();
		
		final ClientChannel clientChannel = getClientChannel(req);
		final String clientIP = getClientIP(req);
		final int reqHashCode = req.hashCode();
		super.asyncExec(req, resp, 120*1000, new SlowOperation(){
			@Override
			public Object exec() throws Exception {
				final Map<String, Object> result = HoperunApiServet.queryOrderDetail (orderId, activityId, transTime, token, userId, req, clientChannel, clientIP, reqHashCode);
				return result;
			}
		});
	}
	
}
