package com.yum.kfc.brand.dzp1807.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.cache.CacheUtil;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.SpringUtil;
import com.hp.jdf.ssm.util.StringUtil;
import com.yum.kfc.brand.ErrCode;
import com.yum.kfc.brand.common.api.BaseCampApiServlet;
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.KBSResultWin;
import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.common.utils.IDHelper;
import com.yum.kfc.brand.common.utils.MybatisTblShardStrategy;
import com.yum.kfc.brand.common.utils.RestClientUtil;
import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Barrage;
import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Draw;
import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Order;
import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Parameter;
import com.yum.kfc.brand.dzp1807.service.Dzp1807Service;

/**
 * 
 * @author yidequan@cloudwalk.cn 2018年6月27日下午2:21:32
 *
 */
@WebServlet(urlPatterns = "/api/dzp1807/*", asyncSupported = true)
public class Dzp1807ApiServlet extends BaseCampApiServlet {
	private static final long serialVersionUID = 4399076541089841884L;
	private Dzp1807Service service = SpringUtil.getBean(Dzp1807Service.class);
	private static final String CAMP_DRAW_ID = "dzp1807";
	private final String CACHE_KEY_USERDRAW_COUNT = "DZP1807.DRAWUSERCOUNT_";
	private final String CACHE_KEY_USERDRAW_TIME = "DZP1807.USERDRAWTIME_";
	private final String CACHE_KEY_USERDRAW_RECORD = "DZP1807.USERDRAWRECORD_";
	private final String CACHE_KEY_USERAWARD_WINCOUNT = "DZP1807.AWARDWINCOUNT_";
	
	private static final String KBS_GET_DRAWINFO_URL = ApplicationConfig.getProperty("campaign.svc.kbs.draw");
	private static final String TYPECODE = ApplicationConfig.getProperty("campaign.dzp1807.vgold.typecode");
	private static final String CAMPAIGNCODE = ApplicationConfig.getProperty("campaign.dzp1807.vgold.campaigncode");
	private static final String KBS_DEDUCT_ORDER__URL = ApplicationConfig.getProperty("campaign.svc.kbs.order");
	private static final String KBS_WIN_URL = ApplicationConfig.getProperty("campaign.svc.kbs.dzp1807.win");
	private static final int USER_RECORD_DAY = Integer.parseInt(ApplicationConfig.getProperty("campaign.dzp1807.user.record.day", "30"));
	private static final Map<String, String> PLAYING_COUNT;	
	
	static{
		try {
			PLAYING_COUNT = JSON.parseObject(StringUtil.stripEmbeddedComment(ApplicationConfig.getProperty("campaign.dzp1807.play.user.count")), new TypeReference<Map<String, String>>(){}.getType());
		} catch (Exception e) {
			throw new IllegalArgumentException("incorrect configuration items, "+e.getMessage(), e);
		}
	}
	
	// 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	public static final int CHANNEL_TYPE_APP = 0;
	public static final int CHANNEL_TYPE_WECHAT = 1;
	public static final int CHANNEL_TYPE_BROWSER = 2;

	// 设备类型(0: android; 1:ios; 2:browser)
	public static final int DEVICE_TYPE_ANDRIOD = 0;
	public static final int DEVICE_TYPE_IOS = 1;
	public static final int DEVICE_TYPE_BROWSER = 2;

	public static List<Integer> CHANNEL_TYPE_LIST = new ArrayList<Integer>();
	public static List<Integer> DEVICE_TYPE_LIST = new ArrayList<Integer>();

	static {
		// 用户渠道(0: Brand App; 1: 微信; 2:浏览器)
		CHANNEL_TYPE_LIST.add(CHANNEL_TYPE_APP);
		CHANNEL_TYPE_LIST.add(CHANNEL_TYPE_WECHAT);
		CHANNEL_TYPE_LIST.add(CHANNEL_TYPE_BROWSER);
		// 设备类型(0: android; 1:ios; 2:browser)
		DEVICE_TYPE_LIST.add(DEVICE_TYPE_ANDRIOD);
		DEVICE_TYPE_LIST.add(DEVICE_TYPE_IOS);
		DEVICE_TYPE_LIST.add(DEVICE_TYPE_BROWSER);
	}

