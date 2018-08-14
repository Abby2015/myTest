package com.yum.kfc.brand.camp1505.api.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.danga.MemCached.MemCachedClient;
import com.google.gson.Gson;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.web.util.WebUtil;
import com.yum.kfc.brand.camp1505.api.Camp1505Api;
import com.yum.kfc.brand.camp1505.pojo.Camp1505Draw;
import com.yum.kfc.brand.camp1505.pojo.Camp1505Open;
import com.yum.kfc.brand.camp1505.pojo.Camp1505Share;
import com.yum.kfc.brand.camp1505.service.Camp1505Service;
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.KBSResultWin;
import com.yum.kfc.brand.common.pojo.Parameter;
import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.BrowserUtil;
import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.common.utils.HttpClientUtil;
import com.yum.kfc.brand.common.utils.JacksonUtil;
import com.yum.kfc.brand.common.utils.RestClientUtil;
import com.yum.kfc.brand.common.utils.SaltUtil;

/**
 * @author luolix
 *
 */
@Component
@Path("/camp1505/motherday")
@Scope("singleton")
public class Camp1505ApiImpl extends BaseApiImpl implements Camp1505Api {
	
	private static Logger logger = LoggerFactory.getLogger(Camp1505ApiImpl.class);
	
	public static final String ASK_ENC_KEY = "$@_+&*Ah";
	public static final String BROWSER_TOKEN_KEY = "$^!%^/^&$^";
	public static final int SALT_LENGTH = 6;
	private final String CACHE_KEY_PREFIX = "KFC.CAMP.FD1505.";
	private final String CACHE_KEY_USERCOUNT = CACHE_KEY_PREFIX+"DRAWUSERCOUNT-";
	private final String CACHE_KEY_USERWON = CACHE_KEY_PREFIX+"WINUSER-";
	
	@Value("${campaign.start}")			public String CAMPAIGN_START;
	@Value("${campaign.end}")			public String CAMPAIGN_END;
	@Value("${campaign.win.rate}")		public int WIN_RATE;
	@Value("${campaign.rate.movie}")	public int MOVE_RATE;
	@Value("${campaign.rate.travel}")	public int TRAVEL_RATE;
	@Value("${campaign.rate.ipad}")		public int IPAD_RATE;
	@Value("${award.movie.value}")		public int AWARD_MOVIE;
	@Value("${award.travel.value}")		public int AWARD_TRAVEL;
	@Value("${award.iPad.value}")		public int AWARD_IPAD;
	@Value("${campaign.limit.user}")	public int MAX_USERCOUNT;
	@Value("${campaign.svc.kbs.win}")	public String KBS_WIN_URL;
	@Value("${campaign.svc.kbs.token}")	public String KBS_TOKEN_URL;
	
	
	@Value("${campaign.url.head}")  	private String urlHead;
	@Value("${wechat.appId}")			private String appId;
	@Value("${wechat.appSecret}")		private String appSecret;
	@Value("${wechat.redirect.url}")	private String wechatRedirectUrl;
	
	@Value("${wechat.client_code}")	private String client_code;
	@Value("${wechat.client_secret}")	private String client_secret;
	@Value("${wechat.base_refer_host}")	private String base_refer_host;
	@Value("${wechat.userinfo_refer_host}")	private String userinfo_refer_host;
	@Value("${wechat.open_id_host}")	private String open_id_host;
	@Value("${wechat.interface}")	private String interface_;
	@Value("${wechat.action}")	private String action_;
	@Value("${wechat.action2}")	private String action_2;
	
	
	public static Random random = new Random();
	public static final int RANDOM_MAX = 1000000;
	
	@Autowired
	private MemCachedClient memcachedClient;

	@Autowired
	private Camp1505Service service;

