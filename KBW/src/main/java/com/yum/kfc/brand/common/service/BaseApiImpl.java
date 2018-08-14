package com.yum.kfc.brand.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hp.jdf.ssm.util.SpringUtil;
import com.yum.kfc.brand.common.pojo.FeiRuiResult;
import com.yum.kfc.brand.common.utils.HttpClientUtil;

@Component
public class BaseApiImpl implements InitializingBean, ApplicationContextAware, DisposableBean {
	
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(BaseApiImpl.class);
	
	protected ThreadPoolTaskExecutor executor;
	protected ApplicationContext context;
		
	//用户渠道(0: Brand App; 1: 微信; 2:浏览器)
	public static final int CHANNEL_TYPE_APP = 0;
	public static final int CHANNEL_TYPE_WECHAT = 1;
	public static final int CHANNEL_TYPE_BROWSER = 2;
	
	//设备类型(0: android; 1:ios; 2:browser)
	public static final int DEVICE_TYPE_ANDRIOD	 = 0;
	public static final int DEVICE_TYPE_IOS	 = 1;
	public static final int DEVICE_TYPE_BROWSER	 = 2;
	
	public static List<Integer> CHANNEL_TYPE_LIST = new ArrayList<Integer>();
	public static List<String> MEDIA_TYPE_LIST = new ArrayList<String>();
	public static List<Integer> DEVICE_TYPE_LIST = new ArrayList<Integer>();
	public static List<Integer> CHOICE_LIST = new ArrayList<Integer>();
	public static List<Integer> SHARE_RESULT_LIST = new ArrayList<Integer>();
	public static List<Integer> MENU_TYPE_LIST = new ArrayList<Integer>();
	public static List<Integer> ASK_TYPE_LIST = new ArrayList<Integer>();
	
	static{
		//用户渠道(0: Brand App; 1: 微信; 2:浏览器)
		CHANNEL_TYPE_LIST.add(CHANNEL_TYPE_APP);
		CHANNEL_TYPE_LIST.add(CHANNEL_TYPE_WECHAT);
		CHANNEL_TYPE_LIST.add(CHANNEL_TYPE_BROWSER);
		//分享媒介类型
		MEDIA_TYPE_LIST.add("QQ");
		MEDIA_TYPE_LIST.add("WX");
		MEDIA_TYPE_LIST.add("TWB");
		MEDIA_TYPE_LIST.add("WB");
		MEDIA_TYPE_LIST.add("RR");
		MEDIA_TYPE_LIST.add("DB");
		MEDIA_TYPE_LIST.add("TB");
		MEDIA_TYPE_LIST.add("ZFB");
		//设备类型(0: android; 1:ios; 2:browser)
		DEVICE_TYPE_LIST.add(DEVICE_TYPE_ANDRIOD);
		DEVICE_TYPE_LIST.add(DEVICE_TYPE_IOS);
		DEVICE_TYPE_LIST.add(DEVICE_TYPE_BROWSER);
		//问题选择范围(1靠谱，0不靠谱，-1未选择)
		CHOICE_LIST.add(1);
		CHOICE_LIST.add(0);
		CHOICE_LIST.add(-1);
		//分享结果
		SHARE_RESULT_LIST.add(0);
		SHARE_RESULT_LIST.add(1);
		//菜单类型
		MENU_TYPE_LIST.add(0);
		MENU_TYPE_LIST.add(1);
		//求友类型
		ASK_TYPE_LIST.add(0);
		ASK_TYPE_LIST.add(1);
	}
	
	@Autowired
	public void setExecutor(ThreadPoolTaskExecutor executor) {
		this.executor = executor;
	}
	
	/**
	 * 异步执行 业务逻辑
	 */
	public void asynExecute(AsynJob eipJob) {
		this.executor.execute(eipJob);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext contex)
	   throws BeansException {
	  this.context=contex;
	}
	
	public ApplicationContext getApplicationContext(){
	  return context;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		doStart();
	}

	protected void doStart() throws Exception {
	}