	private static enum DrawIntervalUnit {
		ALLTIME, DAY, WEEK;

		public static DrawIntervalUnit parse(String strUnit) {
			if (strUnit == null || (strUnit = strUnit.trim()).isEmpty()) {
				return null;
			}
			if ("ALLTIME".equalsIgnoreCase(strUnit))
				return ALLTIME;
			else if ("DAY".equalsIgnoreCase(strUnit))
				return DAY;
			else if ("WEEK".equalsIgnoreCase(strUnit))
				return WEEK;
			else
				throw new IllegalArgumentException("Unsupported DrawIntervalUnit [" + strUnit + "]");
		}
	}
	
	@POST @Path("draw")
	private void draw(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*"); 
		
		JSONObject jo = (JSONObject) super.getBodyAsJson(req);
		final String token = jo.getString("token");
		final int deviceType = jo.getIntValue("deviceType");
		final int channelType = jo.getIntValue("channelType");
		
		Dzp1807Parameter parameter = new Dzp1807Parameter();
		parameter.setIpAddr(super.getClientIP(req));
		parameter.setToken(token);
		parameter.setDeviceType(deviceType);
		parameter.setChannelType(channelType);
		
		if (!CHANNEL_TYPE_LIST.contains(parameter.getChannelType())) {
			throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("Channel Type is incorrect");
		}
		
		if (!DEVICE_TYPE_LIST.contains(parameter.getDeviceType())) {
			throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("Device Type is incorrect");
		}
		
		UserInfo userInfo = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(userInfo == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}
		
		parameter.setPhone(userInfo.getPhone());
		parameter.setUserId(userInfo.getUserId());
		parameter.setSsoUserId(userInfo.getSsoUserId());
		parameter.setCrmUserCode(userInfo.getCrmUserCode());
		
		//防止刷单
//		if(!memcachedClient.add(CACHE_KEY_USERDRAW+parameter.getSsoUserId(), 1, new Date(5*1000))){
//			logger.error("draw frequently, ssoUserId:{}, userId:{}, phone:{}", parameter.getSsoUserId(), parameter.getUserId(), parameter.getPhone());
//			return new Result(ApiErrorCode.CALL_TOO_FREQUENTLY, "您的操作过于频繁，请稍后再试");
//		}
		
		long nowTime = new Date().getTime();
		Object ot = CacheUtil.get(CacheUtil.getDefaultCacheName(), CACHE_KEY_USERDRAW_TIME+parameter.getSsoUserId());
		if(ot!=null){
			long lastTime = Long.parseLong(ot.toString());
			if(nowTime - lastTime < 3*1000){
				throw new ApiException("Bad request", ApiErrorCode.CALL_TOO_FREQUENTLY).setContext("request too frequenty, lastTime:["+lastTime+"], nowTime:["+nowTime+"]");
			}
			CacheUtil.set(CacheUtil.getDefaultCacheName(), CACHE_KEY_USERDRAW_TIME+parameter.getSsoUserId(), nowTime);
		}
		
		//获取奖品信息
		Map<String, Object> drawInfo = getDrawInfo();
		if(drawInfo == null){
			throw new ApiException("Missing draw info", ErrCode.GENERAL_CLIENT_ERROR);
		} 
		
		//校验是否有资格参加
		if(!checkUserCanAttend(drawInfo, parameter.getSsoUserId())){
			throw new ApiException("Bad request", ApiErrorCode.DRAW_REACH_LIMIT);
		}
		
//	
//		Dzp1807Log dl = new Dzp1807Log();
//		dl.setReqFlag(""+request.hashCode());
//		dl.setCallTime(new Date());
//		dl.setParam(JSON.toJSONString(parameter));
//		dl.setHeader(HttpMethod.GET.equals(request.getMethod())?null:("Content-Type: "+ContentType.APPLICATION_JSON.toString()));		
//		dl.setToken(parameter.getToken());
//		dl.setKfcBrandIP(NetUtil.getLocalIP());
//		dl.setClientIP(WebUtil.getRealRemoteAddr(request));
//		dl.setUrl(request.getPathInfo());
//		dl.setKfcBrandIP(localip);
		
		//生成订单号
		String orderId = IDHelper.newOrderID('2', String.valueOf(MybatisTblShardStrategy.byLastDigitOrLenMod10(parameter.getSsoUserId())));
		Dzp1807Order order = new Dzp1807Order();
		BeanUtils.copyProperties(parameter, order);
		order.setOrderId(orderId);
		order.setCreateTime(new Date());
		
		int awardWinLimit = 0;
		boolean won = false;
		Map<String, Object> kGlodOrder = null;
		try {
			//CRM下单扣K金&获取抽奖机会
			kGlodOrder = deductKglod(token, orderId);
			//下单接口抛出异常
			if(kGlodOrder.get("success") == null){ 
				int errCode = Integer.valueOf(kGlodOrder.get("errCode").toString());
				String errMsg = String.valueOf(kGlodOrder.get("errMsg"));
//				String errData = String.valueOf(kGlodOrder.get("errData"));
				
//				dl.setResponse("place order failed, orderId:["+orderId+"], errCode:["+errCode+"], errMsg:["+errMsg+"], errData:["+errData+"]");
//				510514 表示积分不足
//				Ben<all_blue9527@qq.com> 2017/6/21 11:41:32
//				所以这个接口接到514你就当余额不足处理
//				蓝色天空(460953643) 2017/6/21 11:42:22
//				那placeOrder接口只要返回514，我是不是就认为是积分不足？
//				Ben<all_blue9527@qq.com> 2017/6/21 11:47:00
//				是的
//				return new Result(errCode, errMsg, errData); 
				throw new ApiException("Bad Request", errCode).setContext("orderId:["+orderId+"], errMsg:["+errMsg+"]");
			}
			
			order.setStatus(Boolean.valueOf(kGlodOrder.get("success").toString()));
			order.setReverse(Boolean.valueOf(kGlodOrder.get("isReverse").toString()));
			order.setFree(Boolean.valueOf(kGlodOrder.get("fromFreeChance").toString()));
			//下单失败
			if(!order.getStatus()){
//				dl.setResponse("place order failed, orderId:["+orderId+"]");
//				return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "faild to create order");
				throw new ApiException("Bad Request", ErrCode.GENERAL_SERVER_ERROR).setContext("place order failed, orderId:["+orderId+"]");
			}
			
			if(Boolean.valueOf(kGlodOrder.get("winChance").toString())){
				won = true;
				parameter.setAwardType((Integer) kGlodOrder.get("awardType"));
				parameter.setAwardLevel((Integer) kGlodOrder.get("awardLevel"));
				parameter.setActivityId((String) kGlodOrder.get("activityId"));
				parameter.setAwardName((String) kGlodOrder.get("awardName"));
				parameter.setWinImg((String) kGlodOrder.get("winImg"));
				parameter.setPersonalImg((String) kGlodOrder.get("personalImg"));
				parameter.setCouponType((Integer)kGlodOrder.get("couponType"));
				parameter.setUrl((String) kGlodOrder.get("awardLink"));
				
				awardWinLimit = Integer.valueOf(kGlodOrder.get("awardWinLimit").toString());
			}
			
		}catch (ApiException e) {
			throw new ApiException("Bad Request", e.getErrorCode()).setContext(e.getContext());
		}catch (Exception e) {
//			dl.setResponse("error occurred when creating order, orderId:["+orderId+"]");
//			return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "error occurred when creating order");
			throw new ApiException("Bad Request", ErrCode.GENERAL_SERVER_ERROR).setContext("error occurred when creating order, orderId:["+orderId+"]");
		}finally{
			//记录订单			
			saveOrder(order);
			
//			if (!order.getStatus()) {
//				dl.setResponseTime(new Date());
//				String json = JSON.toJSONString(dl);
//				logDzp1807ReqElkJson.info(json);
//			}
		}
		
		Dzp1807Draw draw = new Dzp1807Draw();
		if(order.getStatus()) {
			BeanUtils.copyProperties(parameter, draw);
			draw.setOrderId(orderId);
			draw.setDrawTime(new Date());
			
			try {
				if(won){
					Long awardWinCount = getUserAwardWinCount(parameter.getSsoUserId(), parameter.getAwardType());
					if(awardWinLimit > 0 && awardWinCount >= awardWinLimit){
						//超过奖品中奖次数限制则不发奖品
						won = false;
					}else {
//						if(parameter.getAwardLevel() == 2 || parameter.getAwardLevel() == 3){
//							parameter.setFrCrm(true);
//						}
//						if(parameter.getAwardLevel() != 1){
							parameter.setFrCrm(true);
//						}
						//发放奖品
						KBSResultWin result = requestAwardFromKBS(parameter, orderId);
						if (result.isSuccess() && result.getData()!=null && result.getData().isWin() && StringUtils.isNotBlank(result.getData().getCode())){
							won = true;
							parameter.setAwardCode(result.getData().getCode());
//							logger.info("got award from KBS, result is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
						} else {
							won = false;
//							logger.error("failed request award from KBS, return is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
//							if(!"0".equals(result.getErrCode())) return new Result(Integer.valueOf(result.getErrCode()), result.getErrMsg(), result.getErrData());
							throw new ApiException("Bad Request", result.getErrCode()).setContext("failed to send coupon, errMsg:["+result.getErrMsg()+"]");
						}
					}
				}
			} catch (Exception e) {
				won = false;
//				logger.error("Oops! Something is wrong when requesting coupon from CRM. param[{}], exception[{}]", parameter, e);
//				return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "error occurred when request award");
				throw new ApiException("error occurred when request award", ErrCode.GENERAL_SERVER_ERROR);
			}finally {
				draw.setWinAward(won ? 1 : 0);
				draw.setFree(order.isFree());
				if(won){
					draw.setAwardCode(parameter.getAwardCode());
//					draw.setCouponType(parameter.getCouponType());
//					draw.setUrl(parameter.getUrl());
//					addOrIncrCache(CACHE_KEY_USERAWARD_WINCOUNT+draw.getSsoUserId()+"-"+draw.getAwardType(), "1", null);
					if (!CacheUtil.add(CacheUtil.getDefaultCacheName(), CACHE_KEY_USERAWARD_WINCOUNT+draw.getSsoUserId()+"-"+draw.getAwardType(), 1, 24*60*60)) {
						CacheUtil.incr(CacheUtil.getDefaultCacheName(), CACHE_KEY_USERAWARD_WINCOUNT+draw.getSsoUserId()+"-"+draw.getAwardType(), 1);
					}
				}else {
					draw.setAwardLevel(null);
					draw.setAwardType(null);
					draw.setAwardName(null);
					draw.setWinImg(null);
					draw.setPersonalImg(null);
					draw.setCouponType(null);
					draw.setUrl(null);
				}
				
				saveDraw(draw);
				
				String dateFlag = DateUtil.formatDate(getBeginTime(drawInfo), "yyyyMMdd");
//				addOrIncrCache(CACHE_KEY_USERDRAWCOUNT+dateFlag+"-"+draw.getSsoUserId(), "1", null);
//				expireCache(CACHE_KEY_USERDRAWRECORD+draw.getSsoUserId());
				
				final String ssoUserId = draw.getSsoUserId();
				if (!CacheUtil.add(CacheUtil.getDefaultCacheName(), CACHE_KEY_USERDRAW_COUNT+dateFlag+"-"+ssoUserId, 1, 24*60*60)) {
					CacheUtil.incr(CacheUtil.getDefaultCacheName(), CACHE_KEY_USERDRAW_COUNT+dateFlag+"-"+ssoUserId, 1);
				}
				
				//延迟清除抽奖记录缓存
				Thread t = new Thread(){
					@Override
					public void run() {
						try {
							Thread.sleep(3000);
							CacheUtil.expire(CacheUtil.getDefaultCacheName(), CACHE_KEY_USERDRAW_RECORD+ssoUserId);
						} catch (Exception e) {
							//ignore
						}
					}
				};
				t.setName("expire user draw record");
				t.start();
				
				//将用户昵称加入缓存
				Dzp1807Barrage barrage = new Dzp1807Barrage();
				barrage.setNickname(userInfo.getNickname());
				barrage.setWinAward(won);
				barrage.setAwardName(draw.getAwardName());
				barrage.setAwardLevel(draw.getAwardLevel());
				addAndGetUserNicknamesToCache(true, barrage);
				
				Map<String,Object> data = new HashMap<String,Object>();
				data.put("orderId", orderId);
				data.put("awardCode", won ? draw.getAwardCode() : "");
				data.put("win", won);
				data.put("awardLevel", won ? draw.getAwardLevel() : "");
				data.put("awardName", won ? draw.getAwardName() : "");
				data.put("winImg", won ? draw.getWinImg() : "");
				data.put("personalImg", won ? draw.getPersonalImg() : "");
				data.put("isFree", order.isFree());
				data.put("url", won ? draw.getUrl() : "");
				data.put("couponType", won ? draw.getCouponType() : "");
				
				req.setAttribute(RESULT, data);
//				dl.setResponse(JSON.toJSONString(data));
//				dl.setResponseTime(new Date());
//				String json = JSON.toJSONString(dl);
//				logDzp1807ReqElkJson.info(json);
			}
		}
	}

