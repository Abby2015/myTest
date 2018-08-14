package com.yum.kfc.brand.kbm.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.http.HttpResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.cache.CacheUtil;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.SpringUtil;
import com.hp.jdf.ssm.util.StringUtil;
import com.hp.jdf.ssm.util.BaseHttpClient.ResultHandler;
import com.yum.kfc.brand.ErrCode;
import com.yum.kfc.brand.ai.pojo.AiUserCoupon;
import com.yum.kfc.brand.ai.pojo.KeywordEffectRela;
import com.yum.kfc.brand.ai.pojo.QuickGreeting;
import com.yum.kfc.brand.ai.service.AiService;
import com.yum.kfc.brand.api.CRMApiServlet;
import com.yum.kfc.brand.api.util.ApiHelper;
import com.yum.kfc.brand.api.util.SsoApiHelper;
import com.yum.kfc.brand.common.api.BaseCampApiServlet;
import com.yum.kfc.brand.crm.pojo.ClientChannel;
import com.yum.kfc.brand.link.pojo.Link;
import com.yum.kfc.brand.link.service.LinkService;
import com.yum.kfc.brand.settings.pojo.Settings;
import com.yum.kfc.brand.settings.service.CommonService;

@WebServlet(urlPatterns = "/api/ai/*", asyncSupported = true)
public class AIApiServlet extends BaseCampApiServlet {
	private static final long serialVersionUID = 3188533491751206941L;
	
	private static final AiService aiService = SpringUtil.getBean(AiService.class);
	private static final CommonService commonService = SpringUtil.getBean(CommonService.class);
	private static final LinkService linkService = SpringUtil.getBean(LinkService.class);

	@GET @Path("help")
	private void help(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ts = getStrParam(req, "ts", null);
		
		Object data = super.getOrGenCacheableData("AI_HELP", ts, Integer.MAX_VALUE, new CacheableDataGenerator(){
			@Override
			public CacheableData generate() {
				CacheableData cd = new CacheableData();
				cd.timestamp = new Date().getTime();
				cd.data = createHelpData(cd.timestamp);
				return cd;
			}
			
		});
		
		req.setAttribute(RESULT, data==null?("{\"ts\":\""+ts+"\"}"):data);
	}
	
	@GET @Path("greeting")
	private void greetings(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ts = getStrParam(req, "ts", null);
		
		Object data = super.getOrGenCacheableData("AI_GREETING", ts, Integer.MAX_VALUE, new CacheableDataGenerator(){
			@Override
			public CacheableData generate() {
				CacheableData cd = new CacheableData();
				cd.timestamp = new Date().getTime();
				cd.data = createGreetData(cd.timestamp);
				return cd;
			}
			
		});
		
		req.setAttribute(RESULT, data==null?("{\"ts\":\""+ts+"\"}"):data);
	}
	
	@GET @Path("keyword")
	private void keyword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ts = getStrParam(req, "ts", null);
		
		Object data = super.getOrGenCacheableData("AI_KEYWORD", ts, Integer.MAX_VALUE, new CacheableDataGenerator(){
			@Override
			public CacheableData generate() {
				CacheableData cd = new CacheableData();
				cd.timestamp = new Date().getTime();
				cd.data = createKeywordData(cd.timestamp);
				return cd;
			}
			
		});
		
