package com.yum.kfc.brand.dzp1807.mqc;

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
import com.yum.kfc.brand.dzp1807.pojo.Dzp1807Order;
import com.yum.kfc.brand.dzp1807.service.Dzp1807Service;

public class Dzp1807OrderMQConsumer extends BaseRabbitMQConsumer{
	private static final Logger log = LoggerFactory.getLogger(Dzp1807OrderMQConsumer.class);
	
	private static final String QUEUE_NAME = Dzp1807Order.class.getCanonicalName();
	
	private static final Dzp1807Service service = SpringUtil.getBean(Dzp1807Service.class);
	
	public Dzp1807OrderMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.dzp1807.order", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					Dzp1807Order order = JSON.parseObject(String.valueOf(msg.getPayload()), Dzp1807Order.class);
					service.saveOrder(order);
				} catch(DuplicateKeyException e){
					log.error("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
				} catch(Exception e){
					throw new ConsumeException(e);
				}
			}
		});
	}
}