	private String addAndGetUserNicknamesToCache(boolean isAdd, Dzp1807Barrage barrage) {
		Object o = CacheUtil.get(CacheUtil.getDefaultCacheName(), "DZP1807.DRAW_USER_BARRAGE");
		if (isAdd) {
			List<Dzp1807Barrage> barrageList = null;
			String nickname = barrage.getNickname();
			nickname = (nickname==null||nickname.trim().isEmpty())?"肯德基网友":nickname.trim();
			barrage.setNickname(nickname);
			if (o != null) {
				barrageList = JSON.parseObject(String.valueOf(o), new TypeReference<List<Dzp1807Barrage>>(){}.getType());
				if (barrageList.size() >= 50) {
					barrageList.remove(barrageList.get(0));
				}
				
				barrageList.add(barrage);
			}else {
				barrageList = new ArrayList<Dzp1807Barrage>();
				barrageList.add(barrage);
			}
			
			String s = JSON.toJSONString(barrageList);
			CacheUtil.set(CacheUtil.getDefaultCacheName(), "DZP1807.DRAW_USER_BARRAGE", s);
			
			return s;
		}
		
		return  String.valueOf(o);
	}

	private Map<String, Object> getDrawInfo() {
		Object o = CacheUtil.get(CacheUtil.getDefaultCacheName(), "DZP1807.DRAWINFO");
		if (o != null) {
			return JSON.parseObject(String.valueOf(o), new TypeReference<Map<String, Object>>(){}.getType());
		}else {
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("campId", CAMP_DRAW_ID);
			String str = RestClientUtil.callGetService(KBS_GET_DRAWINFO_URL, p, String.class);
			JSONObject object = JSONObject.parseObject(str).getJSONObject("data");

			Map<String, Object> resultMap = new HashMap<String, Object>();
			if (object == null) {
				return null;
			} else {
				resultMap.put("startTime", object.getString("startTime"));
				resultMap.put("endTime", object.getString("endTime"));
				resultMap.put("attendLimit", object.getString("attendLimit"));
				resultMap.put("attendUnit", object.getString("attendUnit"));
				
				CacheUtil.set(CacheUtil.getDefaultCacheName(), "DZP1807.DRAWINFO", object, 24*60*60);
			}

			return resultMap;
		}
	}	
	
