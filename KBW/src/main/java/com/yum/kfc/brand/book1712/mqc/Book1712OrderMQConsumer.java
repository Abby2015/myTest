package com.yum.kfc.brand.book1712.mqc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import com.alibaba.fastjson.JSON;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.cache.CacheUtil;
import com.hp.jdf.ssm.rmq.BaseRabbitMQConsumer;
import com.hp.jdf.ssm.rmq.ConsumeException;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.MessageConsumer;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.hp.jdf.ssm.util.SpringUtil;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.yum.kfc.brand.book1712.pojo.Book1712Order;
import com.yum.kfc.brand.book1712.service.Book1712Service;

public class Book1712OrderMQConsumer extends BaseRabbitMQConsumer{
	private static final Logger log = LoggerFactory.getLogger(Book1712OrderMQConsumer.class);
	
	private static final String QUEUE_NAME = Book1712Order.class.getCanonicalName();
	
	private static final Book1712Service service = SpringUtil.getBean(Book1712Service.class);
//	private static final MemcacheUtil memcacheUtil = SpringUtil.getBean(MemcacheUtil.class);
//	private static final boolean USE_KBS_CACHE = Boolean.parseBoolean(ApplicationConfig.getProperty("api.cacheutil.kbs", "false"));
	
	public Book1712OrderMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.book1712.order", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					switch(msg.getType()){
					case CREATE:
						Book1712Order order = JSON.parseObject(String.valueOf(msg.getPayload()), Book1712Order.class);
						service.saveBookOrder(order);//insert
						CacheUtil.expire(CacheUtil.getDefaultCacheName(), "BOOK1712_USER_ORDER_"+order.getBrandUserId());
						break;
					case UPDATE:
						Book1712Order uOrder = JSON.parseObject(String.valueOf(msg.getPayload()), Book1712Order.class);
						service.updateBookOrder(uOrder);
						CacheUtil.expire(CacheUtil.getDefaultCacheName(), "BOOK1712_USER_ORDER_"+uOrder.getBrandUserId());
						break;
					default:
						log.error("not support message type [{}] of msg [{}], discarded", msg.getType(), JSON.toJSONString(msg));
						break;
					}
				} catch(DuplicateKeyException e){
					log.error("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
				} catch(Exception e){
					throw new ConsumeException(e);
				}
			}
		});
	}
}
