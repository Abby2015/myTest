package com.yum.kfc.brand.camp1504.api.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
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
import com.yum.kfc.brand.camp1504.api.FooldayApi;
import com.yum.kfc.brand.camp1504.pojo.FooldayAnswer;
import com.yum.kfc.brand.camp1504.pojo.FooldayDraw;
import com.yum.kfc.brand.camp1504.pojo.FooldayOpen;
import com.yum.kfc.brand.camp1504.pojo.FooldayQuestion;
import com.yum.kfc.brand.camp1504.pojo.FooldayShare;
import com.yum.kfc.brand.camp1504.service.FooldayService;
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.KBSResultLogPhone;
import com.yum.kfc.brand.common.pojo.KBSResultWin;
import com.yum.kfc.brand.common.pojo.Parameter;
import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.BrowserUtil;
import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.common.utils.HttpClientUtil;
import com.yum.kfc.brand.common.utils.HttpsPost;
import com.yum.kfc.brand.common.utils.JacksonUtil;
import com.yum.kfc.brand.common.utils.RestClientUtil;
import com.yum.kfc.brand.common.utils.SHA1;
import com.yum.kfc.brand.common.utils.SaltUtil;

/**
 * @author luolix
 *
 */
@Component
@Path("/camp1504/foolday")
@Scope("singleton")
public class FooldayApiImpl extends BaseApiImpl implements FooldayApi {
	
	private static Logger logger = LoggerFactory.getLogger(FooldayApiImpl.class);
	
	public static final String ASK_ENC_KEY = "@_+&*Ah$";
	public static final String BROWSER_TOKEN_KEY = "!%^/^&$^";
	public static final int SALT_LENGTH = 6;
	private final String CACHE_KEY_PREFIX = "KFC.CAMP.FD1504.";
	private final String CACHE_KEY_USERCOUNT = CACHE_KEY_PREFIX+"DRAWUSERCOUNT-";
	private final String CACHE_KEY_USERWON = CACHE_KEY_PREFIX+"WINUSER-";
	private final String CACHE_KEY_PHONEUSED = CACHE_KEY_PREFIX+"CREDITPHONE-";
	private final String CACHE_KEY_TAG = CACHE_KEY_PREFIX+"TAG-";
	
	@Value("${campaign.start}")			public String CAMPAIGN_START;
	@Value("${campaign.end}")			public String CAMPAIGN_END;
	@Value("${campaign.rate.brand}")	public int BRAND_RATE;
	@Value("${campaign.rate.wechat}")	public int WECHAT_RATE;
	@Value("${campaign.rate.browser}")	public int BROWSER_RATE;
	@Value("${campaign.limit.user}")	public int MAX_USERCOUNT;
	@Value("${campaign.svc.kbs.win}")	public String KBS_WIN_URL;
	@Value("${campaign.svc.kbs.phone}")	public String KBS_PHONE_URL;
	@Value("${campaign.svc.kbs.token}")	public String KBS_TOKEN_URL;
	
	
	@Value("${campaign.url.head}")  	private String urlHead;
	@Value("${wechat.grantType}")  		private String grantType;
	@Value("${wechat.openId.url}")		private String openUrl;
	@Value("${wechat.appId}")			private String appId;
	@Value("${wechat.appSecret}")		private String appSecret;
	@Value("${wechat.querytoken.url}")	private String wechatQuerytokenUrl;
	@Value("${wechat.redirect.url}")	private String wechatRedirectUrl;
	@Value("${wechat.share_redirect.url}")	private String wechatShareRedirectUrl;
	
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
	private FooldayService fooldayService;

