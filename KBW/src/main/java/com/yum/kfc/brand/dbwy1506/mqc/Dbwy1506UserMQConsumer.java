package com.yum.kfc.brand.dbwy1506.mqc;

import java.io.IOException;
import java.sql.DataTruncation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.yum.kfc.brand.dbwy1506.pojo.Dbwy1506User;
import com.yum.kfc.brand.dbwy1506.service.Dbwy1506Service;

/**
 * @author luolix
 *
 */
public class Dbwy1506UserMQConsumer extends BaseRabbitMQConsumer{

	private static final Logger log = LoggerFactory.getLogger(Dbwy1506UserMQConsumer.class);
	
	private static final String QUEUE_NAME = Dbwy1506User.class.getCanonicalName();
	
	private static final Dbwy1506Service service = SpringUtil.getBean(Dbwy1506Service.class);
	
	public Dbwy1506UserMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.dbwy1506.user", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					Dbwy1506User user = JSON.parseObject(String.valueOf(msg.getPayload()), Dbwy1506User.class);
					service.saveUser(user);
				} catch(DuplicateKeyException e){
					log.warn("duplicate key met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
				} catch (DataIntegrityViolationException e){
					if (e.getCause() instanceof DataTruncation) {
						log.warn("data truncation met, ignored, [{}], [{}]", JSON.toJSONString(msg), e.getMessage(), e);
					} else {
						throw new ConsumeException(e);
					}
				} catch(Exception e){
					throw new ConsumeException(e);
				}
			}
		});
	}
}