		req.setAttribute(RESULT, data==null?("{\"ts\":\""+ts+"\"}"):data);
	}
	
	@POST @Path("placeCoupon")
	private void campPlaceCoupon(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject jo = (JSONObject) super.getBodyAsJson(req);
		final String token = jo==null?null:jo.getString("token");
		final String keywordId = jo==null?null:jo.getString("keywordId");
		
		if(StringUtil.isAnyEmptyWithTrim(token, keywordId)){
			throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("exist empty param:[" + getBodyAsString(req)+"]");
		}
		
		UserInfo ui = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(ui == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}
		
		final String userId = ui.getUserId();
		final String ssoUserId = ui.getSsoUserId();
		final String crmUserCode = ui.getCrmUserCode();
		final String phone = ui.getPhone();
		final String clientIP = getClientIP(req);
		final int reqHashCode = req.hashCode();
		final ClientChannel clientChannel = getClientChannel(req);
		@SuppressWarnings("rawtypes")
		Map param = SsoApiHelper.buildRcsParam("SCENARIO0003", "EVTTYP0028", null, SsoApiHelper.getRcsSystem(clientChannel), null,
												phone, phone, ssoUserId, token, null);
		SsoApiHelper.callRcsSvc(param, phone, ssoUserId, userId, token, null, crmUserCode,
								reqHashCode, clientIP, null, clientChannel, req, new ResultHandler(){
			@Override
			public void handle(String resultFromHttp, HttpResponse rawResp) {
				JSONObject jo = JSON.parseObject(resultFromHttp);
				int ec = jo.getIntValue("eval_code");
				if (ec == 99){
					//do not change "RCS Failed" to other value, it is used in SsoApiHelper.callRcsSvc()
					throw new ApiException("RCS Failed", ErrCode.GENERAL_SERVER_ERROR_RCS+ec).setContext(resultFromHttp);
				}
			}
		});
		
		final KeywordEffectRela keyword = getAIKeyword(Long.valueOf(keywordId));
		if (keyword != null) {
			int limitCount = keyword.getSendCouponMax();
			int userDrawCount = getUserDrawCount(keyword.getId(), ui.getUserId());
			if(userDrawCount >= limitCount){
				throw new ApiException("Bad request", ErrCode.REACH_LIMIT).setContext("User has gotten ["+limitCount+"]already"); 
			}else {
				String activityIdstr = keyword.getActivityIds();
				if (StringUtil.isEmptyWithTrim(activityIdstr)){
					throw new ApiException("keyword has no activity IDs", ErrCode.NO_CONFIGURATION).setContext(keyword);
				}
				
				final List<String> activityIds= new ArrayList<String>();
				String[] splitActIds = activityIdstr.split(",");
				for(String str: splitActIds){
					if (StringUtil.isNotBlank(str)){
						activityIds.add(str);
					}
				}
				
				super.asyncExec(req, resp, 120*1000, new SlowOperation(){
					@Override
					public Object exec() throws Exception {
						final Map<String, Object> result = new HashMap<String, Object>();
						Object couponCodes = CRMApiServlet.placeCoupons(req, reqHashCode, activityIds, crmUserCode, token, userId, null, clientIP,null,null, false);
						if(couponCodes != null){
							AiUserCoupon coupon = new AiUserCoupon();
							coupon.setKeywordId(keywordId);
							coupon.setUserId(userId);
							coupon.setSsoUserId(ssoUserId);
							coupon.setCrmUserCode(crmUserCode);
							coupon.setActivityIds(keyword.getActivityIds());
							coupon.setCouponCodes(String.valueOf(couponCodes));
							coupon.setDrawTime(new Date());
							coupon.setClientIP(clientIP);
							
							saveUserCoupon(coupon);
							
							if (CacheUtil.get(CacheUtil.getDefaultCacheName(), "AI_USER_DRAW_COUNT_"+userId+"_"+keywordId) != null){
								CacheUtil.incr(CacheUtil.getDefaultCacheName(), "AI_USER_DRAW_COUNT_"+userId+"_"+keywordId, 1);
							}
						}
						
						result.put("couponCodes", couponCodes);
						return result;
					}
				});
			}
		}
		
	}

	private Object createHelpData(Long timestamp) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("ts", timestamp);
		
		Settings aiHelpSettings = commonService.getSettingsByKey(Settings.SettingsKey.aiHelp);
		if (aiHelpSettings != null) {
			data.put("help", aiHelpSettings.getValue());
		}
		
		return data;
	}
	
	private Object createGreetData(Long timestamp) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("ts", timestamp);
		
		List<Map<String, Object>> greetingsList = new ArrayList<Map<String, Object>>();
		data.put("greetings", greetingsList);
		
		List<QuickGreeting> greetings = aiService.getAllGreetings();
		if(greetings != null) {
			for(QuickGreeting greeting: greetings) {
				Map<String, Object> gm = transformGreeting(greeting);
				greetingsList.add(gm);
			}
		}
		
		return data;
	}
	
	private Object createKeywordData(Long timestamp) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("ts", timestamp);
		
		List<Map<String, Object>> keywordsList = new ArrayList<Map<String, Object>>();
		data.put("keywords", keywordsList);
		
		List<KeywordEffectRela> keywords = aiService.getAllKeywords();
		if(keywords != null) {
			for(KeywordEffectRela keyword: keywords) {
				Map<String, Object> km = transformKeyword(keyword);
				keywordsList.add(km);
			}
		}
		
		return data;
	}
	
	private Map<String, Object> transformGreeting(QuickGreeting greetings) {
		if (greetings==null) return null;
		
		Map<String, Object> gm = new HashMap<String, Object>();
		gm.put("id", greetings.getId());
		gm.put("content", greetings.getContent());
		gm.put("sortRank", greetings.getSortRank());
		
		return gm;
	}
	
	private Map<String, Object> transformKeyword(KeywordEffectRela keyword) {
		if (keyword==null) return null;
		
		Map<String, Object> km = new HashMap<String, Object>();
		km.put("id", keyword.getId());
		km.put("keyword", keyword.getKeyword());
		km.put("kwType", keyword.getKeywordType());
		km.put("actType", keyword.getActType());
		km.put("effectNo", keyword.getEffecNo());
		
		Long linkId = keyword.getLinkId();
		if (linkId!=null){
			Link link = getLinkFromCache(linkId, linkService);
			if (link!=null){
				Map<String, Object> linkMap = ApiHelper.transformLinkToApiMap(link);
				km.put("action", linkMap);
			}
		}
		
		return km;
	}
	
	private KeywordEffectRela getAIKeyword(final Long keywordId) {
		Object o = super.getOrGenCacheableData("AI_KEYWORD_"+keywordId, null, Integer.MAX_VALUE, true, null, new CacheableDataGenerator(){
			@Override
			public CacheableData generate() {
				return new CacheableData(aiService.getKeywordEffectRelaById(keywordId));
			}
		});
		
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) return null;
		if (o instanceof KeywordEffectRela) return (KeywordEffectRela)o; 
		return JSON.parseObject(so, KeywordEffectRela.class);
	}
	
	private int getUserDrawCount(final Long keywordId, final String userId) {
		Object o = super.getOrGenCacheableData("AI_USER_DRAW_COUNT_"+userId+"_"+keywordId, null, Integer.MAX_VALUE, true, null, new CacheableDataGenerator(){
			@Override
			public CacheableData generate() {
				return new CacheableData(aiService.getUserDrawCount(keywordId, userId));
			}
		});
		
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) return 0;
		return Integer.parseInt(so);
	}
	
	private void saveUserCoupon(final AiUserCoupon coupon){
		if(RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, coupon);
			RabbitMQHelper.publish(AiUserCoupon.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					aiService.saveUserCoupon(coupon);
				}
			});
		} else {
			aiService.saveUserCoupon(coupon);
		}
	}
}