	@Override
	public Result open(Camp1504Parameter parameter, HttpServletRequest request) {
		parameter.setIpAddr(WebUtil.getRealRemoteAddr(request));
		//从缓存中取出Tag，如果Tag存在，说明用户点击的分享进入的，获得发起者的信息
		Camp1504TagInfo tagInfo = this.getTagInfo(parameter.getTag());
		if(null != tagInfo){
			parameter.setInviterId(tagInfo.getUserId());
			parameter.setInviteChannelType(tagInfo.getChannelType());
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
		//检查是否存在设备号
		if(null == parameter.getDeviceId()){
			return new Result(ApiErrorCode.DEVICE_UUID_ISNULL, "DeviceId is empty");
		}
		final FooldayOpen fooldayOpen = new FooldayOpen();
		BeanUtils.copyProperties(parameter, fooldayOpen);
		fooldayOpen.setId(newUUID());
		fooldayOpen.setOpenTime(new Date());
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, fooldayOpen);
			RabbitMQHelper.publish(FooldayOpen.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						fooldayService.saveOpen(fooldayOpen);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			fooldayService.saveOpen(fooldayOpen);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sid", fooldayOpen.getId());
		result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "", data);
		logger.info("====A01 open ====\n"+JacksonUtil.marshallToString(result));
		return result;
	}
	
	
	@Override
	public Result getQuestion(Camp1504Parameter parameter) {
		String shareQuestionId = null;
		Camp1504TagInfo tagInfo = this.getTagInfo(parameter.getTag());
		if(null != tagInfo){
			shareQuestionId = tagInfo.getQuestionId();
		}
		FooldayQuestion question = fooldayService.getQuestion(parameter.getUserId(), parameter.getSid(), shareQuestionId, parameter.getRecycleCount());
		Result result = null;
		if(null == question){
			result = new Result(ApiErrorCode.NOT_DATA_FOUND_ERROR, "not question data found from server"); 
		}else{
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("questionId", question.getId());
			data.put("code", question.getCode());
			data.put("correct", question.getCorrect());
			data.put("yesPercent", question.getYesPercent());
			data.put("noPercent", question.getNoPercent());
			data.put("naPercent", question.getNaPercent());
			data.put("naPercent", question.getNaPercent());
			data.put("recycleCount", question.getRecycleCount());
			result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data); 
		}
		logger.info("====A02  getQuestion ====\n"+JacksonUtil.marshallToString(result));
		return result;
	}
	
	@Override
	public Result answer(Camp1504Parameter parameter) {
		//检查渠道类型是否正确
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		//检查用户选择
		if(null == parameter.getChoice()){
			return new Result(ApiErrorCode.CHOICE_ISNULL, "choice is empty");
		}else if(!CHOICE_LIST.contains(parameter.getChoice())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "choice is incorrect");
		}
		//检查前台判断的用户是否答对此题
		if(null == parameter.getIsCorrect()){
			return new Result(ApiErrorCode.CHOICE_RIGHT_ISNULL, "isCorrect is empty");
		}else if(!CHOICE_LIST.contains(parameter.getChoice())){
			return new Result(ApiErrorCode.CHOICE_RIGHT_INCORRECT, "isCorrect is incorrect");
		}
		final FooldayAnswer fooldayAnswer = new FooldayAnswer();
		BeanUtils.copyProperties(parameter, fooldayAnswer);
		fooldayAnswer.setId(newUUID());
		fooldayAnswer.setOpenId(parameter.getSid());
		fooldayAnswer.setAnswerTime(new Date(System.currentTimeMillis()));
		//设置Tag
		Camp1504TagInfo tagInfo = new Camp1504TagInfo();
		BeanUtils.copyProperties(parameter, tagInfo);
		String tagInfoStr = new Gson().toJson(tagInfo);
		fooldayAnswer.setTagContent(tagInfoStr);
		Result result = null;
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, fooldayAnswer);
			RabbitMQHelper.publish(FooldayAnswer.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						fooldayService.saveAnswer(fooldayAnswer);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			fooldayService.saveAnswer(fooldayAnswer);
		}
		Map<String,Object> data = new HashMap<String,Object>();
		String tag = SaltUtil.newSalt(SALT_LENGTH)+fooldayAnswer.getId();
		data.put("tag", SaltUtil.encryption(tag, ASK_ENC_KEY));
		//放入缓存
		memcachedClient.set(CACHE_KEY_TAG + fooldayAnswer.getId(), tagInfoStr, 0);
		result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("====A03  answer ====\n"+JacksonUtil.marshallToString(result));
		return result;
	}
	
	@Override
	public Result share(Camp1504Parameter parameter) {
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
		final FooldayShare fooldayShare = new FooldayShare();
		BeanUtils.copyProperties(parameter, fooldayShare);
		fooldayShare.setId(newUUID());
		fooldayShare.setOpenId(parameter.getSid());
		fooldayShare.setShareTime(new Date(System.currentTimeMillis()));
		
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, fooldayShare);
			RabbitMQHelper.publish(FooldayShare.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						fooldayService.saveShare(fooldayShare);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			fooldayService.saveShare(fooldayShare);
		}
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS);
		logger.info("====A04 share ====\n"+JacksonUtil.marshallToString(result));
		return result;
	}
	
	@Override
	public Result draw(Camp1504Parameter parameter, HttpServletRequest request){
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
		
		int rate = 0;
		if (parameter.getChannelType() == 0) rate = BRAND_RATE;
		if (parameter.getChannelType() == 1) rate = WECHAT_RATE;
		if (parameter.getChannelType() == 2) rate = BROWSER_RATE;
		int luck = random.nextInt(RANDOM_MAX);
		boolean gotWinChance = 0<=luck && luck<=RANDOM_MAX/rate;
		boolean won = false;
		if (gotWinChance){
			KBSResultWin result = requestAwardFromKBS(parameter);
			if (result.isSuccess() && result.getData()!=null && result.getData().isWin()){
				won = true;
				memcachedClient.set(CACHE_KEY_USERWON+parameter.getChannelType()+"-"+parameter.getUserId(), "1");
				logger.info("got award from KBS, result is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
			} else {
				logger.info("failed request award from KBS, return is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
			}
		}
		final FooldayDraw fooldayDraw = new FooldayDraw();
		BeanUtils.copyProperties(parameter, fooldayDraw);
		fooldayDraw.setId(newUUID());
		fooldayDraw.setDrawTime(new Date(System.currentTimeMillis()));
		fooldayDraw.setWinAward(won ? 1 : 0);
		fooldayDraw.setOpenId(parameter.getSid());
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, fooldayDraw);
			RabbitMQHelper.publish(FooldayDraw.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						fooldayService.saveWinInfo(fooldayDraw);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			fooldayService.saveWinInfo(fooldayDraw);
		}
		addOrIncr(CACHE_KEY_USERCOUNT+parameter.getChannelType()+"-"+parameter.getUserId(), 1, midNight);
		
		Map<String,Object> data = new HashMap<String,Object>();
		int gift = won ? 1 : 0;
		data.put("gift", gift);
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("====A05 draw ====\n"+JacksonUtil.marshallToString(result));
		return result;
	}
	
	
	@Override
	public Result isWinNotPhone(String userId, Integer channelType){
		logger.info("====A05-1 isWinNotPhone input parameter: userId={}, channelType={}", userId, channelType);
		Map<String, Object> data = new HashMap<String, Object>();
		if(null == channelType){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(channelType)){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		FooldayDraw fooldayDraw = fooldayService.isWinNotPhone(userId, channelType);
		if(null == fooldayDraw){
			data.put("winAward", false);
			data.put("winPhone", null);
		}else{
			data.put("winAward", true);
			data.put("winPhone", fooldayDraw.getPhone());
		}
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, "", data);
		logger.info("====A05-1 isWinNotPhone output====\n"+JacksonUtil.marshallToString(result));
		return result;
	}
	
	
	@Override
	public Result updateWinPhone(Camp1504Parameter parameter){
		logger.info("====A05-2 updateWinPhone input parameter: channelType={}, userId={}, phone={}", 
				parameter.getChannelType(), parameter.getUserId(), parameter.getPhone());
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		if(StringUtils.isBlank(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_ISNULL, "Telephone number is empty");
		}else if(!isCorrectPhone(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_INCORRECT, "Telephone number is incorrect");
		}
		FooldayDraw fooldayDraw = new FooldayDraw();
		BeanUtils.copyProperties(parameter, fooldayDraw);
		boolean isSuccess = fooldayService.saveWinPhone(fooldayDraw);
		Result result = null;
		if(isSuccess){
			result =  new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS);
		}else{
			result =  new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "general server error");
		}
		logger.info("====A05-2 updateWinPhone output====\n"+JacksonUtil.marshallToString(result));
		return result;
	}
	
	
	@Override
	public Result winPhone(Camp1504Parameter parameter){
		Date now = new Date();
		if (now.before(DateUtil.parseDate(CAMPAIGN_START)) || now.after(DateUtil.parseDate(CAMPAIGN_END))) {
			return new Result(ApiErrorCode.CAMPAIGN_NOT_START, "campaign not start or finished");
		}
		if(null == parameter.getChannelType()){
			return new Result(ApiErrorCode.CHINNEL_TYPE_ISNULL, "Channel Type is empty");
		}else if(!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())){
			return new Result(ApiErrorCode.CHINNEL_TYPE_INCORRECT, "Channel Type is incorrect");
		}
		if(StringUtils.isBlank(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_ISNULL, "Telephone number is empty");
		}else if(!isCorrectPhone(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_INCORRECT, "Telephone number is incorrect");
		}
		if(phoneUsed(parameter.getPhone())){
			return new Result(ApiErrorCode.DRAW_PHONE_WINAWARD, "The telephone number has been winning");
		}
		final FooldayDraw fooldayDraw = new FooldayDraw();
		BeanUtils.copyProperties(parameter, fooldayDraw);
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.UPDATE, fooldayDraw);
			RabbitMQHelper.publish(FooldayDraw.class.getCanonicalName()+"Phone", msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						fooldayService.saveWinPhone(fooldayDraw);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
				
			});
		} else {
			fooldayService.saveWinPhone(fooldayDraw);
		}
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS);
		memcachedClient.set(CACHE_KEY_PHONEUSED+parameter.getPhone(), "1");
		if ("0".equals(parameter.getChannelType())){
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("token", parameter.getToken());
			p.put("phone", parameter.getPhone());
			KBSResultLogPhone pr = RestClientUtil.callPostService(KBS_PHONE_URL, p, KBSResultLogPhone.class);
			logger.info("save phone to KBS result is [{}], param is [{}]", new Gson().toJson(pr), new Gson().toJson(parameter));
		}
		logger.info("====A06 winPhone ====\n"+JacksonUtil.marshallToString(result));
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
	
	
	private boolean phoneUsed(final String phone) {
		Object opu = getCache(CACHE_KEY_PHONEUSED+phone, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				boolean used = fooldayService.isPhoneUsed(phone);
				return used ? 1 : 0;
			}
		});
		return "1".equals(String.valueOf(opu));
	}
	
	
	private KBSResultWin requestAwardFromKBS(Parameter param) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("channelType", param.getChannelType());
		p.put("userId", param.getUserId());
		p.put("token", param.getToken());
		if (param.getChannelType() == 1) p.put("awardType", 15);
		if (param.getChannelType() == 0) p.put("awardType", 16);
		String sr = RestClientUtil.callPostService(KBS_WIN_URL, p, String.class);
		logger.info("request award from kbs, param[{}], result[{}]", param, sr);
		KBSResultWin wr = new Gson().fromJson(sr, KBSResultWin.class);
		return wr;
	}
	

	private boolean exceedUserDrawTimes(final Parameter param, Date midNight) {
		Object oUserCount = getCache(CACHE_KEY_USERCOUNT+param.getChannelType()+"-"+param.getUserId(), midNight, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				return fooldayService.queryDrawTimesByUserToday(param.getUserId(), param.getChannelType());
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
				boolean isWon = fooldayService.queryIsUserWon(param.getUserId(), param.getChannelType());
				return isWon ? 1 : 0;
			}
		});
		
		return "1".equalsIgnoreCase(String.valueOf(owf));
	}
	
	
	private Camp1504TagInfo getTagInfo(String tag){
		if (StringUtils.isBlank(tag))
			return null;
		tag = SaltUtil.decryption(tag, ASK_ENC_KEY);
		tag = tag.substring(SALT_LENGTH);
		//从缓存中取得tag
		return this.getCacheTagInfo(tag);
	}
	
	private Camp1504TagInfo getCacheTagInfo(final String answerId) {
		Camp1504TagInfo tagInfo = null;
		Object oTagInfo = getCache(CACHE_KEY_TAG + answerId, null, CacheOp.SET, new DataGenerator(){
			@Override
			public Object generate(){
				String tagStr = fooldayService.getTagContent(answerId);
				return tagStr;
			}
		});
		if (oTagInfo==null){
			logger.error("failed to get TagInfo for answerId[{}], {}", answerId);
			return null;
		}else{
			tagInfo = (oTagInfo instanceof Camp1504TagInfo)? (Camp1504TagInfo)oTagInfo : new Gson().fromJson(oTagInfo.toString(), Camp1504TagInfo.class);
		}
		return tagInfo;
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

	
	/*private Object checkIDToken(final Parameter parameter) {
		Object r = getCache(CACHE_KEY_USERVER+parameter.getChannelType()+"-"+parameter.getUserId(), null, CacheOp.SET, new DataGenerator(){
			@Override
			public Object generate() {
				int channelType = Integer.valueOf(parameter.getChannelType());
				try {
					SnsHelper.verifySnsToken(channelType, parameter.getToken(), parameter.getUserId(), KBS_TOKEN_URL);
					return 1;
				} catch (Exception e) {
					logger.error("failed to verify id token for param[{}], {}", new Gson().toJson(parameter), e.getMessage(), e);
					return null;
				}
			}
		});
		return r;
	}*/
	
	@Override
	public void login(HttpServletRequest request, HttpServletResponse response) {
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		
		logger.info("===login===\n" + "channelType==" +channelType + "  ||  " + "deviceType==" + deviceType );
		
		if(Integer.parseInt(channelType) == CHANNEL_TYPE_WECHAT){
			logger.info("===login===  weixin");
			try {
				String redirect_code = DigestUtils.md5Hex(wechatRedirectUrl+appSecret);
				response.sendRedirect("/"+urlHead+"/pages/camp1504/wx_redirect.jsp?redirect_code="+redirect_code);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}else{
			//对于非微信渠道
			String openid = Parameter.newUUID();
			String token = SaltUtil.encryption(SaltUtil.newSalt(6)+ DigestUtils.md5Hex(openid), FooldayApiImpl.BROWSER_TOKEN_KEY);
			logger.info("===login===  browser");
			try {
				response.sendRedirect("/"+urlHead+"/pages/camp1504/index.jsp?&channelType="+channelType+"&deviceType="+deviceType+"&token="+token +"&openid="+openid);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public void wxRedirect(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		
		String key = request.getParameter("key");
		String openId = getOpenId(key);
		System.out.println("===openId===\n" + openId);
		String access_token = SaltUtil.encryption(SaltUtil.newSalt(6)+ DigestUtils.md5Hex(openId), this.BROWSER_TOKEN_KEY);
		
//		String openId = request.getParameter("openid");
//		Map<String, Object> tokenInfoMap = getAccessToken(request);
//		String access_token = (String) tokenInfoMap.get("access_token");
		
		logger.info("===wxRedirect===\n" + "channelType==" + channelType + 
				"  ||  " + "deviceType==" + deviceType +
				"  ||  " + "openId==" + openId +
				"  ||  " + "access_token==" + access_token);
		response.sendRedirect("/"+urlHead+"/pages/camp1504/index.jsp?openid="+openId+"&token="+access_token+"&channelType="+channelType+"&deviceType="+deviceType);
	}
	
	@Override
	public void share(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		String share_redirect_code = DigestUtils.md5Hex(wechatShareRedirectUrl+appSecret);
		String shareInfo = request.getParameter("shareInfo");
		
		logger.info("share.do==>shareInfo="+shareInfo + " || channelType=" + channelType);
		if(StringUtils.isNotEmpty(shareInfo)){
			String[] shareInfoArr = shareInfo.split("__");
			String questionId = "";
			String fromOpenId = "";
			String tag = "";
			
			questionId = shareInfoArr[0];
			fromOpenId = shareInfoArr[1];
			tag = shareInfoArr[2];
			
			logger.info("===share.do===\n" + "qId==" + questionId + 
					"  ||  " + "foid==" + fromOpenId +
					"  ||  " + "tag==" + tag +
					"  ||  " + "src==" + share_redirect_code +
					"  ||  " + "channelType==" + channelType);
			
			response.sendRedirect("/"+urlHead+"/pages/camp1504/wx_share.jsp?qId="+questionId+"&foid="+fromOpenId+"&tag="+tag 
					+"&src="+share_redirect_code + "&channelType="+channelType +"&deviceType="+ deviceType);
		}else{
			//首页分享跳转
			response.sendRedirect("/login.do");
		}
	}
	
	@Override
	public void share_redirect(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String channelType = BrowserUtil.queryChannelType(request);
		String deviceType = BrowserUtil.queryDeviceType(request);
		String openId = "";
		String access_token = "";
		if(Integer.parseInt(channelType)!=CHANNEL_TYPE_WECHAT){
			//对于非微信渠道
			openId = Parameter.newUUID();
			access_token = SaltUtil.encryption(SaltUtil.newSalt(6)+ DigestUtils.md5Hex(openId), this.BROWSER_TOKEN_KEY);
			response.sendRedirect("/"+urlHead+"/pages/camp1504/wx_share_redirect.jsp?coid="+openId+"&token="+access_token+"&channelType="+channelType+"&deviceType="+deviceType);
		}else{
			//对于微信渠道
			String key = request.getParameter("key");
			openId = getOpenId(key);
			System.out.println("===openId===\n" + openId);
			access_token = SaltUtil.encryption(SaltUtil.newSalt(6)+ DigestUtils.md5Hex(openId), this.BROWSER_TOKEN_KEY);
			
//			openId = request.getParameter("openid");
//			Map<String, Object> tokenInfoMap = getAccessToken(request);
//			access_token = (String) tokenInfoMap.get("access_token");
			
			logger.info("===share_redirect.do===\n" + "coid==" + openId + 
					"  ||  " + "token==" + access_token +
					"  ||  " + "deviceType==" + deviceType +
					"  ||  " + "channelType==" + channelType);
			
			response.sendRedirect("/"+urlHead+"/pages/camp1504/wx_share_redirect.jsp?coid="+openId+"&token="+access_token+"&channelType="+channelType+"&deviceType="+deviceType);
		}
	}
	
	
	@Override
	public Result getTicket(HttpServletRequest request) throws Exception {
			Result sr = new Result();
			ServletContext ServletContext_ = request.getServletContext();
			String timestamp = request.getParameter("timestamp");
			String nonceStr = request.getParameter("nonceStr");
			String url = request.getParameter("url");
			
			if(StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonceStr) || StringUtils.isEmpty(url)){
				sr.setErrCode(ApiErrorCode.GENERAL_CLIENT_ERROR);
				sr.setErrMsg("param  timestamp || nonceStr || url  is null");
			}
			
			logger.info("https  timestamp="+ timestamp + "\n" + "nonceStr=" + nonceStr  +"\n" +"url=" +url);
			
			Map<String,Object> access_token_ticket_map = (HashMap<String,Object>) ServletContext_.getAttribute("access_token_ticket");
			String access_token = "";
			String ticket = "";
			String timestamp_ = "";
			if(access_token_ticket_map!=null 
					&& StringUtils.isNotEmpty((String) access_token_ticket_map.get("access_token")) 
					&& StringUtils.isNotEmpty((String) access_token_ticket_map.get("ticket")) 
					&& StringUtils.isNotEmpty((String) access_token_ticket_map.get("timestamp"))){
				ticket = (String) access_token_ticket_map.get("ticket");
				access_token = (String) access_token_ticket_map.get("access_token");
				timestamp_ = (String) access_token_ticket_map.get("timestamp");
				long timestamp__ = Long.parseLong(timestamp_);
				long current_timestamp = System.currentTimeMillis();
				if((current_timestamp-timestamp__)>=7200000){
					//access_token ticket 有效期为2小时  毫秒数为  2*60*60*1000=7200000
					Map<String, Object> retMap = getAccessTokenNow();
					access_token = (String) retMap.get("access_token");
					retMap = getTicket(access_token);
					ticket = (String) retMap.get("ticket");
					access_token_ticket_map.put("access_token", access_token);
					access_token_ticket_map.put("ticket", ticket);
					access_token_ticket_map.put("timestamp", System.currentTimeMillis()+"");
					ServletContext_.setAttribute("access_token_ticket", access_token_ticket_map);
				}
			}else{
				access_token_ticket_map = new HashMap();
				Map<String, Object> retMap = getAccessTokenNow();
				access_token = (String) retMap.get("access_token");
				retMap = getTicket(access_token);
				ticket = (String) retMap.get("ticket");
				access_token_ticket_map.put("access_token", access_token);
				access_token_ticket_map.put("ticket", ticket);
				access_token_ticket_map.put("timestamp", System.currentTimeMillis()+"");
				ServletContext_.setAttribute("access_token_ticket", access_token_ticket_map);
			}
			String data="jsapi_ticket="+ticket+"&noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;
			String signature = new SHA1().getDigestOfString(data.getBytes());
			Map<String,Object>  retMap_ = new HashMap<String,Object>();
			retMap_.put("signature", signature);
			sr.setData(retMap_);
			logger.info("========== signature =========" + signature);
			sr.setData(retMap_);
			return sr;
	}
	
	/**
	 * 通用获取 wx_jssdk_ticket 方法
	 * @param access_token
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getTicket(String access_token) throws Exception {
		String httpsUrl;
		String json;
		Map<String, Object> retMap;
		httpsUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access_token+"&type=jsapi";
		json = HttpsPost.post(httpsUrl);
		logger.info("https  ticketjson="+json.toString());
		retMap = JacksonUtil.jsonToObject(json, HashMap.class);
		return retMap;
	}

	/**
	 * 通用获取 wx_access_token 方法
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getAccessToken(HttpServletRequest request) throws Exception {
		ServletContext ServletContext_ = request.getServletContext();
		Map<String,Object> access_token_map = (HashMap<String,Object>) ServletContext_.getAttribute("access_token_map");
		String httpsUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appSecret;
		String json = null;
		String timestamp = "";
		if(access_token_map != null 
				&& StringUtils.isNotEmpty((String)access_token_map.get("access_token"))
				&& StringUtils.isNotEmpty((String)access_token_map.get("timestamp"))){
			timestamp = (String)access_token_map.get("timestamp");
			long timestamp_ = Long.parseLong(timestamp);
			long current_timestamp = System.currentTimeMillis();
			if((current_timestamp-timestamp_)>=7200000){
				//access_token 有效期为2小时  毫秒数为  2*60*60*1000=7200000
				json = HttpsPost.post(httpsUrl);
				logger.info("https  tokenjson="+json.toString());
				Map<String,Object>  retMap = JacksonUtil.jsonToObject(json, HashMap.class);
				access_token_map.put("access_token", (String)retMap.get("access_token"));
				access_token_map.put("timestamp", System.currentTimeMillis()+"");
				ServletContext_.setAttribute("access_token_map", access_token_map);
			}
		}else{
			access_token_map = new HashMap();
			json = HttpsPost.post(httpsUrl);
			logger.info("https  tokenjson="+json.toString());
			
			Map<String,Object>  retMap = JacksonUtil.jsonToObject(json, HashMap.class);
			access_token_map.put("access_token", (String)retMap.get("access_token"));
			access_token_map.put("timestamp", System.currentTimeMillis()+"");
			ServletContext_.setAttribute("access_token_map", access_token_map);
		}
		System.out.println("====access_token_map====\n"+JacksonUtil.marshallToString(access_token_map));
		return access_token_map;
	}
	
	/**
	 * 即时获取 wx_access_token 方法
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getAccessTokenNow() throws Exception {
		String httpsUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appSecret;
		String json = null;
		json = HttpsPost.post(httpsUrl);
		logger.info("https  tokenjson="+json.toString());
		Map<String,Object>  retMap = JacksonUtil.jsonToObject(json, HashMap.class);
		return retMap;
	}
	
	/**
	 * 即时获取 ThirdWechatState 方法
	 * @return
	 * 
	 */
	@Override
	public Result getThirdWechatState(Camp1504Parameter parameter) {
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
		
		Map resultMap = null;
//		System.setProperty("http.proxyHost", "web-proxy.sgp.hp.com");  
//		System.setProperty("http.proxyPort", "8080");
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
		
//		System.setProperty("http.proxyHost", "web-proxy.sgp.hp.com");  
//		System.setProperty("http.proxyPort", "8080");
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
