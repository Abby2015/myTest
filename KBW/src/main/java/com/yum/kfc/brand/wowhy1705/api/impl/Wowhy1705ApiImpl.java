package com.yum.kfc.brand.wowhy1705.api.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.danga.MemCached.MemCachedClient;
import com.google.gson.Gson;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.NetUtil;
import com.hp.jdf.ssm.util.BaseHttpClient.HttpMethod;
import com.hp.jdf.ssm.web.util.WebUtil;
import com.yum.kfc.brand.common.constant.ApiErrorCode;
import com.yum.kfc.brand.common.pojo.KBSResultWin;
import com.yum.kfc.brand.common.pojo.Result;
import com.yum.kfc.brand.common.service.BaseApiImpl;
import com.yum.kfc.brand.common.utils.DateUtil;
import com.yum.kfc.brand.common.utils.IDHelper;
import com.yum.kfc.brand.common.utils.MybatisTblShardStrategy;
import com.yum.kfc.brand.common.utils.RestClientUtil;
import com.yum.kfc.brand.wowhy1705.api.Wowhy1705Api;
import com.yum.kfc.brand.wowhy1705.pojo.Wowhy1705Draw;
import com.yum.kfc.brand.wowhy1705.pojo.Wowhy1705Log;
import com.yum.kfc.brand.wowhy1705.pojo.Wowhy1705Order;
import com.yum.kfc.brand.wowhy1705.service.Wowhy1705Service;

@Component
@Path("/wowhy1705")
@Scope("singleton")
public class Wowhy1705ApiImpl extends BaseApiImpl implements Wowhy1705Api {
	private static Logger logger = LoggerFactory.getLogger(Wowhy1705ApiImpl.class);
	private static Logger logWowhy1705ReqElkJson = LoggerFactory.getLogger("REQ_LOG_WOWHY1705");
	private static final String CAMP_DRAW_ID = "wowhy1705";
	private final String CACHE_KEY_PREFIX = "KFC.WOWHY1705.";
	private final String CACHE_KEY_USERDRAWCOUNT = CACHE_KEY_PREFIX+"DRAWUSERCOUNT-";
	private final String CACHE_KEY_USERDRAW = CACHE_KEY_PREFIX+"USERDRAW-";
	private final String CACHE_KEY_USERDRAWRECORD = CACHE_KEY_PREFIX+"USERDRAWRECORD-";
	private final String CACHE_KEY_USERAWARDWINCOUNT = CACHE_KEY_PREFIX+"AWARDWINCOUNT-";
	
	@Value("${campaign.svc.kbs.user}")			private String KBS_GET_USER_URL;
	@Value("${campaign.svc.kbs.win}")			private String KBS_WIN_URL;
	@Value("${campaign.svc.kbs.draw}")			private String KBS_GET_DRAW_URL;
	@Value("${campaign.svc.kbs.order}")			private String KBS_DEDUCT_ORDER__URL;
	@Value("${campaign.win.rate.algorithm}")	private String RATE_ALGORITHM;
	@Value("${campaign.kgold.activity}")		private String ACTIVITY;
//	@Value("${campaign.win.activity1}")			private String ACTIVITY_1;
//	@Value("${campaign.win.activity2}")			private String ACTIVITY_2;
	
	@Autowired
	private MemCachedClient memcachedClient;

	@Autowired
	private Wowhy1705Service service;
	
	private static enum DrawIntervalUnit {
		ALLTIME, DAY, WEEK;
		
		public static DrawIntervalUnit parse(String strUnit){
			if (strUnit==null || (strUnit=strUnit.trim()).isEmpty()){
				return null;
			}
			if("ALLTIME".equalsIgnoreCase(strUnit)) return ALLTIME;
			else if ("DAY".equalsIgnoreCase(strUnit)) return DAY;
			else if ("WEEK".equalsIgnoreCase(strUnit)) return WEEK;
			else throw new IllegalArgumentException("Unsupported DrawIntervalUnit ["+strUnit+"]");
		}
	}
	
