package com.yum.kfc.brand.kbm.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.BaseHttpClient.HttpMethod;
import com.hp.jdf.ssm.util.BaseHttpClient.ResultHandler;
import com.hp.jdf.ssm.util.DateUtil;
import com.hp.jdf.ssm.util.StringUtil;
import com.yum.kfc.brand.ErrCode;
import com.yum.kfc.brand.api.HoperunApiServet;
import com.yum.kfc.brand.api.util.BrandHttpClientHelper;
import com.yum.kfc.brand.api.util.BrandHttpClientHelper.RemoteErrDetecter;
import com.yum.kfc.brand.api.util.BrandHttpClientHelper.Result;
import com.yum.kfc.brand.api.util.RSAUtils;
import com.yum.kfc.brand.common.api.BaseCampApiServlet;
import com.yum.kfc.brand.common.utils.CspAzureClient;
import com.yum.kfc.brand.crm.pojo.ClientChannel;
import com.yum.kfc.brand.user.api.service.UserApiService;
import com.yum.kfc.brand.user.service.impl.UserServiceHelper;

@WebServlet(urlPatterns = "/api/csp/*", asyncSupported=true)
public class CSPApiServlet extends BaseCampApiServlet {
	
	private static final Logger log = LoggerFactory.getLogger(CSPApiServlet.class);
	
	private static final long serialVersionUID = 1L;
	
	@Autowired UserApiService userService;
	
	protected static String IMG_ROOTDIR = ApplicationConfig.getProperty("api.csp.img.rootdir");
	protected static String IMG_ROOTURL = ApplicationConfig.getProperty("api.csp.img.rooturl");
	
	private static final int CSP_TIMEOUT_MS_CONNECT = Integer.parseInt(ApplicationConfig.getProperty("ws.csp.timeout.connect", "1000"));
	private static final int CSP_TIMEOUT_MS_READ = Integer.parseInt(ApplicationConfig.getProperty("ws.csp.timeout.read", "1000"));
	private static final boolean TRACE_CSP_SIGNATURE = Boolean.parseBoolean(ApplicationConfig.getProperty("trace.signature.csp", "false"));
	private static final boolean TRACE_RESP_BODY_CSP = Boolean.parseBoolean(ApplicationConfig.getProperty("trace.resp.body.csp", "false"));
	private static final boolean SAVE_CSP_SUCCESS_LOG_TO_DB = !Boolean.parseBoolean(ApplicationConfig.getProperty("log.request.failed.only.csp", "false"));
	private static final String CSP_LOG_DEST = ApplicationConfig.getProperty("log.request.dest.csp", "NONE");
	private static final Logger logCspReqElkJson = LoggerFactory.getLogger("ELK_REQ_LOG_CSP");
	
