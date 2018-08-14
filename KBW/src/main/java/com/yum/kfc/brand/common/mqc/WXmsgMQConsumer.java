package com.yum.kfc.brand.common.mqc;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.api.ApiException;
import com.hp.jdf.ssm.rmq.BaseRabbitMQConsumer;
import com.hp.jdf.ssm.rmq.ConsumeException;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.MessageConsumer;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.StringUtil;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.yum.kfc.brand.api.WXFeiruiApiServlet;
import com.yum.kfc.brand.crm.pojo.ClientChannel;

public class WXmsgMQConsumer extends BaseRabbitMQConsumer{
	private static final Logger log = LoggerFactory.getLogger(WXmsgMQConsumer.class);
	
	private static final String QUEUE_NAME = "BRAND.KFC.CAMP.WX.MSG";
	
	public WXmsgMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.camp.wx.msg", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					switch(msg.getType()){
					case CREATE:
						Map<String, String> m = JSON.parseObject(String.valueOf(msg.getPayload()), new TypeReference<Map<String, String>>(){}.getType());
						String content = m.get("content");
						String targetOpenId = m.get("targetOpenId");
						String token = m.get("token");
						int reqHashCode = Integer.parseInt(m.get("reqFlag"));
						String clientIP = m.get("clientIP");
						String cc = m.get("clientChannel");
						ClientChannel clientChannel = StringUtil.isEmptyWithTrim(cc)?null:ClientChannel.valueOf(cc);
						
						//text message
						WXFeiruiApiServlet.sendMsg(targetOpenId, content, token, reqHashCode, clientIP, clientChannel);
						break;
					case UPDATE:
						JSONObject jo = JSON.parseObject(String.valueOf(msg.getPayload()));
						String snm = jo.getString("notifyMsg");
						Map<String, Object> msgNotify = JSON.parseObject(snm, new TypeReference<Map<String, Object>>(){}.getType());
						String scc = jo.getString("clientChannel");
						clientChannel = StringUtil.isEmptyWithTrim(scc)?null:ClientChannel.valueOf(scc);
						
						//template message
						WXFeiruiApiServlet.sendMsg(jo.getString("openId"), msgNotify, jo.getString("token"), jo.getIntValue("reqFlag"), jo.getString("clientIP"), clientChannel);
						break;
					default:
						throw new ApiException("not supported message type, msg is [{}]", JSON.toJSONString(msg));
					}
				} catch(Exception e){
					log.error("failed to send wx msg, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					throw new ConsumeException(e);
				}
			}
		});
	}
}