	@Override
	public Result draw(Wowhy1705Parameter parameter, HttpServletRequest request) {
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
		
		//根据token获取用户信息
		Map<String, Object> userInfo = getUserInfoByToken(parameter.getToken());
		if(userInfo == null){
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is invalid");
		}
		parameter.setPhone(userInfo.get("phone").toString());
		parameter.setUserId(userInfo.get("userId").toString());
		parameter.setSsoUserId(userInfo.get("ssoUserId").toString());
		parameter.setCrmUserCode(userInfo.get("crmUserCode").toString());
		
		//防止刷单
//		if(!memcachedClient.add(CACHE_KEY_USERDRAW+parameter.getSsoUserId(), 1, new Date(5*1000))){
//			logger.error("draw frequently, ssoUserId:{}, userId:{}, phone:{}", parameter.getSsoUserId(), parameter.getUserId(), parameter.getPhone());
//			return new Result(ApiErrorCode.CALL_TOO_FREQUENTLY, "您的操作过于频繁，请稍后再试");
//		}
		
		long nowTime = new Date().getTime();
		Object ot = memcachedClient.get(CACHE_KEY_USERDRAW+parameter.getSsoUserId());
		logger.info("wowhy1705 try to add memcache:{}", memcachedClient.add(CACHE_KEY_USERDRAW+parameter.getSsoUserId(), 1, new Date(5*1000)));
		if(ot!=null){
			long lastTime = Long.parseLong(ot.toString());
			if(nowTime - lastTime < 3*1000){
				logger.error("draw frequently, ssoUserId:{}, userId:{}, phone:{}", parameter.getSsoUserId(), parameter.getUserId(), parameter.getPhone());
				return new Result(ApiErrorCode.CALL_TOO_FREQUENTLY, "您的操作过于频繁，请稍后再试");
			}
			memcachedClient.set(CACHE_KEY_USERDRAW+parameter.getSsoUserId(), nowTime);
		}
		
		//获取奖品信息
		Map<String, Object> drawInfo = getDrawInfo();
		if(drawInfo == null){
			return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "no draw info from KBS");
		} 
		
		//校验是否有资格参加
		if(!checkUserCanAttend(drawInfo, parameter.getSsoUserId())){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("attendLimit", drawInfo.get("attendLimit"));
			map.put("attendUnit", drawInfo.get("attendUnit"));
			
			return new Result(ApiErrorCode.DRAW_REACH_LIMIT, "User draw reach limit", map);
		}
		
		Wowhy1705Log wl = new Wowhy1705Log();
		wl.setReqFlag(""+request.hashCode());
		wl.setCallTime(new Date());
		wl.setParam(JSON.toJSONString(parameter));
		wl.setHeader(HttpMethod.GET.equals(request.getMethod())?null:("Content-Type: "+ContentType.APPLICATION_JSON.toString()));		
		wl.setToken(parameter.getToken());
		wl.setKfcBrandIP(NetUtil.getLocalIP());
		wl.setClientIP(WebUtil.getRealRemoteAddr(request));
		
		//生成订单号
		String orderId = IDHelper.newOrderID('1', String.valueOf(MybatisTblShardStrategy.byLastDigitOrLenMod10(parameter.getSsoUserId())));
		Wowhy1705Order order = new Wowhy1705Order();
		BeanUtils.copyProperties(parameter, order);
		order.setOrderId(orderId);
		order.setCreateTime(new Date());
		
