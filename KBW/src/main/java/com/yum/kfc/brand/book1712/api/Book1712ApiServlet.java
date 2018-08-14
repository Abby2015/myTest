package com.yum.kfc.brand.book1712.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.ErrorCodeRuntimeException;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.cache.CacheUtil;
import com.hp.jdf.ssm.rmq.FailPublishHandler;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.DateUtil;
import com.hp.jdf.ssm.util.SpringUtil;
import com.hp.jdf.ssm.util.StringUtil;
import com.yum.kfc.brand.ErrCode;
import com.yum.kfc.brand.api.BaseBrandApiServlet;
import com.yum.kfc.brand.api.EGiftCardApiServlet;
import com.yum.kfc.brand.api.WXFeiruiApiServlet;
import com.yum.kfc.brand.api.util.SsoApiHelper;
import com.yum.kfc.brand.api.util.SsoApiHelper.SsoUser;
import com.yum.kfc.brand.book1712.pojo.Book1712Order;
import com.yum.kfc.brand.book1712.pojo.Book1712Share;
import com.yum.kfc.brand.book1712.service.Book1712Service;
import com.yum.kfc.brand.camp.pojo.ActivityType;
import com.yum.kfc.brand.common.api.BaseCampApiServlet;
import com.yum.kfc.brand.common.utils.ShareUtil;
import com.yum.kfc.brand.common.utils.UserInfoFromKBS;
import com.yum.kfc.brand.crm.pojo.ClientChannel;
import com.yum.kfc.brand.user.pojo.User;

@WebServlet(urlPatterns = "/api/book/*", asyncSupported=true)
public class Book1712ApiServlet extends BaseCampApiServlet {
	private static final long serialVersionUID = -1178878975247477865L;
	private static final Logger log = LoggerFactory.getLogger(Book1712ApiServlet.class);
	
//	private static final MemCachedClient memcachedClient = SpringUtil.getBean(MemCachedClient.class);
//	private static final MemcacheUtil memcacheUtil = SpringUtil.getBean(MemcacheUtil.class);
//	private static final boolean USE_KBS_CACHE = Boolean.parseBoolean(ApplicationConfig.getProperty("api.cacheutil.kbs", "false"));
//	private static final UserApiService userService = SpringUtil.getBean(UserApiService.class);
	private static final Book1712Service bookService = SpringUtil.getBean(Book1712Service.class);
//	private static final ShareService shareService = SpringUtil.getBean(ShareService.class);
	private static final boolean PROD_ENV = Boolean.parseBoolean(ApplicationConfig.getProperty("api.server.env.prod", "false"));
	private static final int BOOK_ALL_COUNT_MAX = Integer.parseInt(ApplicationConfig.getProperty("api.camp.book.all.max", "5000"));
	private static final int BOOK_SINGLE_COUNT_MAX = Integer.parseInt(ApplicationConfig.getProperty("api.camp.book.single.max", "2"));
	private static final String BOOK_INVITE_URL = ApplicationConfig.getProperty("api.camp.book.invite.url", "https://login.kfc.com.cn/CRM/superapp_wechat/PaymentWechat/index_grouponWX.html?shareId=");
	private static final int DELAY_EXPIRE_CACHE_MS = Integer.parseInt(ApplicationConfig.getProperty("api.camp.book.share.cache.delay.ms", "500"));
	
	private static final boolean MULTI_GROUP = Boolean.parseBoolean(ApplicationConfig.getProperty("api.camp.book.group.multi", "false"));
	
	private static final String[] WX_MSGS = new String[]{
		"恭喜您加入拼团, 再邀请%1$s位小伙伴即可拼团成功！快去<a href='%2$s'>邀请好友</a>吧!",
		"您的好友%1$s加入拼团，还差%2$s位小伙伴哦！快去<a href='%3$s'>邀请好友</a>吧!",
		"恭喜您拼团成功！"
	};
	
