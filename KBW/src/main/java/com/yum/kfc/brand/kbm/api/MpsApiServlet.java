package com.yum.kfc.brand.kbm.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.util.BaseHttpClient.HttpMethod;
import com.hp.jdf.ssm.util.BaseHttpClient.ResultHandler;
import com.hp.jdf.ssm.util.StringUtil;
import com.yum.kfc.brand.ErrCode;
import com.yum.kfc.brand.api.util.BrandHttpClientHelper;
import com.yum.kfc.brand.api.util.BrandHttpClientHelper.RemoteErrDetecter;
import com.yum.kfc.brand.api.util.BrandHttpClientHelper.Result;
import com.yum.kfc.brand.common.api.BaseCampApiServlet;
import com.yum.kfc.brand.crm.pojo.ClientChannel;

@WebServlet(urlPatterns = "/api/mps/*", asyncSupported=true)
public class MpsApiServlet extends BaseCampApiServlet {//Member Profile Service
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(MpsApiServlet.class);

	private static final String MPS_SVC_ROOT = ApplicationConfig.getProperty("ws.mps.svc.root", "http://172.29.164.199");
	private static final String MPS_APPID = ApplicationConfig.getProperty("ws.mps.svc.appid", "68bfe4d4dc2ba278d3d2e1e86a8a6727");
	
	private static final int MPS_TIMEOUT_MS_CONNECT = Integer.parseInt(ApplicationConfig.getProperty("ws.mps.timeout.connect", "1000"));
	private static final int MPS_TIMEOUT_MS_READ = Integer.parseInt(ApplicationConfig.getProperty("ws.mps.timeout.read", "1000"));
	private static final boolean TRACE_MPS_SIGNATURE = Boolean.parseBoolean(ApplicationConfig.getProperty("trace.signature.mps", "false"));
	private static final boolean TRACE_RESP_BODY_MPS = Boolean.parseBoolean(ApplicationConfig.getProperty("trace.resp.body.mps", "false"));
	private static final boolean SAVE_MPS_SUCCESS_LOG_TO_DB = !Boolean.parseBoolean(ApplicationConfig.getProperty("log.request.failed.only.mps", "false"));
	private static final String MPS_LOG_DEST = ApplicationConfig.getProperty("log.request.dest.mps", "DB");
	private static final Logger logMpsReqElkJson = LoggerFactory.getLogger("ELK_REQ_LOG_MPS");
	
	@POST @Path("tag")
	private void getTag(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		JSONObject jo = (JSONObject) super.getBodyAsJson(req);
		if (jo==null || jo.isEmpty()) {
			throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("missing param");
		}
		
		final String token = jo.getString("token");
		String tagId = jo.getString("tagId");
		
		if (StringUtil.isAnyEmptyWithTrim(token, tagId)) {
			throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("missing param");
		}

		final String clientIP = getClientIP(req);
		final ClientChannel clientChannel = getClientChannel(req);
		final int reqHashCode = req.hashCode();
		
		final UserInfo ui = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(ui == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}
//		final UserInfo ui = new UserInfo("userId", "ssoUserId", "crmUserCode", "phone");
		
		StringBuilder forSign = new StringBuilder("{")//{p1=v1_p2=v2}caller_id
			.append("brand_id=KFC")
			.append("_tag_id=").append(tagId)
			.append("_user_id=").append(ui.getCrmUserCode())
			.append("_user_id_type=kfc_usercode")
			.append("}").append(MPS_APPID);
		
		String sign = DigestUtils.md5Hex(forSign.toString()).toUpperCase();
		
		if(log.isTraceEnabled()){
			if (TRACE_MPS_SIGNATURE || (req!=null && "1".equals(req.getHeader("tracesignmps")))){
				log.trace("request@{}, {}: string for signature is [{}], signature is [{}]", reqHashCode, logTime(), forSign, sign);
			}
		}
		
		final Map<String, String> headers = new HashMap<String, String>();
		headers.put("caller_id", MPS_APPID);
		headers.put("timestamp", ""+System.currentTimeMillis());
		headers.put("signature", sign);
		
		//http://{service_name}/profile/tag/{brand_id}/{user_id}/{user_id_type}/{tag_id}
		final String URL = MPS_SVC_ROOT+"/profile/tag/KFC/"+ui.getCrmUserCode()+"/kfc_usercode/"+URLEncoder.encode(tagId, "UTF-8");
		super.asyncExec(req, resp, new SlowOperation(){
			@Override
			public Object exec() throws Exception {
				callMpsSvc(HttpMethod.GET, URL, headers, null, ui.getSsoUserId(), ui.getUserId(), token, null, ui.getCrmUserCode(), ui.getPhone(), reqHashCode, clientIP, null, clientChannel, req, new ResultHandler(){
					@Override
					public void handle(String resultFromHttp, HttpResponse rawResp) {
						JSONObject jo = JSON.parseObject(resultFromHttp);
						JSONObject body = jo.getJSONObject("body");
						body.remove("brand_id");
						body.remove("user_id");
						body.remove("user_id_type");
						req.setAttribute(RESULT, body);
					}});
				return req.getAttribute(RESULT);
			}
		});
	}
	