	private boolean checkUserCanAttend(Map<String, Object> drawInfo, String ssoUserId) {
		int limit = Integer.parseInt(String.valueOf(drawInfo.get("attendLimit")));
		
		boolean canAttend = false;
		if(limit <= 0){
			canAttend = true;
		}else{
			Date beginTime = getBeginTime(drawInfo);
			
			Long userDrawCount = getUserDrawCount(ssoUserId, beginTime);			
			if(userDrawCount < limit){
				canAttend = true;
			}
		}
		
		return canAttend;
	}
	
	private Date getBeginTime(Map<String, Object> drawInfo){
		DrawIntervalUnit unit = DrawIntervalUnit.parse(String.valueOf(drawInfo.get("attendUnit")));
		Date beginTime = null;
		switch (unit) {
		case ALLTIME:
			break;
		case DAY:
			beginTime = getTodayBeginTime();
			break;
		case WEEK:
			beginTime = getWeekBeginTime();
			break;
		default:
			break;
		}
		
		return beginTime;
	}
	
	private Long getUserDrawCount(final String ssoUserId, final Date beginTime){
		String dateFlag = DateUtil.formatDate(beginTime, "yyyyMMdd");
		
		Object o = CacheUtil.get(CacheUtil.getDefaultCacheName(), CACHE_KEY_USERDRAW_COUNT+dateFlag+"_"+ssoUserId);
		
		String so = String.valueOf(o);
		
		if (o==null||"null".equals(so)||so.isEmpty()) return 0L;
		return Long.valueOf(so);
	}
	