		int awardWinLimit = 0;
		boolean won = false;
		Map<String, Object> kGlodOrder = null;
		try {
			//CRM下单扣K金&获取抽奖机会
			kGlodOrder = deductKglod(parameter.getToken(), orderId);
			//下单接口抛出异常
			if(kGlodOrder.get("success") == null){ 
				int errCode = Integer.valueOf(kGlodOrder.get("errCode").toString());
				String errMsg = String.valueOf(kGlodOrder.get("errMsg"));
				String errData = String.valueOf(kGlodOrder.get("errData"));
				
				wl.setResponse("place order failed, orderId:["+orderId+"], errCode:["+errCode+"], errMsg:["+errMsg+"], errData:["+errData+"]");
//				510514 表示积分不足
//				Ben<all_blue9527@qq.com> 2017/6/21 11:41:32
//				所以这个接口接到514你就当余额不足处理
//				蓝色天空(460953643) 2017/6/21 11:42:22
//				那placeOrder接口只要返回514，我是不是就认为是积分不足？
//				Ben<all_blue9527@qq.com> 2017/6/21 11:47:00
//				是的
				return new Result(errCode, errMsg, errData); 
			}
			
			order.setStatus(Boolean.valueOf(kGlodOrder.get("success").toString()));
			order.setReverse(Boolean.valueOf(kGlodOrder.get("isReverse").toString()));
			order.setFree(Boolean.valueOf(kGlodOrder.get("fromFreeChance").toString()));
			//下单失败
			if(!order.getStatus()){
				wl.setResponse("place order failed, orderId:["+orderId+"]");
				return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "faild to create order");
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
						
		} catch (Exception e) {
			wl.setResponse("error occurred when creating order, orderId:["+orderId+"]");
			return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "error occurred when creating order");
		}finally{
			//记录订单			
			saveOrder(order);
			
			if (!order.getStatus()) {
				wl.setResponseTime(new Date());
				String json = JSON.toJSONString(wl);
				logWowhy1705ReqElkJson.info(json);
			}
		}
		
		Map<String,Object> data = new HashMap<String,Object>();
		Wowhy1705Draw draw = new Wowhy1705Draw();
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
						if(parameter.getCouponType() != 4){
							parameter.setFrCrm(true);
						}
						//发放奖品
						KBSResultWin result = requestAwardFromKBS(parameter);
						if (result.isSuccess() && result.getData()!=null && result.getData().isWin() && StringUtils.isNotBlank(result.getData().getCode())){
							won = true;
							parameter.setAwardCode(result.getData().getCode());
							logger.info("got award from KBS, result is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
						} else {
							won = false;
							logger.error("failed request award from KBS, return is [{}], request param is [{}]", new Gson().toJson(result), new Gson().toJson(parameter));
							if(!"0".equals(result.getErrCode())) return new Result(Integer.valueOf(result.getErrCode()), result.getErrMsg(), result.getErrData());
						}
					}
				}
			} catch (Exception e) {
				won = false;
				logger.error("Oops! Something is wrong when requesting coupon from CRM. param[{}], exception[{}]", parameter, e);
				return new Result(ApiErrorCode.GENERAL_SERVER_ERROR, "error occurred when request award");
			}finally {
				draw.setWinAward(won ? 1 : 0);
				draw.setFree(order.isFree());
				if(won){
					draw.setAwardCode(parameter.getAwardCode());
					addOrIncrCache(CACHE_KEY_USERAWARDWINCOUNT+draw.getSsoUserId()+"-"+draw.getAwardType(), "1", null);
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
				addOrIncrCache(CACHE_KEY_USERDRAWCOUNT+dateFlag+"-"+draw.getSsoUserId(), "1", null);
				expireCache(CACHE_KEY_USERDRAWRECORD+draw.getSsoUserId());
				
				data.put("win", won);
				data.put("awardLevel", won ? draw.getAwardLevel() : "");
				data.put("awardName", won ? draw.getAwardName() : "");
				data.put("winImg", won ? draw.getWinImg() : "");
				data.put("personalImg", won ? draw.getPersonalImg() : "");
				data.put("isFree", order.isFree());
				data.put("url", won ? draw.getUrl() : "");
				data.put("couponType", won ? draw.getCouponType() : "");
				
				wl.setResponse(JSON.toJSONString(data));
				wl.setResponseTime(new Date());
				String json = JSON.toJSONString(wl);
				logWowhy1705ReqElkJson.info(json);
			}
		}
		
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
		logger.info("====wowhy1705 draw ====\n"+ JSON.toJSONString(result));
		return result;
	}
	
	private void saveDraw(final Wowhy1705Draw draw) {
//		service.saveDraw(draw);
		
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, draw);
			RabbitMQHelper.publish(Wowhy1705Draw.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
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

	private void saveOrder(final Wowhy1705Order order) {
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, order);
			RabbitMQHelper.publish(Wowhy1705Order.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					logger.warn("failed to publish to MQ, {}, turn to save to DB directly",  reason.getMessage(), reason);
					try{
						service.saveOrder(order);
					}catch(DuplicateKeyException e){
						logger.debug("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					}
				}
			});
		} else {
			service.saveOrder(order);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> deductKglod(String token, String transid) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("token", token);
		p.put("campId", CAMP_DRAW_ID);
		p.put("rateAlgorithm", RATE_ALGORITHM);
		p.put("transid", transid);
		p.put("activityId", ACTIVITY);
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

	private Map<String, Object> getUserInfoByToken(String token) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("token", token);
		String str = RestClientUtil.callPostService(KBS_GET_USER_URL, p, String.class);
		logger.info("request user info from kbs, param[{}], result[{}]", p, str);
		JSONObject object = JSONObject.parseObject(str).getJSONObject("data");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(object == null){
			return null;
		}
		else{
			resultMap.put("phone", object.getString("phone"));
			resultMap.put("userId", object.getString("userId"));
			resultMap.put("ssoUserId", object.getString("ssoUserId"));
			resultMap.put("crmUserCode", object.getString("crmUserCode"));
		}
			
		return resultMap;
	}
	
	private Map<String, Object> getDrawInfo() {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("campId", CAMP_DRAW_ID);
		String str = RestClientUtil.callGetService(KBS_GET_DRAW_URL, p, String.class);
		logger.info("request award from kbs, param[{}], result[{}]", p, str);
		JSONObject object = JSONObject.parseObject(str).getJSONObject("data");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(object == null){
			return null;
		}else{
			resultMap.put("startTime", object.getString("startTime"));
			resultMap.put("endTime", object.getString("endTime"));
			resultMap.put("attendLimit", object.getString("attendLimit"));
			resultMap.put("attendUnit", object.getString("attendUnit"));
		}
			
		return resultMap;
	}

	private KBSResultWin requestAwardFromKBS(Wowhy1705Parameter param) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("channelType", param.getChannelType());
		p.put("token", param.getToken());
		p.put("awardType", param.getAwardType());
		p.put("activityId", param.getActivityId());
		p.put("frCrm", param.isFrCrm());
		p.put("couponType", param.getCouponType());
		String sr = RestClientUtil.callPostService(KBS_WIN_URL, p, String.class);
		logger.info("request award from kbs, param[{}], result[{}]", param, sr);
		KBSResultWin wr = new Gson().fromJson(sr, KBSResultWin.class);
		return wr;
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
	
	private Long getUserDrawCount(final String ssoUserId, final Date beginTime){
		String dateFlag = DateUtil.formatDate(beginTime, "yyyyMMdd");
//		Date midNight = DateUtil.getMidNight(beginTime);
		
		Object o = memcachedClient.get(CACHE_KEY_USERDRAWCOUNT+dateFlag+"-"+ssoUserId);
		
//		Object o = getCache(CACHE_KEY_USERDRAWCOUNT+dateFlag+"-"+ssoUserId, midNight, CacheOp.ADD, new DataGenerator(){
//			@Override
//			public Object generate() {
//				Long drawCount = service.getUserDrawCount(ssoUserId, beginTime, new Date());
//				return drawCount;
//			}
//		});
//		
		String so = String.valueOf(o);
		
		if (o==null||"null".equals(so)||so.isEmpty()) return 0L;
		return Long.valueOf(so);
	}
	
	private Long getUserAwardWinCount(final String ssoUserId, final int awardType){
		Object o = getCache(CACHE_KEY_USERAWARDWINCOUNT+ssoUserId+"-"+awardType, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				Long awardWinCount = service.getUserAwardWinCount(ssoUserId, awardType);
				return awardWinCount;
			}
		});

		String so = String.valueOf(o);
		
		if (o==null||"null".equals(so)||so.isEmpty()) return 0L;
		return Long.valueOf(so);
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
	
	private void addOrIncrCache(String key, String inc, Date expiry){
		if(!memcachedClient.add(key, inc, expiry)){
			memcachedClient.incr(key);
		}
	}
	
	private void expireCache(String key){
		memcachedClient.delete(key);
	}

	@Override
	public Result getDrawRecord(String token, HttpServletRequest request) {
		if(token == null || token.isEmpty()){
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is empty");
		}
		
		//根据token获取用户信息
		Map<String, Object> userInfo = getUserInfoByToken(token);
		if(userInfo == null){
			return new Result(ApiErrorCode.INVALID_TOKEN, "token is invalid");
		}
		
		String ssoUserId =userInfo.get("ssoUserId").toString();
		
		 List<Wowhy1705Draw> draws = getUserDrawRecord(ssoUserId);
		 List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		 for(Wowhy1705Draw draw: draws){
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
		
		Result result = new Result(ApiErrorCode.GENERAL_SERVER_SUCCESS, null, data);
//		logger.info("====wowhy1705 record ====\n"+ JSON.toJSONString(result));
		logger.info("====wowhy1705 record ====\n total record:"+draws.size());
		return result;
	}
	
	private List<Wowhy1705Draw> getUserDrawRecord(final String ssoUserId){
		Object o = getCache(CACHE_KEY_USERDRAWRECORD+ssoUserId, null, CacheOp.ADD, new DataGenerator(){
			@Override
			public Object generate() {
				List<Wowhy1705Draw> draws = service.getUserDrawRecord(ssoUserId);
				return JSONObject.toJSONString(draws);
			}
		});
		
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) return null;
		return JSON.parseObject(so, new TypeReference<List<Wowhy1705Draw>>(){}.getType());
	}
}
