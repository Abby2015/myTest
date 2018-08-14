package com.yum.kfc.brand.bmh1506.api.impl;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yum.kfc.brand.bmh1506.pojo.Bmh1506Draw;
import com.yum.kfc.brand.common.pojo.FeiRuiResult;
import com.yum.kfc.brand.common.service.BaseApiImpl;

@Component
public class Bmh1506TaskJob {
	
	private static Logger logger = LoggerFactory.getLogger(Bmh1506TaskJob.class);
	
	@Value("${wechat.client_code}")				private String client_code;
	@Value("${wechat.client_secret}")			private String client_secret;
	@Value("${wechat.action.http.url}")			private String actionHttpUrl;
	@Value("${wechat.sendMsg.interface}")		private String sendMsgInterface;
	@Value("${wechat.sendMsg.action}")			private String sendMsgAction;
	@Value("${wechat.sendMsg.tplData}")			private String ptlData;
	
	public void callSendMsgService(List<Bmh1506Draw> drawList){
		for(Bmh1506Draw draw : drawList){
			Map<String, String> getParams = new HashMap<String, String>();
			getParams.put("client_code", client_code);
			getParams.put("client_secret", client_secret);
			getParams.put("time", Long.toString(System.currentTimeMillis()/1000));
			getParams.put("interface", sendMsgInterface);
			getParams.put("action", sendMsgAction);
			//post参数
			Map<String, String> postParams = new HashMap<String, String>();
			String tpl_data = ptlData.replace("{0}", draw.getUserId()).replace("{1}", draw.getUserId());
			postParams.put("tpl_data", tpl_data);
			String httpUrl = BaseApiImpl.packHttpUrl(actionHttpUrl, getParams, postParams);
			logger.info("\n===={} http url: {}", sendMsgAction, httpUrl);
			postParams = new HashMap<String, String>();
			FeiRuiResult feiRuiResult = new FeiRuiResult();
			try {
				tpl_data=URLEncoder.encode(tpl_data,"utf-8");
				postParams.put("tpl_data", tpl_data);
				logger.info("\n===={} post params: {}", sendMsgAction, JSON.toJSONString(postParams));
				feiRuiResult = BaseApiImpl.callFeiRuiService(httpUrl, postParams);
				logger.info("\n===={} return result: {}", sendMsgAction, JSON.toJSONString(feiRuiResult));
			}catch (Exception e) {
				logger.error("call tpl_data failure, error describe: []", e.getMessage(),  e);
			}
		}
	}

}