	private Date getTodayBeginTime(){
		Calendar currentDate = Calendar.getInstance();   
		  
		currentDate.set(Calendar.HOUR_OF_DAY, 0);  
		currentDate.set(Calendar.MINUTE, 0);  
		currentDate.set(Calendar.SECOND, 0);  
		
		return currentDate.getTime();
	}
	
	private Date getWeekBeginTime(){
		Calendar currentDate = Calendar.getInstance();   
		
		currentDate.setFirstDayOfWeek(Calendar.MONDAY);  	          
		currentDate.set(Calendar.HOUR_OF_DAY, 0);  
		currentDate.set(Calendar.MINUTE, 0);  
		currentDate.set(Calendar.SECOND, 0);  
		currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
		
		return currentDate.getTime();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> deductKglod(String token, String transid) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("token", token);
		p.put("campId", CAMP_DRAW_ID);
//		p.put("rateAlgorithm", RATE_ALGORITHM);
		p.put("transid", transid);
		p.put("activityId", TYPECODE);
		p.put("campaignCode", CAMPAIGNCODE);
		String str = RestClientUtil.callPostService(KBS_DEDUCT_ORDER__URL, p, String.class);
//		logger.info("request deduct k-glod from kbs, param[{}], result[{}]", p, str);
		JSONObject jo = JSONObject.parseObject(str);
		JSONObject data = jo.getJSONObject("data");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(data == null){
			resultMap = JSON.parseObject(jo.toJSONString(), Map.class);
		}else{
			resultMap = JSON.parseObject(data.toJSONString(), Map.class);
		}
			
		return resultMap;
	}
	