	@SuppressWarnings("rawtypes")
	private static void callMpsSvc(HttpMethod method, final String svcUrl, final Map<String, String> headers, final Map params,
			final String ssoUserId, final String brandUserId, final String brandToken, final String openid, final String crmUserCode, final String phone,
			final int reqHashCode, final String clientIP, final String terminalID, final ClientChannel clientChannel, final HttpServletRequest req,
			final ResultHandler rh){
		
		ResultHandler commonMpsResultHandler = new ResultHandler(){
			@Override
			public void handle(String resultFromHttp, HttpResponse rawResp) {
				int statusCode = BrandHttpClientHelper.getStatusCode(rawResp);
				
				String[] err = getMpsErr(resultFromHttp);//[0] errCode from MPS, [1] errMsg from MPS
				switch (statusCode){
					case 200:
						if("2000".equals(err[0])){
							if (rh!=null) rh.handle(resultFromHttp, rawResp);
							break;
						}
					default:
						throw new ApiException(err[1], ErrCode.GENERAL_SERVER_ERROR_MPS+(err[0]==null?statusCode:Integer.parseInt(err[0])))
									.setContext("remote MPS service return un-defined status code ["+statusCode+"], result from http ["+resultFromHttp+"]");
				}
			}};
		
		final boolean traceRespBodyFromHeader =  req==null?true:"1".equals(req.getHeader("tracerespmps"));
		BrandHttpClientHelper.callAndLogging(
				method, svcUrl, headers, params==null?null:JSON.toJSONString(params), HttpMethod.GET.equals(method)?null:ContentType.APPLICATION_JSON,
				MPS_TIMEOUT_MS_CONNECT, MPS_TIMEOUT_MS_READ, "MPS", ErrCode.GENERAL_SERVER_ERROR_MPS,
				""+reqHashCode, clientIP, clientChannel, openid, brandToken, phone, brandUserId, ssoUserId, crmUserCode, null, terminalID, null,
				commonMpsResultHandler, new RemoteErrDetecter(){
					@Override
					public String[] detectErrInRespBody(Result result) {
						return getMpsErr(result.responseBody);
					}},
				req, log, TRACE_RESP_BODY_MPS||traceRespBodyFromHeader, false, SAVE_MPS_SUCCESS_LOG_TO_DB, MPS_LOG_DEST,
				"mpsLog", logMpsReqElkJson, null, null);
	}
	
	/**
	 * return: [0] errCode from MPS, [1] errMsg from MPS
	 */
	protected static String[] getMpsErr(String resultFromHttp) {
		String errMsg = "Remote MPS Server Error";
		String errCode = null;
		try{
			JSONObject jo = JSON.parseObject(resultFromHttp);
			errCode = jo.getString("code");
			errMsg = jo.getString("messages");
		}catch(Exception ignore){}
		
		return new String[]{errCode, StringUtil.isEmptyWithTrim(errMsg)?"Remote MPS Server Error":errMsg};
	}

}
