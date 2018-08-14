package com.yum.kfc.brand.kbm.mqc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import com.alibaba.fastjson.JSON;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.rmq.BaseRabbitMQConsumer;
import com.hp.jdf.ssm.rmq.ConsumeException;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.MessageConsumer;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.SpringUtil;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.yum.kfc.brand.ai.pojo.AiUserCoupon;
import com.yum.kfc.brand.ai.service.AiService;

public class AiUserCouponMQConsumer extends BaseRabbitMQConsumer{
	private static final Logger log = LoggerFactory.getLogger(AiUserCouponMQConsumer.class);
	
	private static final String QUEUE_NAME = AiUserCoupon.class.getCanonicalName();
	
	private static final AiService aiService = SpringUtil.getBean(AiService.class);
	
	public AiUserCouponMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.ai.userCoupon", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					AiUserCoupon coupon = JSON.parseObject(String.valueOf(msg.getPayload()), AiUserCoupon.class);
					aiService.saveUserCoupon(coupon);
				} catch(DuplicateKeyException e){
					log.error("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
				} catch(Exception e){
					throw new ConsumeException(e);
				}
			}
		});
	}
}