	private static final String CSP_SVC_ROOT = ApplicationConfig.getProperty("ws.csp.svc.root");
	private static final String CSP_APPID = ApplicationConfig.getProperty("ws.csp.svc.appid", "KFC_APP");
	private static final String CSP_APPSECRET = ApplicationConfig.getProperty("ws.csp.svc.appsecret", "6f8cda41e");
	private static final String CSP_PUBLIC_KEYSTORE = ApplicationConfig.getProperty("ws.csp.svc.pk", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCND1oR3h6jmVwMPIRXZDVERzWeN4dKSfgtaz/V5yCu66Dq7oAt2sxMmhilEiRorHBkG9t5Dv60aHwl6fvjudUJ7zOFoKDFxdYEPmTVqIC0zzZJ1J0mRNoFjRBTgbI7OVo0cVjvpeJ8j8eWjqqX4osE8xceu/w4MG7fBrRjygWxIwIDAQAB");
	private static final PublicKey CSP_PUBLIC_KEY = RSAUtils.getPublicKey(CSP_PUBLIC_KEYSTORE);
	private static final RSAUtils RSA = new RSAUtils();
	
	private static final String CSP_BLOB_CONTAINER_NAME = ApplicationConfig.getProperty("api.azure.bloc.container.name.csp", "cspuat");
	private static final boolean SAME_DISK_ACCROSS_NODES  = Boolean.parseBoolean(ApplicationConfig.getProperty("img.rootdir.sameDiskAccrossNodes", "false"));
	
	public static final String REGEX = "<([^>]*)>|\\(.*?\\)|[|&,;$%@\\\\+\\'\\\"\n\r]"; 
	
	@POST @Path("")
	private void createCsp(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		String reqContentType = req.getContentType();
		reqContentType = reqContentType==null?"":reqContentType.toLowerCase().trim();
		final boolean isJsonBody = reqContentType.startsWith("application/json");
	
		JSONObject jo = isJsonBody?(JSONObject) super.getBodyAsJson(req):null;
		if (isJsonBody && (jo==null || jo.isEmpty())){
			throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("missing param");
		}
		
		final String token = isJsonBody?jo.getString("token"):super.getStrParam(req, "token", null);
		Date happenTime = isJsonBody?jo.getDate("time"):TypeUtils.castToDate(super.getStrParam(req, "time", null));
		String storeCode = isJsonBody?jo.getString("storeId"):super.getStrParam(req, "storeId", null);
		String name = isJsonBody?jo.getString("name"):super.getStrParamPart(req, "name", null);
		String tel = isJsonBody?jo.getString("tel"):super.getStrParam(req, "tel", null);
		String detail = isJsonBody?jo.getString("detail"):super.getStrParamPart(req, "detail", null);
		String orderId = isJsonBody?jo.getString("orderId"):super.getStrParam(req, "orderId", null);
		String activityId = isJsonBody?jo.getString("activityId"):super.getStrParam(req, "activityId", null);
		String transTime = isJsonBody?jo.getString("transTime"):super.getStrParam(req, "transTime", null);
		
		if (StringUtil.isAnyEmptyWithTrim(token, happenTime==null?null:happenTime.toString(), storeCode, name, tel)) {
			throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("missing param ["+jo.toJSONString()+"]");
		}
		
		if (!StringUtil.isEmptyWithTrim(orderId)){
			if (StringUtil.isAnyEmptyWithTrim(activityId, transTime)){
				throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("missing param ["+jo.toJSONString()+"]");
			}
		}
		
		//过滤特殊字符
		filterSpecialChar(name, detail);

		JSONArray imgs = new JSONArray();
		if (isJsonBody){
			for(int i=1;i<=3;i++){
				String imgBase64 = jo.getString("img"+i);
				if (StringUtil.isEmptyWithTrim(imgBase64)) continue;
				byte[] imgBytes = Base64.decodeBase64(imgBase64);
				
				String filename = UUID.randomUUID().toString().replace("-", "");
				String path=UserServiceHelper.getImgPath(filename);
				File pf = new File(IMG_ROOTDIR+File.separatorChar+path);
				pf.getParentFile().mkdirs();
				
				OutputStream os = new BufferedOutputStream(new FileOutputStream(pf));
				os.write(imgBytes);
				os.close();
				
				String imgPath = null;
				if (!pf.exists()){
					log.error("new CSP image part saving said success, but the file do not exist [{}][{}]", pf, pf.getAbsoluteFile());
					continue;
				} else {
					imgPath = File.separatorChar=='\\'?path.replace(File.separatorChar, '/'):path;
					try{
						String containerName = CSP_BLOB_CONTAINER_NAME;
						String blobName = imgPath;
						String fn = pf.getName();
						uploadCspImgToAzureViaMQ(pf.getAbsolutePath(), fn==null?null:FILE_NAME_MAP.getContentTypeFor(fn), containerName, blobName, true);
					}catch(Exception e){
						log.error("request@{}, {}: upload csp img to azure failed [{}]", req.hashCode(), logTime(), StringUtil.getMsgOrClzName(e, true));
						if (log.isTraceEnabled()) log.trace("request@{}, {}: upload csp img to azure failed [{}]", req.hashCode(), logTime(), StringUtil.getMsgOrClzName(e, true), e);
					}
				}
				
				JSONObject ji = new JSONObject();
				ji.put("url", StringUtil.disableXSS(CspAzureClient.getBlobEndPoint()+CSP_BLOB_CONTAINER_NAME+"/"+imgPath, true));
				imgs.add(ji);
			}
		} else if (req.getContentType()!=null&&req.getContentType().toLowerCase().indexOf("multipart/form-data")>=0){
			for(int i=1;i<=3;i++){
				Part part = req.getPart("img"+i);
				if (part==null || part.getSize()<=0) continue;

				String filename = UUID.randomUUID().toString().replace("-", "");
				String path=UserServiceHelper.getImgPath(filename);
				File pf = new File(IMG_ROOTDIR+File.separatorChar+path);
				pf.getParentFile().mkdirs();
				super.savePart(part, pf);
				String imgPath = null;
				if (!pf.exists()){
					log.error("new CSP image part saving said success, but the file do not exist [{}][{}]", pf, pf.getAbsoluteFile());
					continue;
				} else {
					imgPath = File.separatorChar=='\\'?path.replace(File.separatorChar, '/'):path;
					try{
						String containerName = CSP_BLOB_CONTAINER_NAME;
						String blobName = imgPath;
						String fn = pf.getName();
						uploadCspImgToAzureViaMQ(pf.getAbsolutePath(), fn==null?null:FILE_NAME_MAP.getContentTypeFor(fn), containerName, blobName, true);
					}catch(Exception e){
						log.error("request@{}, {}: upload csp img to azure failed [{}]", req.hashCode(), logTime(), StringUtil.getMsgOrClzName(e, true));
						if (log.isTraceEnabled()) log.trace("request@{}, {}: upload csp img to azure failed [{}]", req.hashCode(), logTime(), StringUtil.getMsgOrClzName(e, true), e);
					}
				}
				
				JSONObject ji = new JSONObject();
				ji.put("url", StringUtil.disableXSS(CspAzureClient.getBlobEndPoint()+CSP_BLOB_CONTAINER_NAME+"/"+imgPath, true));
			}
		}
		
		final String clientIP = getClientIP(req);
		final ClientChannel clientChannel = getClientChannel(req);
		final int reqHashCode = req.hashCode();
		
		UserInfo ui = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(ui == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}
		final String userId = ui.getUserId();
		final String ssoUserId = ui.getSsoUserId();
		final String crmUserCode = ui.getCrmUserCode();
		final String phone = ui.getPhone();
		
		Map<String, Object> orderDetail = null;
		Object orderDetailFromRH = null;
		if (!StringUtil.isEmptyWithTrim(orderId)){
			orderDetail = HoperunApiServet.queryOrderDetail(orderId, activityId, transTime, token, userId, req, clientChannel, clientIP, reqHashCode);
			if(orderDetail != null){
				orderDetailFromRH = orderDetail.get("rhResp");
			}
		}
		
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", "0");//事件类型，0-投诉
		//params.put("sourceId", "j9283jda2"); //KFC系统唯一标识，如果存在
		params.put("sourceUserCode", crmUserCode); //各业务系统自己usercode，如KFC系统的user code等等。非必填
		params.put("diningWay", "0"); //就餐方式, 0-堂食，1-外带, KFC只用“0-堂食”分类
		params.put("incidentDateStr", DateUtil.formatDate(happenTime, "yyyy-MM-dd HH:mm")); //事件发生时间, 2018-01-15 16:00
		//params.put("deptId", storeCode); //事件发生的餐厅ID,对应餐厅ID，从YUM主数据来,
		params.put("deptNo", storeCode); //餐厅编号，有餐厅时
		params.put("businessUnitId", "CNKFC"); //品牌编号，来自主数据
		params.put("customerName", name); //客户姓名
		params.put("customerTelephone", tel); //客户电话
		params.put("incidentDetails", detail); //对应页面上的投诉内容, 如：本次在这个餐厅的用餐非常不愉快，服务员的态度特别差...
		params.put("orderDetails", JSON.toJSONString(orderDetailFromRH)); //订单详情, 如：外带全家桶，单价118，备注可乐不要冰块
		params.put("imgList", imgs); //投诉照片: https://o5wwk8baw.qnssl.com/a42bdcc1178e62b4694c830f028db5c0/large ":[{"url":""},{"url",""},...]
		//params.put("additionParams":{}                         //扩展字段
		
		final String URL = CSP_SVC_ROOT+"/api/cs/incident/create?sign="+URLEncoder.encode(sign(req.hashCode(), req), "UTF-8");
		super.asyncExec(req, resp, new SlowOperation(){
			@Override
			public Object exec() throws Exception {
				callCspSvc(HttpMethod.POST, URL, params, ssoUserId, userId, token, null, crmUserCode, phone, reqHashCode, clientIP, null, clientChannel, req, new ResultHandler(){
					@Override
					public void handle(String resultFromHttp, HttpResponse rawResp) {
						//{"errorCode":"0",//0成功, 1失败    "errorMessage":"" //错误消息  "data":{"id":"UUID", //事件UUID "incidentNo": 201801150110001 //事件编号 }}
						JSONObject jo = JSON.parseObject(resultFromHttp);
						req.setAttribute(RESULT, jo.getJSONObject("data"));
					}});
				return req.getAttribute(RESULT);
			}});
	}
	
	private void filterSpecialChar(String...params) {
		for (String str: params) {
			if (str != null) {
				Pattern pattern = Pattern.compile(REGEX);  
			    Matcher matcher = pattern.matcher(str);
			    if (matcher.find()) {
//			    	String specialCharStr = String.valueOf(str.charAt(matcher.start()));
//			    	String errMsg = "您输入的内容中包含特殊字符: %1$s, 保存失败";
//			    	if (specialCharStr.equals("<")) {
//						errMsg = String.format(errMsg, "<>");
//					} else if (specialCharStr.equals("(")) {
//						errMsg = String.format(errMsg, "()");
//					}else {
//						errMsg =  String.format(errMsg, specialCharStr);
//					}
			    	
			    	String errMsg = "输入的内容不能包含特殊字符, 例如: |&;$%@'\"\\'\\\"<>()+,\\以及换行回车等"; 
			    	
			    	throw new ApiException(errMsg, ErrCode.SPECIAL_CHAR).setContext("special chars, param:["+str+"]");
				}
			}
		}
	}

	protected void uploadCspImgToAzureViaMQ(final String fileAbsolutePath, final String contentType, final String containerName, final String blobName, final boolean deleteAfterUpload){
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Map<String, String> params = new HashMap<String, String>();
			params.put("filePath", fileAbsolutePath);
			params.put("containerName", containerName);
			params.put("blobName", blobName);
			params.put("contentType", contentType);
			params.put("origin", LOCAL_IP);
			params.put("deleteAfterUpload", ""+deleteAfterUpload);
			params.put("sameDiskAccrossNodes", ""+SAME_DISK_ACCROSS_NODES);
			Message msg = new Message(Message.Type.CREATE, params);
			RabbitMQHelper.publish(CspAzureClient.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					uploadCspImgToAzure(fileAbsolutePath, contentType, containerName, blobName, deleteAfterUpload);
				}
			});
		} else {
			uploadCspImgToAzure(fileAbsolutePath, contentType, containerName, blobName, deleteAfterUpload);
		}
	}
	
	public static void uploadCspImgToAzure(String fileAbsolutePath, String contentType, String containerName, String blobName, boolean deleteAfterUpload){
		File f = new File(fileAbsolutePath);
		try {
			CspAzureClient.uploadBlob(f, contentType, containerName, blobName, false);//container inited in static block already
		} catch (Throwable e) {
			throw new ApiException("Remote Azure Server Error", ErrCode.GENERAL_SERVER_ERROR_AZS, e).setContext(StringUtil.getMsgOrClzName(e, true));
		}
		
		if (deleteAfterUpload){
			try{
				f.delete();
			}catch(Throwable e){
				log.warn("failed to delete file uploaded to azure storage already {}", StringUtil.getMsgOrClzName(e, true), e);
			}
		}
	}
	
	private static String sign(int reqHashcode, HttpServletRequest req){
		String forSign = CSP_APPID+"&"+CSP_APPSECRET+"&"+System.currentTimeMillis();
		
		byte[] bytes = RSA.encrypt(forSign.getBytes(), CSP_PUBLIC_KEY);
		
		String sign = Base64.encodeBase64String(bytes);
		
		if(log.isTraceEnabled()){
			if (TRACE_CSP_SIGNATURE || (req!=null && "1".equals(req.getHeader("tracesigncsp")))){
				log.trace("request@{}, {}: string for signature is [{}], signature is [{}]", reqHashcode, logTime(), forSign, sign);
			}
		}
		
		return sign;
	}
	
	@SuppressWarnings("rawtypes")
	private static void callCspSvc(HttpMethod method, final String svcUrl, final Map params,
			final String ssoUserId, final String brandUserId, final String brandToken, final String openid, final String crmUserCode, final String phone,
			final int reqHashCode, final String clientIP, final String terminalID, final ClientChannel clientChannel, final HttpServletRequest req,
			final ResultHandler rh){
		
		ResultHandler commonCspResultHandler = new ResultHandler(){
			@Override
			public void handle(String resultFromHttp, HttpResponse rawResp) {
				int statusCode = BrandHttpClientHelper.getStatusCode(rawResp);
				
				String[] err = getCspErr(resultFromHttp);//[0] errCode from CSP, [1] errMsg from CSP
				switch (statusCode){
					case 200:
						if("0".equals(err[0])){
							if(rh!=null) rh.handle(resultFromHttp, rawResp);
							break;
						}
					default:
						throw new ApiException(err[1], ErrCode.GENERAL_SERVER_ERROR_CSP+(err[0]==null?statusCode:Integer.parseInt(err[0])))
									.setContext("remote CSP service return un-defined status code ["+statusCode+"], result from http ["+resultFromHttp+"]");
				}
			}};
		
		final boolean traceRespBodyFromHeader =  req==null?true:"1".equals(req.getHeader("tracerespcsp"));
		BrandHttpClientHelper.callAndLogging(
				method, svcUrl, null, params==null?null:JSON.toJSONString(params), HttpMethod.GET.equals(method)?null:ContentType.APPLICATION_JSON,
				CSP_TIMEOUT_MS_CONNECT, CSP_TIMEOUT_MS_READ, "CSP", ErrCode.GENERAL_SERVER_ERROR_CSP,
				""+reqHashCode, clientIP, clientChannel, openid, brandToken, phone, brandUserId, ssoUserId, crmUserCode, null, terminalID, null,
				commonCspResultHandler, new RemoteErrDetecter(){
					@Override
					public String[] detectErrInRespBody(Result result) {
						return getCspErr(result.responseBody);
					}},
				req, log, TRACE_RESP_BODY_CSP||traceRespBodyFromHeader, false, SAVE_CSP_SUCCESS_LOG_TO_DB, CSP_LOG_DEST,
				"cspLog", logCspReqElkJson, null, null);
	}
	
	/**
	 * return: [0] errCode from CSP, [1] errMsg from CSP
	 */
	protected static String[] getCspErr(String resultFromHttp) {
		String errMsg = "Remote CSP Server Error";
		String errCode = null;
		try{
			JSONObject jo = JSON.parseObject(resultFromHttp);
			errCode = jo.getString("errorCode");
			errMsg = jo.getString("errorMessage");
		}catch(Exception ignore){}
		
		return new String[]{errCode, StringUtil.isEmptyWithTrim(errMsg)?"Remote CSP Server Error":errMsg};
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@POST @Path("sas")
	private void generateSAS(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject jo = (JSONObject) super.getBodyAsJson(req);
		if (jo==null || jo.isEmpty()) {
			throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("missing param");
		}
		
		String token = jo.getString("token");
		if (StringUtil.isEmptyWithTrim(token)) {
			throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("missing param");
		}
		
		UserInfo ui = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(ui == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}
		
		String sas = CspAzureClient.createSAS4BlobContainer(CSP_BLOB_CONTAINER_NAME);
		Map result = new HashMap();
		result.put("sas", sas);
		result.put("ttlMinutes", CspAzureClient.SAS_TTL_MS/1000/60);
		
		req.setAttribute(RESULT, result);
	}
	
	@POST @Path("__clusterNodeCall512_upld_file_to_azure_")
	private void uploadCspFileToAzure(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		JSONObject jo = (JSONObject) super.getBodyAsJson(req);
		
		final String fileAbsolutePath = jo.getString("filePath");
		final String containerName = jo.getString("containerName");
		final String blobName = jo.getString("blobName");
		final String contentType = jo.getString("contentType");
		final boolean deleteAfterUpload = jo.getBooleanValue("deleteAfterUpload");
		
		if (!fileAbsolutePath.startsWith(ROOTDIR_IMG)||fileAbsolutePath.indexOf("..")>=0) {
			log.warn("request@{}, {}: SUSPECIOUS REQUEST, do nothing and abort from request [{}]", req.hashCode(), logTime(), jo.toJSONString());
			return;
		}
		
		try{
			CspAzureClient.checkContainerName(containerName);
			CspAzureClient.checkBlobName(blobName);
		}catch(IllegalArgumentException e){
			log.warn("request@{}, {}: invalid azure message, discard, [{}]", req.hashCode(), logTime(), jo.toJSONString(), e);
			return;
		}
		
		super.asyncExec(req, resp, 120*1000, new SlowOperation(){
			@Override
			public Object exec() throws Exception {
				uploadCspImgToAzure(fileAbsolutePath, contentType, containerName, blobName, deleteAfterUpload);
				return null;
			}
		});
	}
}