	private void saveOrder(final Dzp1807Order order) {
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, order);
			RabbitMQHelper.publish(Dzp1807Order.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					service.saveOrder(order);
				}
			});
		} else {
			service.saveOrder(order);
		}
	}
	
	private Long getUserAwardWinCount(final String ssoUserId, final int awardType){
		Object o =super.getOrGenCacheableData(CACHE_KEY_USERAWARD_WINCOUNT+ssoUserId+"-"+awardType, null, Integer.MAX_VALUE, new CacheableDataGenerator(){
			@Override
			public CacheableData generate() {
				return new CacheableData(service.getUserAwardWinCount(ssoUserId, awardType));
			}
		});

		String so = String.valueOf(o);
		
		if (o==null||"null".equals(so)||so.isEmpty()) return 0L;
		return Long.valueOf(so);
	}
	
	private KBSResultWin requestAwardFromKBS(Dzp1807Parameter param, String transid) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("channelType", param.getChannelType());
		p.put("token", param.getToken());
		p.put("awardType", param.getAwardType());
		p.put("activityId", param.getActivityId());
		p.put("frCrm", param.isFrCrm());
		p.put("couponType", param.getCouponType());
		p.put("transid", transid);
		p.put("campaignCode", CAMPAIGNCODE);
		String sr = RestClientUtil.callPostService(KBS_WIN_URL, p, String.class);