	/**
	 * 获取资源消息
	 * @param msgKey
	 * @param arg
	 * @return
	 */
	public static String getMessage(String msgCode, Object[] arg) {
		return SpringUtil.getResource(msgCode, arg, Locale.CHINA);
	}

	public static String getMessage(String msgCode) {
		return getMessage(msgCode, null);
	}
	
	public static String encrypt(String data, String salt) {
		return DigestUtils.md5Hex(data + "{" + salt.toLowerCase() + "}");
	}

	public static String encryptBase64(String data) {
		return Base64.encodeBase64String(data.getBytes());
	}

	public static String decryptBase64(String data) {
		return new String(Base64.decodeBase64(data));
	}
	
	public static String getStringValue(String value){
		return StringUtils.isBlank(value) ? "" : value;
	}

	@Override
	public void destroy() throws Exception {
		doShutdown();
	}

	protected void doShutdown() {

	}
	
	
	
	public static String newUUID(){
		return String.format("%X%S", new Date().getTime(), UUID.randomUUID().toString().replace("-", ""));
	}
	
	public static boolean isCorrectPhone(String phone) {
		if(phone.length() == 11 && phone.substring(0, 1).equals("1") && isNumber(phone)){
			return true;
		}
		return false;
	}
	
	private static boolean isNumber(String number){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher matcher = pattern.matcher(number);
		if (matcher.matches()){
			return true;
		}
		return false;
	}
	
	
	public static String packHttpUrl(String httpUrl, Map<String, String> getParams, Map<String, String> postParams){
		Map<String, String> totalMap = new TreeMap<String, String>();
		totalMap.putAll(getParams);
		totalMap.putAll(postParams);
		StringBuffer sb = new StringBuffer();
		for (String key : totalMap.keySet()) {
			sb.append(totalMap.get(key));
	    }
		String authcode = DigestUtils.md5Hex(sb.toString());
		getParams.put("authcode", authcode);	
		getParams.remove("client_secret");
		StringBuffer url = new StringBuffer(httpUrl);
		int index = 0;
		String symbol = "";
		for(String key : getParams.keySet()) {
			symbol = index == 0 ? "?" : "&";
			url.append(symbol).append(key).append("=").append(getParams.get(key));
			index ++;
	    }
		return url.toString();
	}
	
	public static FeiRuiResult callFeiRuiService(String httpUrl, Map<String, String> postParams) throws Exception {
		String retValue = HttpClientUtil.sendPostRequestByJava(httpUrl, postParams);
		FeiRuiResult result = JSON.parseObject(retValue, FeiRuiResult.class);
		return result;
	} 
	
	public static void main(String[] args){
//		System.setProperty("http.proxyHost", "web-proxy.sgp.hp.com");  
//		System.setProperty("http.proxyPort", "8080");
		//callCardBagService();
		String sendMsgHttpUrl = "http://adapter.verystar.cn/externalapi.php";
		Map<String, String> getParams = new HashMap<String, String>();
		getParams.put("client_code", "p1yKgCbmN4suZcSpK51SaSPUzqLM6DiH");
		getParams.put("client_secret", "7YiHmexZOR643k9p5SbhwitfxZnVgBYP");
		//getParams.put("time", "1432993330");
		getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
		getParams.put("interface", "promoCenter");
		getParams.put("action", "getTypeSendRewardItem");
		//post参数
		Map<String, String> postParams = new HashMap<String, String>();
		//String myOpenId = "o1Z-rjmj_t7GnwiMWB_h8QBrUSF8";
		
		postParams.put("open_id", "o1Z-rjmj_t7GnwiMWB_h8QBrUSF8");
		//postParams.put("open_id", "o1Z-rjru4Cr12r5yGzi1OAt1-QgA");
		postParams.put("event_id", "335");
		postParams.put("item_id", "810");
		String httpUrl = packHttpUrl(sendMsgHttpUrl, getParams, postParams);
		System.out.println(httpUrl);
		String result = null;
		try {
//			result = RestClientUtil.callPostService(httpUrl, postParams, String.class);
			result = HttpClientUtil.sendPostRequestByJava(httpUrl, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result);
		
	}
}