	private static final Date CAMP_BOOK_START, CAMP_BOOK_END;
	static{
		try {
			CAMP_BOOK_START = DateUtil.parseDate(ApplicationConfig.getProperty("camp.book.start", "2017-10-30 00:00:00"), "yyyy-MM-dd HH:mm:ss");
			CAMP_BOOK_END = DateUtil.parseDate(ApplicationConfig.getProperty("camp.book.end", "2017-12-26 23:59:59"), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			throw new IllegalArgumentException("incorrect configuration items, "+e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("serial")
	@GET @Path("isAttend")
	private void userIsAttend(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token = super.getStrParam(req, "token", null);
		
		if(StringUtil.isEmptyWithTrim(token)){
			throw new ApiException("Bad request", ErrCode.INVALID_TOKEN).setContext("invalid token");
		}

		//根据token获取用户信息
		UserInfo ui = super.getUserInfoByToken(token, req.getHeader("kbck"));
		if(ui == null){
			throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
		}	
		final String userId = ui.getUserId();
		final String ssoUserId = ui.getSsoUserId();
		final String crmUserCode = ui.getCrmUserCode();
		final String phone = ui.getPhone();
		
		List<Book1712Order> orders = getUserBookOrder(userId);
		
		boolean isAttend = false;
		String shareId = null;
		
		Book1712Order bOrder = orders==null||orders.size()==0?null:orders.get(0);
		if(bOrder != null){
			Object[] r = isAttendOfOrder(bOrder, userId, ssoUserId, crmUserCode, token, phone, req);
			isAttend = (Boolean)r[0];
			shareId = (String)r[1];
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("isAttend", isAttend);
		result.put("fromShareId", shareId);
		
		if (MULTI_GROUP){
			List<Map<String, Boolean>> all = new ArrayList<Map<String, Boolean>>();
			
			if (bOrder!=null){
				final String sid = shareId; final boolean ia = isAttend;
				all.add(new HashMap<String, Boolean>(){{put(sid, ia);}});
				
				for(int i=1; i<orders.size(); i++){
					final Object[] r = isAttendOfOrder(bOrder, userId, ssoUserId, crmUserCode, token, phone, req);
					if (r[1]!=null) all.add(new HashMap<String, Boolean>(){{put((String)r[1], (Boolean)r[0]);}});
				}
			}
			
			result.put("all", all);
		}
		
		Date now = new Date();
		boolean isCampFinished = (now.after(CAMP_BOOK_END)) || (bookSellCount() >= BOOK_ALL_COUNT_MAX);
		result.put("isFinished", isCampFinished);

		req.setAttribute(RESULT, result);	
	}
	
	/**
	 * @return [0] isAttend, [1] shareId
	 */
	private Object[] isAttendOfOrder(Book1712Order bOrder, String userId, String ssoUserId, String crmUserCode, String token, String phone, HttpServletRequest req) throws ServletException, IOException{
		boolean isAttend = false;
		String shareId = null;

		final String orderId = bOrder.getOrderId();
		final String fromShareId = bOrder.getFromShareId();
//		result.put("orderId", orderId);
//		result.put("fromShareId", fromShareId);
		
		Book1712Share bShare = getUserBookShare(fromShareId, userId);
		if(bShare != null){
			isAttend = true;
			shareId = fromShareId;
		}else {
			boolean orderCompleted = false;
			final String orderStatus = bOrder.getStatus2();
			if (isPaidStatus(orderStatus)){
				orderCompleted = true;
			} else {//not paid yet, try to query again to see if it was paid already
				final ClientChannel clientChannel = ClientChannel.kbwc;
				req.setAttribute(REQ_CLIENT_CHANNEL, clientChannel);
				final int reqHashCode = req.hashCode();
				final String clientIP = getClientIP(req);
				
				Book1712Order updateOrder = new Book1712Order();
				updateOrder.setOrderId(orderId);
				updateOrder.setBrandUserId(userId);
				updateOrder.setUpdateTime(new Date());
				
				JSONObject orderJo = null;
				boolean invalidOrder = false;
				try {
					orderJo = EGiftCardApiServlet.queryOrderForStu(ActivityType.KMALL, req, reqHashCode, orderId, token, ssoUserId, phone, clientIP, clientChannel);
				} catch (ApiException e) {
					updateOrder.setOrder2(String.valueOf(e.getContext()));
					invalidOrder = true;
				}
				
				String orderStatusNew = null;
				if(orderJo != null){
					orderStatusNew = orderJo.getString("orderStatus");
					updateOrder.setStatus2(orderStatusNew);
					updateOrder.setOrder2(orderJo.toJSONString());
				}
				
				if(isPaidStatus(orderStatusNew)){
					orderCompleted = true;
					updateBookOrder(updateOrder);
				}else {
//					2017/12/05
//					沈雪 阿拉伯公主  10:17:03
//					嗯，我的理解这个订单30分钟未支付就是取消状态了
//					沈雪 阿拉伯公主  10:17:53
//					半个小时
//					沈雪 阿拉伯公主  10:18:00
//					跟商城确认了
//					蓝色天空  10:18:26
//					也就是说30分钟后，我再拿这个orderId去获取payUrl是不行的？
//					沈雪 阿拉伯公主  10:19:17
//					对的
//					沈雪 阿拉伯公主  10:19:31
//					这个订单是已删除的状态，拿这个id只能查询不能做任何事情
					long createTimeStamp = bOrder.getCreateTime().getTime();
					long nowTimeStamp = System.currentTimeMillis(); 
					if(invalidOrder || (createTimeStamp - nowTimeStamp > 30*60*1000)){ 
						updateOrder.setIsValid(false);
						updateBookOrder(updateOrder);
					}
				}
			}
			
			if(orderCompleted){
				isAttend = true;
				shareId = fromShareId;
				
				final Book1712Share bs = new Book1712Share();
				bs.setOrderId(orderId);
				bs.setBrandUserId(userId);
				bs.setSsoUserId(ssoUserId);
				bs.setCrmUserCode(crmUserCode);
				bs.setHeadImg(bOrder.getHeadImg());
				bs.setOpenId(bOrder.getOpenId());
				bs.setNickname(bOrder.getNickname());
				bs.setPhone(phone);
				bs.setUnionId(bOrder.getUnionId());
				bs.setCreateTime(new Date());
				bs.setShareId(fromShareId);
				String userShareId = ShareUtil.generateShareId(userId, "book1712", orderId);
				bs.setSharer(userShareId.equals(fromShareId));
				
				boolean added = false;
				try {
					added = CacheUtil.add(CacheUtil.getDefaultCacheName(), "CAMP_BOOK_"+orderId+"_SAVE_MUTEX", 1, 10);
					if (added || CacheUtil.isCacheDown(CacheUtil.getDefaultCacheName(), "CAMP_BOOK_"+orderId+"_SAVE_MUTEX")){
						saveBookShare(bs);
					}
				}finally{
					if (added) {
						CacheUtil.expire(CacheUtil.getDefaultCacheName(), "CAMP_BOOK_"+orderId+"_SAVE_MUTEX");
					}
				}
			}
		}
		
		return new Object[]{isAttend, shareId};
	}
	
	@GET @Path("detail")
	private void shareDetail(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String shareId = super.getStrParam(req, "shareId", null);
		
		if(StringUtil.isEmptyWithTrim(shareId)){
			throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("shareId is empty");
		}
		
		final Book1712ApiServlet thiz = this;
		final int reqHashCode = req.hashCode();
		final String clientIP = getClientIP(req);
		final ClientChannel clientChannel = getClientChannel(req);
		
		super.asyncExec(req, resp, new SlowOperation(){
			@Override
			public Object exec() throws Exception {
				List<Book1712Share> shareList = getBookShares(shareId);
				
				if (shareList==null || shareList.isEmpty()){
					try{//sleep awhile and then try to query again
						Thread.sleep(800);
					}catch(InterruptedException ignore){}
					
					shareList = getBookShares(shareId);
					if (shareList==null||shareList.isEmpty()){
						List<Book1712Order> orders = bookService.getNotPaiedBookOrdersByShareId(shareId);
						for(Book1712Order order: orders){
							queryBookOrder(order, ActivityType.KMALL, false, shareId, order.getOrderId(),
											"queryOnBehalfOfUser", order.getBrandUserId(), order.getPhone(), order.getSsoUserId(), order.getCrmUserCode(),
											order.getNickname(), order.getHeadImg(), order.getOpenId(), order.getUnionId(),
											reqHashCode, clientIP, clientChannel, req, thiz);
						}
						shareList = getBookShares(shareId);
					}
				}
				
				List<Map<String, Object>> attenderList = new ArrayList<Map<String, Object>>();
				if(shareList != null){
					for(Book1712Share share: shareList){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("orderId", share.getOrderId());
						map.put("isSharer", share.isSharer());
						map.put("nickname", share.getNickname());
						map.put("headImg", share.getHeadImg());
						map.put("createTime", share.getCreateTime());
						
						attenderList.add(map);
					}
				}
					
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("detail", attenderList);
				
				boolean success = false;
				if(shareList != null && (shareList.size() >= BOOK_SINGLE_COUNT_MAX)){
					success = true;
					result.put("completeTime", shareList.get(shareList.size()-1).getCreateTime());
				}
				result.put("isSuccess", success);

				Date now = new Date();
				boolean isCampFinished = (now.after(CAMP_BOOK_END)) || (bookSellCount() >= BOOK_ALL_COUNT_MAX);
				result.put("isFinished", isCampFinished);
				
				req.setAttribute(RESULT, result);	
				
				return result;
			}
		});
	}
	
	@GET @Path("pay/wx/mp")
	private void bookForWeChatMediaPlatform(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		bookForWeChatMediaPlatform(req, resp, false);
	}
	@GET @Path("pay/wx/mp/tu")
	private void bookForWeChatMediaPlatformForTmpUser(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		bookForWeChatMediaPlatform(req, resp, true);
	}
	private void bookForWeChatMediaPlatform(final HttpServletRequest req, final HttpServletResponse resp, final boolean tmpUser) throws ServletException, IOException {
		checkCampaignPeriod(CAMP_BOOK_START, CAMP_BOOK_END);
		
		final String key = req.getParameter("key");
		String pv = getStrParam(req, "pv", null);
		JSONObject jo = JSON.parseObject(pv);
		pv = jo.getString("pv");
		final boolean validPV = pv!=null && !(pv=pv.trim()).isEmpty()
								&& (pv.indexOf("..")<0
									|| (pv.indexOf("?")>0 && pv.indexOf("..")>pv.indexOf("?")
									));
		
		final String pathVersion = pv;
		
		final String retPara = jo.getString("retPara");//原样返回
		final String token = jo.getString("token");
		final int count = jo.getIntValue("count");
		final String activityId = jo.getString("activityId");
		final String categoryName = jo.getString("categoryName");
		final int pay = jo.getIntValue("payType");//1 zfb, 2 wechat
		final int payChannel = jo.getIntValue("payChannel");//1 APP, 2 WAP, 3 WEB
		final boolean loadTest = jo==null?false:jo.getBooleanValue("lt");
		final String tuPhoneStr = tmpUser?jo.getString("phone"):null;
		final String fromShareId = jo.getString("fromShareId");
		final JSONObject joShipment= jo.getJSONObject("shipment");
		
		final ClientChannel clientChannel = ClientChannel.kbwc;
		req.setAttribute(REQ_CLIENT_CHANNEL, clientChannel);
		final int reqHashCode = req.hashCode();
		final String clientIP = getClientIP(req);
		
		final Book1712ApiServlet thiz = this;
		
		asyncExec(req, resp, new SlowOperation(){@Override public Object exec() throws Exception {
			try{
				checkUserCanAttend(fromShareId);
				
				if (!validPV) throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("incorrect pv param, "+pathVersion);
				
				if (StringUtil.isAnyEmptyWithTrim(activityId, categoryName)) throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("exist empty param:[" + getBodyAsString(req)+"]");
				
				if (pay!=1 && pay!=2) throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("invalid pay type ["+pay+"]");
				if (payChannel<1 || payChannel>4) throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("invalid pay channel ["+payChannel+"]");
				
				String tuPhone = StringUtil.safeTrim(tuPhoneStr);
				if (tmpUser){
					if(tuPhone==null || tuPhone.length()!=11 || !tuPhone.startsWith("1")){
						throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("no/invalid phone provided ["+tuPhoneStr+"]");
					}
					try{
						Long.parseLong(tuPhone);
					}catch(Exception e){
						throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("no/invalid phone provided ["+tuPhoneStr+"]");
					}
				}
				
				Map<String, String> map = WXFeiruiApiServlet.getNewWechatUserInfo(key, req.hashCode(), clientIP, clientChannel, token);
//				Map<String, String> map = new HashMap<String, String>(){{put("openid","o1Z-rjuFOAkQ8YUe9YLTNr0BjzlI");put("nickname","测试");put("unionid","abc12345"+System.currentTimeMillis());}};
				
				String openId = map.get("openid");
				if (openId==null||openId.trim().isEmpty()) throw new ApiException("Failed get OpenID from FR", ErrCode.GENERAL_SERVER_ERROR_FER).setContext(map);
				
				String unionid = map.get("unionid");
				
				String headImagePath = map.get("headimgurl");
				String subscribe = map.get("subscribe");
				String nn = map.get("nickname");
				
				String userId, ssoUserId, phone, crmUserCode;
				SsoUser su = null;
				if(loadTest) {
					userId = "u"+token;
					ssoUserId = "s"+token;
					phone = token.substring(1);//suppose token formated as [.][0-9]{2,}
					crmUserCode = "m"+token;
				} else {
					Map<String, Object> userInfoMap = UserInfoFromKBS.getUserInfoFromKBSByOpenId(openId);
					User user = JSON.parseObject(String.valueOf(userInfoMap.get("brandUser")), User.class);
					su = JSON.parseObject(String.valueOf(userInfoMap.get("ssoUser")), SsoUser.class);
					userId = user.getId();
					ssoUserId = su.getId();
					phone = SsoApiHelper.dec(su.getPhone(), clientChannel);
					crmUserCode = su.getUserCode();
				}
				
//				SsoUser su = new SsoUser();su.setId("19126e71-4e0f-4b4a-9657-6844a5d89a5f_0");
//				String userId = "151ADDD79BE51CEB9BC62E74AF6B4FE4690D2A17AEE";
//				String phone = "15926326995";
//				String crmUserCode = "2095238958";
//				String ssoUserId = su.getId();
				
				if(crmUserCode == null){
					crmUserCode = getCrmUserIdByUserId(userId, reqHashCode, clientIP, null, clientChannel, req, su);
					if((crmUserCode==null||crmUserCode.trim().isEmpty())) {
						throw new ApiException("Internal Server Error", ErrCode.GENERAL_SERVER_ERROR).setContext("not crm member for user id ["+userId+"], ssoUser["+JSON.toJSONString(su)+"]");
					}
				}
				
				
//				if(checkUserAttendAready(userId)){
//					throw new ApiException("Bad request", ErrCode.REACH_LIMIT).setContext("User attend already. userId:["+userId+"]");
//				}
				
				//检查用户是否有未支付的订单
				String orderId=null, shareIdInDB=null;
				List<Book1712Order> orders = getUserBookOrder(userId);
				if (MULTI_GROUP){
					if (orders!=null && orders.size()>0){
						for(Book1712Order bo: orders){
							if(!isPaidStatus(bo.getStatus2())){
								orderId = bo.getOrderId();
								shareIdInDB = bo.getFromShareId();
								break;
							}
						}
					}
				} else {
					Book1712Order bo = orders==null||orders.size()==0?null:orders.get(0);
					if(bo != null){
						orderId = bo.getOrderId();
						shareIdInDB = bo.getFromShareId();
						String orderStatus = bo.getStatus2();
						if(isPaidStatus(orderStatus)){
							throw new ApiException("Bad request", ErrCode.REACH_LIMIT).setContext("User attend already. userId:["+userId+"], orderId["+orderId+"]");
						}
					}
				}
				
				//create order and get payUrl
				JSONObject shipmentJo = new JSONObject();
				if (joShipment !=null) shipmentJo = joShipment;
				Map<String, Object> result = placeOrderAndGetPayUrl(ActivityType.KMALL, loadTest, orderId, shareIdInDB, fromShareId, activityId, categoryName, count, pay, payChannel,
												openId, unionid, nn, headImagePath, token, ssoUserId, userId, phone, crmUserCode,
												reqHashCode, clientIP, clientChannel, req,
												shipmentJo.getString("province"), shipmentJo.getString("city"), shipmentJo.getString("area"), shipmentJo.getString("addressDetail"), shipmentJo.getString("personName"), shipmentJo.getString("phoneNum")
												);
				
				resp.setStatus(302);
				resp.setHeader("Location", (PROD_ENV?"https://login.kfc.com.cn/":"https://tlogin.kfc.com.cn/")+pathVersion
														+(pathVersion.contains("?")?"&":"?")
														+"data="+URLEncoder.encode(JSON.toJSONString(result), "UTF-8")
														+(retPara==null?"":("&retPara="+URLEncoder.encode(retPara, "UTF-8")))
														+"&openid="+openId+"&headImgPath="+(headImagePath==null?"":URLEncoder.encode(headImagePath, "UTF-8"))
														+"&subscribe="+(subscribe==null?"0":subscribe)+"&nickname="+(nn==null?"":URLEncoder.encode(nn, "UTF-8"))
														);
			}catch(Exception e){
				log.error("request@{} {}: failed when book/pay/wx/mp, {}", reqHashCode, logTime(), StringUtil.getMsgOrClzName(e, true), e);
				if (log.isTraceEnabled()) log.trace("request@{} {}: failed when book/pay/wx/mp, {}", reqHashCode, logTime(), StringUtil.getMsgOrClzName(e, true), e);
				
				String cem = thiz.customizeMsgForThrowable(e);
				WXFeiruiApiServlet.handleFailedWxRedirect(e, reqHashCode, (PROD_ENV?"https://login.kfc.com.cn/":"https://tlogin.kfc.com.cn/")+pathVersion, retPara, cem, req, resp);
			}
			
			return null;
		}});
	}
	
	private Map<String, Object> placeOrderAndGetPayUrl(ActivityType activityType, Boolean loadTest, String orderId, String shareIdInDB, String fromShareId, final String activityId, final String categoryName, final int count, final int pay, final int payFrom, String openid,
			String unionid, String nickname, String headImg, final String token, final String ssoUserId, final String userId, String phone, String crmUserCode, final int reqHashCode, final String clientIP,
			final ClientChannel clientChannel, final HttpServletRequest req, String shipmentProvince, String shipmentCity, String shipmentArea, String shipmentAddressDetail, String shipmentPersonName,
			String shipmentPhoneNum) throws ServletException, IOException {
		
		String payType = getPayType(pay);
		String payChannel = getPayChannel(payFrom);

		boolean hasNonPaidOrderAlready = orderId!=null;
		JSONObject order = null;
		if(!hasNonPaidOrderAlready){//no not paid order yet, so, create a new order
			if (loadTest) {
				order = JSON.parseObject("{\"orderNo\":\"on" + token + "\",\"orderStatus\":\"os" + token + "\"}");
				orderId = order.getString("orderNo");
			} else {
				order = EGiftCardApiServlet.createOrder(activityType, null, null, req, reqHashCode, new String[]{activityId}, categoryName, new int[]{count}, phone, "PERSON", crmUserCode, openid, token, userId, ssoUserId, clientIP, clientChannel);
				orderId = order.getString("orderNo");
				//save user order adress
				if (!StringUtil.isAnyEmptyWithTrim(shipmentProvince, shipmentCity, shipmentArea, shipmentAddressDetail, shipmentPersonName, shipmentPhoneNum)) {
					EGiftCardApiServlet.saveShipmentAddr(ActivityType.KMALL, req, reqHashCode, orderId, shipmentProvince, shipmentCity, shipmentArea, shipmentAddressDetail, shipmentPersonName,
							shipmentPhoneNum, phone, crmUserCode, null, token, userId, clientIP, clientChannel);
				}
			}
		}
		
		//get payUrl
		JSONObject payUrl = null;
		Exception payUrlException = null;
		try {
			if (loadTest) {
				payUrl = JSON.parseObject("{\"payUrl\":\"op" + token + "\"}");
			} else {
				payUrl = EGiftCardApiServlet.getPayUrl(activityType, req, reqHashCode, orderId, payType, payChannel, openid, token, userId, crmUserCode, phone, clientIP, clientChannel);
			}
		} catch (RuntimeException e) {
			payUrlException = e;
			throw e;
		} finally {
			if (hasNonPaidOrderAlready){
				Book1712Order updateOrder = new Book1712Order();
				updateOrder.setOrderId(orderId);
				updateOrder.setBrandUserId(userId);
				updateOrder.setFromShareId(fromShareId);
				updateOrder.setUpdateTime(new Date());
				updateOrder.setPayUrl(payUrl==null?(payUrlException==null?null
																		:(StringUtil.getMsgOrClzName(payUrlException, true)+"\n\n"+StringUtil.getFullStackTrace(payUrlException)))
												:payUrl.toJSONString());
				updateBookOrder(updateOrder);
			}else {
				final Book1712Order bo = new Book1712Order();
				bo.setOrderId(orderId);
				bo.setStatus(order.getString("orderStatus"));
				bo.setCreateTime(new Date());
				bo.setCrmUserCode(crmUserCode);
				bo.setBrandUserId(userId);
				bo.setSsoUserId(ssoUserId);
				bo.setPhone(phone);
				bo.setActivityId(activityId);
				bo.setCategoryName(categoryName);
				bo.setCount(count);
				bo.setPayType(payType);
				bo.setPayChannel(payChannel);
				bo.setOpenId(openid);
				bo.setUnionId(unionid);
				bo.setNickname(nickname);
				bo.setHeadImg(headImg);
				bo.setOrder(order.toJSONString());
				try{bo.setSubTotal(order.getJSONObject("orderInfoPojo").getString("subTotal"));}catch(Exception e){}
				try{bo.setGrandTotal(order.getJSONObject("orderInfoPojo").getString("grandTotal"));}catch(Exception e){}
				
				bo.setPayUrl(payUrl==null?(payUrlException==null?null
																:(StringUtil.getMsgOrClzName(payUrlException, true)+"\n\n"+StringUtil.getFullStackTrace(payUrlException)))
										:payUrl.toJSONString());
				if(fromShareId == null){
					fromShareId = ShareUtil.generateShareId(userId, "book1712", orderId);
				}
				
				bo.setFromShareId(fromShareId);

				CacheUtil.expire(CacheUtil.getDefaultCacheName(), "BOOK1712_USER_ORDER_"+userId);
				if (RabbitMQHelper.RABBIT_MQ_ENABLED) {
					Message msg = new Message(Message.Type.CREATE, bo);
					RabbitMQHelper.publish(Book1712Order.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
						@Override
						public void handleFailPublish(Throwable reason, Message msg) {
							bookService.saveBookOrder(bo);//insert
							CacheUtil.expire(CacheUtil.getDefaultCacheName(), "BOOK1712_USER_ORDER_"+userId);
						}
					});
				} else {
					bookService.saveBookOrder(bo);//insert
					CacheUtil.expire(CacheUtil.getDefaultCacheName(), "BOOK1712_USER_ORDER_"+userId);
				}
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("orderId", orderId);
		if(!hasNonPaidOrderAlready){
			result.put("orderStatus", order.getString("orderStatus"));
			try{result.put("subTotal", order.getJSONObject("orderInfoPojo").getString("subTotal"));}catch(Exception e){}
			try{result.put("grandTotal", order.getJSONObject("orderInfoPojo").getString("grandTotal"));}catch(Exception e){}
		}
		
		try {
			result.put("payUrl", JSON.parseObject(payUrl.getString("payUrl")));
		} catch (ClassCastException e) {
			result.put("payUrl", payUrl.getString("payUrl"));
		} catch (JSONException e) {
			result.put("payUrl", payUrl.getString("payUrl"));
		}
		return result;
	}	
	
	@GET @Path("order")
	private void queryBookOrderSG(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		queryBookOrder(req, resp, false);
	}
	
	@GET @Path("order/mg")
	private void queryBookOrderMG(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		queryBookOrder(req, resp, true);
	}
	
	private void queryBookOrder(final HttpServletRequest req, HttpServletResponse resp, boolean multiGroup) throws ServletException, IOException {
		final String token = super.getStrParam(req, "token", null);
		String orderId = super.getStrParam(req, "orderId", null);
//		String fromShareId = super.getStrParam(req, "fromShareId", null);
		final boolean loadTest = super.getBoolParam(req, "lt", false);
		
		if(StringUtil.isEmptyWithTrim(token)){
			throw new ApiException("Bad request", ErrCode.INVALID_TOKEN).setContext("invalid token");
		}
		
//		if(StringUtil.isAnyEmptyWithTrim(token, orderId, fromShareId)){
//			throw new ApiException("Bad request", ErrCode.GENERAL_CLIENT_ERROR).setContext("Missing param. token["+token+"], orderId["+orderId+"], fromShareId["+fromShareId+"]");
//		}
		
		final String userId, ssoUserId, phone, crmUserCode;
		final int reqHashCode = req.hashCode();
		final String clientIP = getClientIP(req);
		final ClientChannel clientChannel = ClientChannel.kbwc;
		if(loadTest) {
			userId = "u"+token;
			ssoUserId = "su"+token;
			phone = token.substring(1);//suppose token formated as [.][0-9]{2,}
			crmUserCode = "m"+token;
		} else {
			//根据token获取用户信息
			UserInfo ui = super.getUserInfoByToken(token, req.getHeader("kbck"));
			if(ui == null){
				throw new ApiException("Bad request", ErrCode.GENERAL_SERVER_ERROR).setContext("failed to get userInfo from KBS, token["+token+"]");
			}
			userId = ui.getUserId();
			ssoUserId = ui.getSsoUserId();
			crmUserCode = ui.getCrmUserCode();
			phone = ui.getPhone();
		}
		
//		SsoUser su = new SsoUser();su.setId("19126e71-4e0f-4b4a-9657-6844a5d89a5f_0");
//		final String ssoUserId = su.getId();
//		final String userId = "151ADDD79BE51CEB9BC62E74AF6B4FE4690D2A17AEE";
//		final String phone = "15926326995";
//		final String crmUserCode = "2095238958";
		
		final List<Book1712Order> orders = getUserBookOrder(userId);
		final String crmUserId = crmUserCode;
		final BaseBrandApiServlet thiz = this;
		if ((multiGroup || MULTI_GROUP) && StringUtil.isEmptyWithTrim(orderId)) {//return all orders
			if (orders!=null){
				asyncExec(req, resp, new SlowOperation(){@Override public Object exec() throws Exception {
					List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
					for(Book1712Order bOrder: orders){//TODO change to batch query API?
						Map<String, Object> mo = queryOrder(bOrder, token, phone, userId, ssoUserId, crmUserId, reqHashCode, clientIP, clientChannel, req, loadTest, thiz);
						if (mo!=null && !mo.isEmpty()) result.add(mo);
					}
					return result;
				}});
			}
		} else {//return specified order or latest order
			Book1712Order bOrder = null;
			if ((multiGroup || MULTI_GROUP)){
				bOrder = getOrderOfId(orders, orderId);
			} else {
				bOrder = orders==null||orders.size()==0?null:orders.get(0);
			}
			if(bOrder != null){
				final Book1712Order order = bOrder;
				asyncExec(req, resp, new SlowOperation(){@Override public Object exec() throws Exception {
					return queryOrder(order, token, phone, userId, ssoUserId, crmUserId, reqHashCode, clientIP, clientChannel, req, loadTest, thiz);
				}});
			}
		}
	}

	private Book1712Order getOrderOfId(final List<Book1712Order> orders, String orderId) {
		if (orders==null) return null;
		
		for(Book1712Order o: orders){
			if (orderId.equals(o.getOrderId())){
				return o;
			}
		}
		
		return null;
	}

	private Map<String, Object> queryOrder(Book1712Order bOrder, final String token, final String phone, final String userId, final String ssoUserId, final String crmUserId,
			final int reqHashCode, final String clientIP, final ClientChannel clientChannel, final HttpServletRequest req, final boolean loadTest, final BaseBrandApiServlet thiz)
			throws ServletException, IOException {
		String wxNickname = bOrder.getNickname(); 
		String wxHeadImg = bOrder.getHeadImg();
		String openId = bOrder.getOpenId();
		String unionId = bOrder.getUnionId();
		
		String finalOrderId = bOrder.getOrderId();
		String finalFromShareId = bOrder.getFromShareId();
		
		return queryBookOrder(bOrder, ActivityType.KMALL, loadTest, finalFromShareId, finalOrderId, token, userId, phone, ssoUserId, crmUserId, wxNickname, wxHeadImg, openId, unionId, reqHashCode, clientIP, clientChannel, req, thiz);
	}
	
	private Map<String, Object> queryBookOrder(Book1712Order bOrder, ActivityType activityType, boolean loadTest, String fromShareId, final String orderId, final String token, String userId, String phone, String ssoUserId, String crmUserId, String nickname, String headImg, 
									String openId, String unionId, final int reqHashCode, final String clientIP, final ClientChannel clientChannel, HttpServletRequest req, BaseBrandApiServlet bas) throws ServletException, IOException{
		JSONObject orderJo = null;
		if(loadTest) {
			orderJo = JSON.parseObject("{\"coupons\":[{\"activity\":\"中杯现磨咖啡兑换券\",\"couponCode\":\"TESTCODE1\",\"usedStatus\":false,\"userId\":\"utest\",\"validEndTime\":4071451549000,\"validStartTime\":1452149149000,\"validStatus\":true},{\"activity\":\"中杯现磨咖啡兑换券\",\"couponCode\":\"TESTCODE2\",\"usedStatus\":false,\"userId\":\"utest\",\"validEndTime\":4071451549000,\"validStartTime\":1452149149000,\"validStatus\":true},{\"activity\":\"葡式蛋挞1只买一赠一券\",\"couponCode\":\"TESTCODE3\",\"usedStatus\":false,\"userId\":\"utest\",\"validEndTime\":1564558237000,\"validStartTime\":1436167836000,\"validStatus\":true},{\"activity\":\"中杯现磨咖啡兑换券\",\"couponCode\":\"TESTCODE4\",\"usedStatus\":false,\"userId\":\"\",\"validEndTime\":4071451549000,\"validStartTime\":1452149149000,\"validStatus\":true},{\"activity\":\"中杯现磨咖啡兑换券\",\"couponCode\":\"TESTCODE5\",\"usedStatus\":false,\"userId\":\"\",\"validEndTime\":4071451549000,\"validStartTime\":1452149149000,\"validStatus\":true},{\"activity\":\"中杯现磨咖啡兑换券\",\"couponCode\":\"TESTCODE6\",\"usedStatus\":false,\"userId\":\"\",\"validEndTime\":4071451549000,\"validStartTime\":1452149149000,\"validStatus\":true}],\"deliveryTime\":1486546496252,\"grandTotal\":6.00,\"orderId\":\"o"+token+"\",\"orderMobile\":\"m"+phone+"\",\"orderStatus\":\"ORDER_COMPLETED\",\"orderStatusString\":\"ORDER_COMPLETED\",\"orderTime\":1486546448384,\"payType\":\"YUMPAY_ALIPAY\",\"payTypeString\":\"支付网关支付宝\",\"subTotal\":6}");
		}else {
			try {
				orderJo = EGiftCardApiServlet.queryOrderForStu(activityType, req, reqHashCode, orderId, token, ssoUserId, phone, clientIP, clientChannel);
			} catch (ApiException e) {
				Book1712Order updateOrder = new Book1712Order();
				updateOrder.setOrderId(orderId);
				updateOrder.setBrandUserId(userId);
				updateOrder.setOrder2(String.valueOf(e.getContext()));
				updateOrder.setUpdateTime(new Date());
				updateOrder.setIsValid(false);
				
				updateBookOrder(updateOrder);
				
				return null;
			}
		}
		
		final JSONObject order = orderJo;
		if(!orderId.equals(order.getString("orderId"))) throw new ApiException("Remote InfoSys Server Error", ErrCode.GENERAL_SERVER_ERROR_EGC).setContext("requested order id is ["+orderId+"], but got is ["+order.getString("orderId")+"], order returned["+order.toJSONString()+"]");
		
		final String orderStatus = order.getString("orderStatus");	
		Map<String, Object> resultOrder = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("order", resultOrder);
		
		req.setAttribute(RESULT, result);
		
		//check the order is invalid or not
		if(!isPaidStatus(orderStatus)){
			//List<Book1712Order> orders = getUserBookOrder(userId);
			//Book1712Order bOrder = orders==null||orders.size()==0?null:orders.get(0);
			if(bOrder != null){
				Book1712Order updateOrder = new Book1712Order();
				updateOrder.setOrderId(orderId);
				updateOrder.setBrandUserId(userId);
				updateOrder.setStatus2(orderStatus);
				updateOrder.setOrder2(order.toJSONString());
				updateOrder.setUpdateTime(new Date());
				
				long createTimeStamp = bOrder.getCreateTime().getTime();
				long nowTimeStamp = System.currentTimeMillis(); 
				if(createTimeStamp - nowTimeStamp > 30*60*1000){ 
					updateOrder.setIsValid(false);
					updateBookOrder(updateOrder);
					
//					return result;
					return null;
				}
			}
		}
		
		resultOrder.put("orderId", orderId);
		resultOrder.put("orderStatus", orderStatus);
		
//		{"orderId":"K7976518481","payType":"YUMPAY_WXPAY","payTypeString":"支付网关微信","orderMobile":"15385513051","orderStatus":"ORDER_COMPLETED","orderStatusString":"ORDER_COMPLETED","orderTime":1511775220979,"deliveryTime":1511775228087,"mainProducts":[{"activityId":"SMK20170910944_10000","prodductCategory":"会员特权","productName":"Y78-WOW双堡套餐","quantity":1,"unitPrice":6900.000,"unitPoints":null,"subProducts":null,"coupons":null,"productChannelType":"K-1"}],"grandTotal":6900.00,"subTotal":6900,"channelId":null,"userChannelAccount":null,"redeemPointTotal":0}
		for(String key: order.keySet()){
			if ("mainProducts".equals(key)){
				continue;
			} else if ("payType".equals(key)){
				String value = order.getString(key);
				if ("YUMPAY_ALIPAY".equals(value)){
					value = "1";
				} else if ("YUMPAY_WXPAY".equals(value)){
					value = "2";
				}
				resultOrder.put(key, value);
			} else {
				resultOrder.put(key, order.get(key));
			}
		}
		
		//query shipment address
		try{
			resultOrder.put("shipmentAddr", EGiftCardApiServlet.queryShipmentAddr(activityType, req, reqHashCode, orderId, phone, crmUserId, null, token, ssoUserId, clientIP, clientChannel));
		}catch(Exception e){
			log.error("request@{} {}: failed to query shipment address for order [{}} of user [{}][{}], ignored", reqHashCode, logTime(), orderId, ssoUserId, crmUserId, e);
		}
		
		boolean savedAlready = false;
		String userShareId = ShareUtil.generateShareId(userId, "book1712", orderId);
		if(StringUtil.isEmptyWithTrim(fromShareId)){
			fromShareId = userShareId;
		}else {
			Book1712Share userShare = getUserBookShare(fromShareId, userId);
			savedAlready = userShare!=null;
		}
		resultOrder.put("fromShareId", fromShareId);
		
		if (savedAlready) return result;
		
		//if not payed yet
		if(!isPaidStatus(orderStatus))
			return result;


		onOrderPaid(order, phone, fromShareId, userShareId,
					userId, ssoUserId, crmUserId,
					openId, unionId, nickname, headImg,
					token, reqHashCode, clientIP, clientChannel);
			
		return result;
	}

	void onOrderPaid(final JSONObject order, String phone, String fromShareId, String userShareId,
					String userId, String ssoUserId, String crmUserId,
					String openId, String unionId, String nickname, String headImg,
					final String token, final int reqHashCode, final String clientIP, final ClientChannel clientChannel) {
		final String orderId = order.getString("orderId");
		final String orderStatus = order.getString("orderStatus");	
		
		final Book1712Share bs = new Book1712Share();
		bs.setOrderId(orderId);
		bs.setBrandUserId(userId);
		bs.setSsoUserId(ssoUserId);
		bs.setCrmUserCode(crmUserId);
		bs.setHeadImg(headImg);
		bs.setOpenId(openId);
		bs.setNickname(nickname);
		bs.setPhone(phone);
		bs.setUnionId(unionId);
		bs.setCreateTime(new Date());
		if(StringUtil.isEmptyWithTrim(fromShareId)){
			bs.setShareId(fromShareId);
			bs.setSharer(true);
		}else {
			bs.setShareId(fromShareId);
			bs.setSharer(fromShareId.equals(userShareId));
		}
		
		boolean added = false;
		boolean savedBS = false;
		try{
			//save share and update order
			added = CacheUtil.add(CacheUtil.getDefaultCacheName(), "CAMP_BOOK_"+orderId+"_UPDATE_MUTEX", 1, 10);
			if (added || CacheUtil.isCacheDown(CacheUtil.getDefaultCacheName(), "CAMP_BOOK_"+orderId+"_UPDATE_MUTEX")){
				Book1712Order updateOrder = new Book1712Order();
				updateOrder.setOrderId(orderId);
				updateOrder.setBrandUserId(userId);
				updateOrder.setStatus2(orderStatus);
				updateOrder.setOrder2(order.toJSONString());
				updateOrder.setUpdateTime(new Date());
				
				saveBookShare(bs);
				savedBS = true;
				updateBookOrder(updateOrder);
			}
			
			//send wx msg to user
			String inviteLink = BOOK_INVITE_URL + fromShareId;
			String msgContent = null;
			int leftCount;
			if(bs.isSharer()){//"恭喜您加入拼团, 再邀请%1$s位小伙伴即可拼团成功！快去<a href='%2$s'>邀请好友</a>吧!",
				leftCount = BOOK_SINGLE_COUNT_MAX - 1;
				//msgContent = String.format(WX_MSGS[0], leftCount, inviteLink);
				//sendWXmsgToUser(msgContent, openId, token, reqHashCode, clientIP, clientChannel);
				log.warn("try to notify sharer {}, {}, {}", openId, bs.getOrderId(), bs.getNickname());
				sendSharerOrderCreationNotifyMsg(openId, token, reqHashCode, clientIP, clientChannel);
			}else {
				List<Book1712Share> bookShares = getBookShares(fromShareId);
				if(bookShares == null) bookShares = new ArrayList<Book1712Share>();
				boolean saved = false;
				for(Book1712Share bShare : bookShares){
					if(orderId.equals(bShare.getOrderId())){
						saved = true;
						break;
					}
				}
				if (!saved && savedBS){
					bookShares.add(bs);
					saved = true;
				}
				
				int size = bookShares==null?0:bookShares.size();
				if(saved){
					leftCount = BOOK_SINGLE_COUNT_MAX - size;
				}else {
					long currentCount = 0;
					if (CacheUtil.add(CacheUtil.getDefaultCacheName(), "BOOK1712_SHARE_COUNT_"+fromShareId, size+1, Integer.MAX_VALUE)){
						currentCount = size+1;
					} else {
						currentCount = CacheUtil.incr(CacheUtil.getDefaultCacheName(), "BOOK1712_SHARE_COUNT_"+fromShareId, 1);
					}
					leftCount = BOOK_SINGLE_COUNT_MAX - (int)currentCount; 
				}

				if(leftCount > 0){//"您的好友%1$s加入拼团，还差%2$s位小伙伴哦！快去<a href='%3$s'>邀请好友</a>吧!",
					msgContent = String.format(WX_MSGS[1], nickname, leftCount, inviteLink);
					for(Book1712Share bShare : bookShares){
						final String attenderOpenId = bShare.getOpenId();
						if(!orderId.equals(bShare.getOrderId())){
							asyncSendWXTextMsg(msgContent, attenderOpenId, token, reqHashCode, clientIP, clientChannel);
						}
					}
				}else {
					//msgContent = WX_MSGS[2];//"恭喜您拼团成功！"
					//List<String> openIdList = new ArrayList<String>();
					//if(!saved) openIdList.add(openId);
					//
					//for(Book1712Share bShare : bookShares){
					//	openIdList.add(bShare.getOpenId());
					//}
					//
					//for(String opid: openIdList){
					//	sendWXmsgToUser(msgContent, opid, token, reqHashCode, clientIP, clientChannel);
					//}
					String sharerName = null;
					for(Book1712Share bShare : bookShares){
						if (bShare.isSharer()) {
							sharerName = bShare.getNickname();
							break;
						}
					}

					for(final Book1712Share bShare : bookShares){
						if (bShare.isSharer()) {
							log.warn("try to notify sharer2 {}, {}, {}", bShare.getOpenId(), bShare.getOrderId(), bs.getNickname());
							sendGroupSuccessNotifyMsg(bShare.getOpenId(), bShare.getOrderId(), bs.getNickname(),
														token, reqHashCode, clientIP, clientChannel);
						} else if (bShare.getOrderId().equals(orderId)){//sharee
							log.warn("try to notify sharee {}, {}, {}", bShare.getOpenId(), bShare.getOrderId(), sharerName);
							sendGroupSuccessNotifyMsg(bShare.getOpenId(), bShare.getOrderId(), sharerName,
														token, reqHashCode, clientIP, clientChannel);
						}
					}
				}
			}
		}finally{
			if (added) {
				CacheUtil.expire(CacheUtil.getDefaultCacheName(), "CAMP_BOOK_"+orderId+"_UPDATE_MUTEX");
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	private Map<String, String> getUserWxInfo(final String userId, final String orderId) {
//		Map<String, String> userWxInfo = new HashMap<String, String>();
//		Object o = memcacheUtil.getCache("BOOK1712_USER_WX_INFO_"+orderId, Integer.MAX_VALUE, CacheOp.ADD, new DataGenerator(){
//			@Override
//			public Object generate() {
//				return bookService.getUserWxInfo(userId, orderId);
//			}
//		});
//		
//		if (o==null) return userWxInfo;
//		userWxInfo = (Map<String, String>) (
//									(o instanceof Map)
//									? o
//									: JSON.parseObject(String.valueOf(o), new TypeReference<Map<String, String>>(){}.getType())
//									);
//		
//		return userWxInfo;
//	}
	
	private static String getPayType(final int pay) {
		return pay==1?"YUMPAY_ALIPAY":"YUMPAY_WXPAY";
	}

	private static String getPayChannel(final int payFromChannel) throws ErrorCodeRuntimeException {
		String payChannel = null;
		switch (payFromChannel){
		case 1: payChannel = "APP"; break;
		case 2: payChannel = "WAP"; break;
		case 3: payChannel = "WEB"; break;
		case 4: payChannel = "WECHATMINI"; break;
		default: throw new ApiException("Bad Request", ErrCode.GENERAL_CLIENT_ERROR).setContext("invalid pay channel ["+payFromChannel+"]");
		}
		return payChannel;
	}
	
	private void checkCampaignPeriod(Date start, Date end) {
		Date now = new Date();
		if(now.before(start)) {
			throw new ApiException("Campaign not start", ErrCode.NOT_START).setContext("now ["+now+"], start ["+start+"]");
		}
		
		if(now.after(end)) {
			throw new ApiException("Campaign finished already", ErrCode.END_ALREADY).setContext("now ["+now+"], end ["+end+"]");
		}
	}
	
	private int bookSellCount() {
		Object o = super.getOrGenCacheableData("BOOK1712_SELL_COUNT", null, Integer.MAX_VALUE, new CacheableDataGenerator(){
			@Override
			public CacheableData generate() {
				return new CacheableData(bookService.getAllCompletedCount());
			}
		});
		
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) return 0;
		return Integer.parseInt(so);
	}

	private void checkUserCanAttend(final String shareId) {
		int sellCount = bookSellCount();
		if(sellCount >= BOOK_ALL_COUNT_MAX) {
			throw new ApiException("Bad request", ErrCode.END_ALREADY).setContext("Reach total count limit. Current count:["+sellCount+"]");
		}
		
		if(shareId != null){
			List<Book1712Share> bookShares = getBookShares(shareId);
			
			if(bookShares!=null && (bookShares.size() >= BOOK_SINGLE_COUNT_MAX)) {
				throw new ApiException("Bad request", ErrCode.REACH_LIMIT).setContext("Reach single limit. Current count:["+bookShares.size()+"]");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Book1712Share> getBookShares(final String shareId) {
		log.warn("begin to read cache [{}], kbs [{}]", "BOOK1712_SHARES_"+shareId, true);
		Object o = super.getOrGenCacheableData("BOOK1712_SHARES_"+shareId, null, Integer.MAX_VALUE, new CacheableDataGenerator(){
				@Override
				public CacheableData generate() {
					List<Book1712Share> shareList = bookService.getBookShares(shareId);
					if(shareList==null || shareList.size()==0) shareList = null;
					return new CacheableData(shareList);
				}
			});
		log.warn("end reading cache [{}], result is [{}]", "BOOK1712_SHARES_"+shareId, JSON.toJSONString(o));
		
		List<Book1712Share> result = null;
		
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) result = null;
		else if (o instanceof List) result = (List<Book1712Share>)o; 
		else result = JSON.parseObject(so, new TypeReference<List<Book1712Share>>(){}.getType());
		
		if (result==null || result.size()<BOOK_SINGLE_COUNT_MAX){
			//long now = System.currentTimeMillis();
			//Date last = new Date(0L);
			//for (Book1712Share s: result){
			//	if (s.getCreateTime().after(last)) last = s.getCreateTime();
			//}
			//if (now - last.getTime() < 3*60*1000L){//repeatedly to expire cache within minutes to avoid DB replication delay
				delayExpireBookShareCache("BOOK1712_SHARES_"+shareId, 100);
				delayExpireBookShareCache("BOOK1712_SHARE_"+shareId, 100);
			//}
		}
		return result;
	}
	
	private Book1712Share getUserBookShare(final String shareId, final String userId){
		Object o = super.getOrGenCacheableData("BOOK1712_SHARE_"+shareId+"_"+userId, null, Integer.MAX_VALUE, true,
											"BOOK1712_SHARE_"+shareId, new CacheableDataGenerator(){
				@Override
				public CacheableData generate() {
					return new CacheableData(bookService.getUserBookShare(shareId, userId));
				}
			});
		
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) return null;
		if (o instanceof Book1712Share) return (Book1712Share)o; 
		return JSON.parseObject(so, Book1712Share.class);
	}
	
	@SuppressWarnings("unchecked")
	private List<Book1712Order> getUserBookOrder(final String userId) {
		Object o =super.getOrGenCacheableData("BOOK1712_USER_ORDER_"+userId, null, Integer.MAX_VALUE, new CacheableDataGenerator(){
				@Override
				public CacheableData generate() {
					List<Book1712Order> orders = bookService.getUserBookOrder(userId);
					if (orders==null||orders.size()==0){
						return new CacheableData(null);
					} else {
						return new CacheableData(MULTI_GROUP?orders:orders.get(0));
					}
				}
			});
		
		String so = String.valueOf(o);
		if (o==null||"null".equals(so)||so.isEmpty()) return null;
		if (MULTI_GROUP){
			if (o instanceof List) return (List<Book1712Order>)o;
			return JSON.parseObject(so, new TypeReference<List<Book1712Order>>(){}.getType());
		} else {
			if (o instanceof Book1712Order) return Arrays.asList((Book1712Order)o); 
			return  Arrays.asList(JSON.parseObject(so, Book1712Order.class));
		}
	}
	



	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	void sendSharerOrderCreationNotifyMsg(final String openId,
											final String token, final int reqHashCode, final String clientIP, final ClientChannel clientChannel) {
		final Map<String, Object> msgNotify = new HashMap<String, Object>();
		msgNotify.put("touser", openId);
		msgNotify.put("template_id", "CddwSlVswx8sHjQRljVGQeWJzBbtc2frYDNcjwI0pR8");
		msgNotify.put("url", "https://login.kfc.com.cn/CRM/superapp_wechat/PaymentWechat/index_grouponBookWX.html");
		msgNotify.put("data", new HashMap(){{
			put("first", new HashMap(){{put("value","恭喜您完成支付，创建拼团成功啦，还差1人即可成功拼团，赶快邀请好友吧！");}});
			put("keyword1", new HashMap(){{put("value","《地图》拼团");}});
			put("keyword2", new HashMap(){{put("value","创建拼团成功，已支付");}});
			put("remark", new HashMap(){{put("value","请点击详情查看，谢谢！");}});
		}});
		asyncSendWXTemplateMsg(openId, msgNotify, token, reqHashCode, clientIP, clientChannel);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	void sendGroupSuccessNotifyMsg(final String openId, final String orderId, final String sharerName,
									final String token, final int reqHashCode, final String clientIP, final ClientChannel clientChannel) {
		final String sharername = StringUtil.isEmptyWithTrim(sharerName)?"TA":sharerName;
		Map<String, Object> msgNotify = new HashMap<String, Object>();
		msgNotify.put("touser", openId);
		msgNotify.put("template_id", "ThvaVOT0t8doATPZPmKRqRVOVEZ7U19GrHUg92cy7go");
		msgNotify.put("url", "https://login.kfc.com.cn/CRM/superapp_wechat/PaymentWechat/index_grouponBookWX.html");
		msgNotify.put("data", new HashMap(){{
			put("first", new HashMap(){{put("value","恭喜您与"+sharername+"拼团成功！请立即前往查看。");}});
			put("keyword1", new HashMap(){{put("value",orderId);}});//订单编号
			put("keyword2", new HashMap(){{put("value","小书迷王国地图1套（含4册）");}});//团购商品
			put("remark", new HashMap(){{put("value","感谢您的参与！");}});
		}});
		asyncSendWXTemplateMsg(openId, msgNotify, token, reqHashCode, clientIP, clientChannel);
	}
	
	private void saveBookShare(final Book1712Share share){
		if(RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.CREATE, share);
			RabbitMQHelper.publish(Book1712Share.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					saveBookShareAndUpdateCache(share, "1");
				}
			});
		} else {
			saveBookShareAndUpdateCache(share, "2");
		}
	}
	
	private void saveBookShareAndUpdateCache(final Book1712Share share, String flag) {
		Date now = new Date();
		Date ct = share.getCreateTime();
		share.setCreateTime(now);
		try{
			bookService.saveBookShare(share);//insert
			if (CacheUtil.get(CacheUtil.getDefaultCacheName(), "BOOK1712_SELL_COUNT")!=null){
				CacheUtil.incr(CacheUtil.getDefaultCacheName(), "BOOK1712_SELL_COUNT", 1);
			}
		}catch(DuplicateKeyException e){
			log.error("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(share), e.getMessage(), e);
		}
		log.warn("{} schedule to expire cache [{}], kbs[{}], create time from [{}] to [{}]", flag, "BOOK1712_SHARES_"+share.getShareId(), true, ct==null?null:ct.getTime(), now.getTime());
		delayExpireBookShareCache("BOOK1712_SHARES_"+share.getShareId());
		delayExpireBookShareCache("BOOK1712_SHARE_"+share.getShareId());
	}
	
	private static final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
	private static void delayExpireBookShareCache(final String key){
		delayExpireBookShareCache(key, DELAY_EXPIRE_CACHE_MS);
	}
	private static void delayExpireBookShareCache(final String key, long delayMS){
		expireBookSharesCache(key);
		
		ses.schedule(new Runnable(){
			@Override
			public void run() {
				expireBookSharesCache(key);
			}
		}, delayMS, TimeUnit.MILLISECONDS);
	}
	
	private static void expireBookSharesCache(final String key) {
		log.warn("begin to expire cache [{}], kbs[{}]", key, true);
		CacheUtil.expire(CacheUtil.getDefaultCacheName(), key);
		log.warn("done expire cache [{}], kbs[{}]", key, true);
	}

	private void updateBookOrder(final Book1712Order order){
		CacheUtil.expire(CacheUtil.getDefaultCacheName(), "BOOK1712_USER_ORDER_"+order.getBrandUserId());
		
		if(RabbitMQHelper.RABBIT_MQ_ENABLED){
			Message msg = new Message(Message.Type.UPDATE, order);
			RabbitMQHelper.publish(Book1712Order.class.getCanonicalName(), msg, RabbitMQHelper.DEFAULT_PUBLISH_TIMEOUT_MS, new FailPublishHandler(){
				@Override
				public void handleFailPublish(Throwable reason, Message msg) {
					bookService.updateBookOrder(order);
					CacheUtil.expire(CacheUtil.getDefaultCacheName(), "BOOK1712_USER_ORDER_"+order.getBrandUserId());
				}
			});
		} else {
			bookService.updateBookOrder(order);
			CacheUtil.expire(CacheUtil.getDefaultCacheName(), "BOOK1712_USER_ORDER_"+order.getBrandUserId());
		}
	}

	boolean isPaidStatus(String orderStatus) {
		return "ORDER_PAID_SUCCESS".equals(orderStatus) || "ORDER_COMPLETED".equals(orderStatus) || "ORDER_PROCESSING".equals(orderStatus);
	}
}