//		logger.info("request award from kbs, param[{}], result[{}]", param, sr);
		KBSResultWin wr = new Gson().fromJson(sr, KBSResultWin.class);
		return wr;
	}
	
	private void saveDraw(final Dzp1807Draw draw) {
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, draw);
			RabbitMQHelper.publish(Dzp1807Draw.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					service.saveDraw(draw);
				}
			});
		} else {
			service.saveDraw(draw);
		}
	}
	
	@GET @Path("record") 
	private void getDrawRecord(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		
		final String token = getStrParam(req, "token", null);
		
		if(token == null || token.isEmpty()){
			throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("token is empty");
		}
		
		//根据token获取用户信息
		UserInfo userInfo = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(userInfo == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}
		
		String ssoUserId =userInfo.getSsoUserId();
		Date beginTime = new Date(System.currentTimeMillis() - USER_RECORD_DAY*24*60*60*1000L);
		
		List<Dzp1807Draw> draws = getUserDrawRecord(ssoUserId, beginTime);
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		if(draws!=null && draws.size()>0){
			 for(Dzp1807Draw draw: draws){
				 Map<String,Object> drawMap = new HashMap<String,Object>();
				 drawMap.put("orderId", draw.getOrderId());
				 drawMap.put("awardName", draw.getAwardName());
				 drawMap.put("awardLevel", draw.getAwardLevel());
				 drawMap.put("drawTime", draw.getDrawTime());
				 drawMap.put("winImg", draw.getWinImg());
				 drawMap.put("personalImg", draw.getPersonalImg());
				 drawMap.put("isFree", draw.isFree());
				 drawMap.put("couponType", draw.getCouponType());
				 
				 data.add(drawMap);
			 }
		 }
		
		req.setAttribute(RESULT, data);
	}
	
	@SuppressWarnings("unchecked")
	private List<Dzp1807Draw> getUserDrawRecord(final String ssoUserId, final Date beginTime){
		Object o =super.getOrGenCacheableData(CACHE_KEY_USERDRAW_RECORD+ssoUserId, null, 24*60*60, new CacheableDataGenerator(){
			@Override
			public CacheableData generate() {
				return new CacheableData(service.getUserDrawRecord(ssoUserId, beginTime));
			}
		});
		
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) return null;
		if (o instanceof List) return (List<Dzp1807Draw>)o;
		return JSON.parseObject(so, new TypeReference<List<Dzp1807Draw>>(){}.getType());
	}
	
	@GET @Path("count") 
	private void getDrawCount(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		
		final String token = getStrParam(req, "token", null);

		if(token == null || token.isEmpty()){
			throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("token is empty");
		}
		
		//根据token获取用户信息
		UserInfo userInfo = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(userInfo == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}

		// 获取奖品信息
		Map<String, Object> drawInfo = getDrawInfo();
		if (drawInfo == null) {
			throw new ApiException("Missing draw info", ErrCode.GENERAL_SERVER_ERROR);
		}

		int limit = Integer.parseInt(String.valueOf(drawInfo.get("attendLimit")));

		Date beginTime = getBeginTime(drawInfo);
		Long userDrawCount = getUserDrawCount(userInfo.getSsoUserId(), beginTime);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("limitCount", limit);
		data.put("userDrawCount", userDrawCount);

		req.setAttribute(RESULT, data);
	}
	
	@GET @Path("barrage") 
	private void getBarrage(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		
		String barrageStr = addAndGetUserNicknamesToCache(false, null);
		List<Dzp1807Barrage> barrageList = null;
		if (!"null".equals(barrageStr)){
			barrageList = JSON.parseObject(barrageStr, new TypeReference<List<Dzp1807Barrage>>(){}.getType());
		}
		
		Calendar cal = Calendar.getInstance();
		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("users", barrageList);
		data.put("playCount", PLAYING_COUNT.get(String.valueOf(currentHour)));
		
		req.setAttribute(RESULT, data);
	}
	
	
}