	@Override
	public Result open(Camp1505Parameter parameter, HttpServletRequest request) {
		parameter.setIpAddr(WebUtil.getRealRemoteAddr(request));
		//检查渠道类型是否正确
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		//检查设备类型是否正确
		if(null == parameter.getDeviceType()){
			return new Result(ApiErrorCode.DEVICE_TYPE_ISNULL, "Device Type is empty");
		}else if(!DEVICE_TYPE_LIST.contains(parameter.getDeviceType())){
			return new Result(ApiErrorCode.DEVICE_TYPE_INCORRECT, "Device Type is incorrect");
		}
		//检查是否存在设备号
		if(null == parameter.getDeviceId()){
			return new Result(ApiErrorCode.DEVICE_UUID_ISNULL, "DeviceId is empty");
		}
		final Camp1505Open open = new Camp1505Open();
		BeanUtils.copyProperties(parameter, open);
		open.setId(newUUID());
		open.setOpenTime(new Date());
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, open);
			RabbitMQHelper.publish(Camp1505Open.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveOpen(open);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			service.saveOpen(open);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sid", open.getId());
		result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "", data);
		logger.info("====A01 open ====\n"+JSON.toJSONString(result));
		return result;
	}
	
	
	@Override
	public Result share(Camp1505Parameter parameter) {
		//检查分享内容
//		if(null == parameter.getTasteOption() || StringUtils.isBlank(parameter.getOtherContent())){
		if(null == parameter.getTasteOption()){
			return new Result(ApiErrorCode.SHARE_CONTENT_ISNULL, "Share Content is empty");
		}
		//检查渠道类型是否正确
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		//检查设备类型是否正确
		if(null == parameter.getDeviceType()){
			return new Result(ApiErrorCode.DEVICE_TYPE_ISNULL, "Device Type is empty");
		}else if(!DEVICE_TYPE_LIST.contains(parameter.getDeviceType())){
			return new Result(ApiErrorCode.DEVICE_TYPE_INCORRECT, "Device Type is incorrect");
		}
		//检查分享媒介
		if(StringUtils.isBlank(parameter.getMediaType())){
			return new Result(ApiErrorCode.MEDIA_TYPE_ISNULL, "Media Type is empty");
		}else if(!MEDIA_TYPE_LIST.contains(parameter.getMediaType())){
			return new Result(ApiErrorCode.MEDIA_TYPE_INCORRECT, "Media Type is incorrect");
		}
		//检查分享结果
		if(null == parameter.getShareResult()){
			return new Result(ApiErrorCode.SHARE_RESULT_ISNULL, "result is empty");
		}else if(!SHARE_RESULT_LIST.contains(parameter.getShareResult())){
			return new Result(ApiErrorCode.SHARE_RESULT_INCORRECT, "result is incorrect");
		}
		final Camp1505Share share = new Camp1505Share();
		BeanUtils.copyProperties(parameter, share);
		share.setId(newUUID());
		share.setOpenId(parameter.getSid());
		share.setShareTime(new Date(System.currentTimeMillis()));
		
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, share);
			RabbitMQHelper.publish(Camp1505Share.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveShare(share);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			service.saveShare(share);
		}
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS);
		logger.info("====A04 share ====\n"+new Gson().toJson(result));
		return result;
	}
	
	@Override
	public Result draw(Camp1505Parameter parameter, HttpServletRequest request){
		parameter.setIpAddr(WebUtil.getRealRemoteAddr(request));
		//检查渠道类型是否正确
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		//检查设备类型是否正确
		if(null == parameter.getDeviceType()){
			return new Result(ApiErrorCode.DEVICE_TYPE_ISNULL, "Device Type is empty");
		}else if(!DEVICE_TYPE_LIST.contains(parameter.getDeviceType())){
			return new Result(ApiErrorCode.DEVICE_TYPE_INCORRECT, "Device Type is incorrect");
		}
		//检查抽奖有效期
		Date now = new Date(), midNight = DateUtil.getMidNight();
		if (now.before(DateUtil.parseDate(CAMPAIGN_START)) || now.after(DateUtil.parseDate(CAMPAIGN_END))) {
			return new Result(ApiErrorCode.CAMPAIGN_NOT_START, "campaign not start or finished");
		}
		if(exceedUserDrawTimes(parameter, midNight)) {
			return new Result(ApiErrorCode.TOOMANY_USER_DRAWTIMES, "too many exceed User Draw Times");
		}
		if(wonAlready(parameter)) {
			return new Result(ApiErrorCode.USER_HAVE_WON, "User have won the draw");
		}
		int rate = WIN_RATE;
		int luck = random.nextInt(RANDOM_MAX);
		boolean gotWinChance = 0<=luck && luck<=RANDOM_MAX/rate;
		boolean won = false;
		String awardCodeId = null;
		if (gotWinChance){
			Integer awardType = null;
			int awardRate = random.nextInt(100);
			if(awardRate <= MOVE_RATE){
				awardType = AWARD_MOVIE;
			}else if(awardRate <= (MOVE_RATE+TRAVEL_RATE)){
				awardType = AWARD_TRAVEL;
			}else{
				awardType = AWARD_IPAD;
			}
			parameter.setAwardType(awardType);
			KBSResultWin result = requestAwardFromKBS(parameter);
			if (result.isSuccess() && result.getData()!=null && result.getData().isWin() && StringUtils.isNotBlank(result.getData().getCode())){
				won = true;
				awardCodeId = result.getData().getCode();
				memcachedClient.set(CACHE_KEY_USERWON+parameter.getChannelType()+"-"+parameter.getUserId(), "1");
				logger.info("got award from KBS, result is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
			} else {
				logger.info("failed request award from KBS, return is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
			}
		}
		final Camp1505Draw draw = new Camp1505Draw();
		BeanUtils.copyProperties(parameter, draw);
		draw.setId(newUUID());
		draw.setDrawTime(new Date(System.currentTimeMillis()));
		draw.setWinAward(won ? 1 : 0);
		if(won){
			draw.setAwardType(parameter.getAwardType());
			draw.setAwardCodeId(awardCodeId);
		}
		draw.setOpenId(parameter.getSid());
		//如果中奖了，直接保存到DB，DB失败写到日志，方便人工处理
		if(won){	
			boolean isSuccess = service.saveDraw(draw);
			if(!isSuccess){
				logger.error("win draw save to DB failure, draw data[{}]", JSON.toJSONString(draw));
				new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "general server error, please try it later");
			}
		}else{	//如果没有中奖，走MQ
			if (RabbitMQHelper.RABBIT_MQ_ENABLED){
				Message msg = new Message(Message.Type.CREATE, draw);
				RabbitMQHelper.publish(Camp1505Draw.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
					@Override
					public void handleFailPublish(Throwable reason, Message msg) {
						logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
						try{
							service.saveDraw(draw);
						}catch(DuplicateKeyException e){
							logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
						}
					}
					
				});
			} else {
				service.saveDraw(draw);
			}
		}
		addOrIncr(CACHE_KEY_USERCOUNT+parameter.getChannelType()+"-"+parameter.getUserId(), 1, midNight);
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("win", won);
		data.put("awardType", won ? draw.getAwardType() : "");
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("====A03 draw ====\n"+ JSON.toJSONString(result));
		return result;
	}
	
	
	
	
	@Override
	public Result winInfo(Camp1505Parameter parameter){
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		//检查设备类型是否正确
		if(null == parameter.getDeviceType()){
			return new Result(ApiErrorCode.DEVICE_TYPE_ISNULL, "Device Type is empty");
		}else if(!DEVICE_TYPE_LIST.contains(parameter.getDeviceType())){
			return new Result(ApiErrorCode.DEVICE_TYPE_INCORRECT, "Device Type is incorrect");
		}
		if(StringUtils.isBlank(parameter.getName())){
			return new Result(ApiErrorCode.DRAW_NAME_ISNULL, "name is empty");
		}
		if(StringUtils.isBlank(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_ISNULL, "Telephone number is empty");
		}else if(!isCorrectPhone(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_INCORRECT, "Telephone number is incorrect");
		}
		if(StringUtils.isBlank(parameter.getAddress())){
			return new Result(ApiErrorCode.DRAW_ADDRESS_ISNULL, "address is empty");
		}
		Camp1505Draw draw = new Camp1505Draw();
		BeanUtils.copyProperties(parameter, draw);
		boolean isSuccess = service.saveWinAward(draw);
		Result result = null;
		if(!isSuccess){
			logger.error("win person info save to DB failure, win persion data[{}]", JSON.toJSONString(draw));
			result = new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "general server error, please try it later");
		}else{
			result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS);
		}
		logger.info("====A04 winInfo ====\n"+JSON.toJSONString(result));
		return result;
	}
	

	@Override
	public Result getWechatCode(String state) {
		if(StringUtils.isBlank(state)){
			return new Result(ApiErrorCode.GENERAL_CLIENT_ERROR, "state is empty");
		}
		String code=DigestUtils.md5Hex(state+"d6ac48cfcb9a6f611f203e6c7237034c");
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("code", code);
		return new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
	}
	
	
	private KBSResultWin requestAwardFromKBS(Camp1505Parameter param) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("channelType", param.getChannelType());
		p.put("userId", param.getUserId());
		p.put("token", param.getToken());
		p.put("awardType", param.getAwardType());
		String sr = RestClientUtil.callPostService(KBS_WIN_URL, p, String.class);
		logger.info("request award from kbs, param[{}], result[{}]", param, sr);
		KBSResultWin wr = new Gson().fromJson(sr, KBSResultWin.class);
		return wr;
	}
	

	private boolean exceedUserDrawTimes(final Parameter param, Date midNight) {
		Object oUserCount = getCache(CACHE_KEY_USERCOUNT+param.getChannelType()+"-"+param.getUserId(), midNight, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				return service.queryDrawTimesByUserToday(param.getUserId(), param.getChannelType());
			}
		});
		
		if (oUserCount!=null){
			try{
				long ucount = Long.parseLong(String.valueOf(oUserCount));
				if (ucount>MAX_USERCOUNT) return true;
			}catch(Exception e){
			}
		}
		
		return false;
	}
	
	private boolean wonAlready(final Parameter param){
		Object owf = getCache(CACHE_KEY_USERWON+param.getChannelType()+"-"+param.getUserId(), null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				boolean isWon = service.queryIsUserWon(param.getUserId(), param.getChannelType());
				return isWon ? 1 : 0;
			}
		});
		
		return "1".equalsIgnoreCase(String.valueOf(owf));
	}
		
	
	private void addOrIncr(String key, long inc, Date expiry){
		if(!memcachedClient.add(key, inc, expiry)){
			memcachedClient.incr(key);
		}
	}
	
	private Object getCache(String key, Date expiry, CacheOp copSave, DataGenerator dg){
		Object o = memcachedClient.get(key);
		if (o==null && dg!=null){
			o = dg.generate();
			if (o!=null){
				switch (copSave){
				case ADD:
					memcachedClient.add(key, String.valueOf(o), expiry);
					break;
				case SET:
					memcachedClient.set(key,  String.valueOf(o), expiry);
					break;
				default:
					throw new RuntimeException(copSave+" not support in this method");
				}
			}
		}
		return o;
	}
	
	private static enum CacheOp{
		GET, GETS, ADD, SET, INCR, ADDORINCR
	}
	
	private static interface DataGenerator{
		Object generate();
	}

	
	@Override
	public void login(HttpServletRequest request, HttpServletResponse response) {
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		
		String utm_source = request.getParameter("utm_source");
		if(StringUtils.isEmpty(utm_source)){
			utm_source = "";
		}
		
		logger.info("===login===\n" + "channelType==" +channelType + "  ||  " + "deviceType==" + deviceType + "  ||  " + "utm_source=" + utm_source);
		
		if(Integer.parseInt(channelType) == CHANNEL_TYPE_WECHAT){
			logger.info("===login===  weixin");
			try {
				String redirect_code = DigestUtils.md5Hex(wechatRedirectUrl+appSecret);
				response.sendRedirect("/"+urlHead+"/pages/camp1505/wx_redirect.html?redirect_code="+redirect_code+"&utm_source="+utm_source);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		else{
			//对于非微信渠道
			String openid = Parameter.newUUID();
			String token = SaltUtil.encryption(SaltUtil.newSalt(6)+ DigestUtils.md5Hex(openid), Camp1505ApiImpl.BROWSER_TOKEN_KEY);
			logger.info("===login===  browser");
			try {
				response.sendRedirect("/"+urlHead+"/pages/camp1505/index.html?&channelType="+channelType+"&deviceType="+deviceType+"&token="+token +"&openid="+openid+"&utm_source="+utm_source);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public void wxRedirect(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
//		String openId = request.getParameter("openid");
		String key = request.getParameter("key");
		String openId = getOpenId(key);
		System.out.println("===openId===\n" + openId);
		
//		Map map2 = request.getParameterMap();
//		System.out.println("===getParameterMap===\n" + JacksonUtil.marshallToString(map2));
//		Map<String, Object> tokenInfoMap = getAccessToken(request);
//		String access_token = (String) tokenInfoMap.get("access_token");
		
		String access_token = SaltUtil.encryption(SaltUtil.newSalt(6)+ DigestUtils.md5Hex(openId), this.BROWSER_TOKEN_KEY);
		
		logger.info("===wxRedirect===\n" + "channelType==" + channelType + 
				"  ||  " + "deviceType==" + deviceType +
				"  ||  " + "openId==" + openId +
				"  ||  " + "access_token==" + access_token);
		response.sendRedirect("/"+urlHead+"/pages/camp1505/transfer.html?openid="+openId+"&token="+access_token+"&channelType="+channelType+"&deviceType="+deviceType);
	}

	/**
	 * 即时获取 ThirdWechatState 方法
	 * @return
	 * 
	 */
	@Override
	public Result getThirdWechatState(Camp1505Parameter parameter) {
		String timestamp = System.currentTimeMillis()+"";
		timestamp = timestamp.substring(0, timestamp.length()-3);
		//检查设备类型是否正确
		if(StringUtils.isEmpty(parameter.getRedirectUrl())){
			return new Result(ApiErrorCode.REDIRECT_URL_ISNULL, "redirect url isnull");
		}
		
		String dataStr = action_ + client_code + client_secret + interface_ + parameter.getRedirectUrl() + timestamp;
		String authcode = DigestUtils.md5Hex(dataStr);
		String httpUrl = "http://adapter.verystar.cn/externalapi.php?interface="+interface_+"&action="+action_
							+"&time="+timestamp+"&client_code="+client_code+"&authcode="+authcode;
		String paramData = "redirect_uri="+parameter.getRedirectUrl();
		
//		System.setProperty("http.proxyHost", "web-proxy.sgp.hp.com");  
//		System.setProperty("http.proxyPort", "8080");
		Map resultMap = null;
		try {
			String result = HttpClientUtil.sendPostRequestByJava(httpUrl, paramData);
			resultMap = JacksonUtil.jsonToObject(result, HashMap.class);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "GENERAL_SERVER_ERROR");
		}
		logger.info("resultMap="+resultMap.toString());
		return new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, resultMap);
	}
	
	
	/**
	 * 即时获取 openid 方法
	 * @return
	 * 
	 */
	public String getOpenId(String key) {
		String timestamp = System.currentTimeMillis()+"";
		timestamp = timestamp.substring(0, timestamp.length()-3);
		
		String dataStr = action_2 + client_code + client_secret + interface_ + key + timestamp;
		String authcode = DigestUtils.md5Hex(dataStr);
		String httpUrl = "http://adapter.verystar.cn/externalapi.php?interface="+interface_+"&action="+action_2
				+"&time="+timestamp+"&client_code="+client_code+"&authcode="+authcode;
		String paramData = "key="+key;
		
		String openid = "";
		try {
			String result = HttpClientUtil.sendPostRequestByJava(httpUrl, paramData);
			JSONObject resultJSON = JSONObject.fromObject(result);
			logger.info("resultJSON=\n"+resultJSON.toString());
			openid = (String) JSONObject.fromObject(resultJSON.get("data")).get("openid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("openid="+openid);
		return openid;
	}
	
}
