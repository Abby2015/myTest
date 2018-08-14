package com.yum.kfc.brand.bnbjc1508.mqc;

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
import com.yum.kfc.brand.bnbjc1508.pojo.Bnbjc1508Share;
import com.yum.kfc.brand.bnbjc1508.service.Bnbjc1508Service;

/**
 * @author luolix
 *
 */
public class Bnbjc1508ShareMQConsumer extends BaseRabbitMQConsumer{

	private static final Logger log = LoggerFactory.getLogger(Bnbjc1508ShareMQConsumer.class);
	
	private static final String QUEUE_NAME = Bnbjc1508Share.class.getCanonicalName();
	
	private static final Bnbjc1508Service service = SpringUtil.getBean(Bnbjc1508Service.class);
	
	public Bnbjc1508ShareMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.bnbjc1508.share", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					Bnbjc1508Share share = JSON.parseObject(String.valueOf(msg.getPayload()), Bnbjc1508Share.class);
					service.saveShare(share);
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
