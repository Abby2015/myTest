package com.yum.kfc.brand.luh1604.mqc;

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
import com.yum.kfc.brand.luh1604.pojo.Luh1604Open;
import com.yum.kfc.brand.luh1604.service.Luh1604Service;

public class Luh1604OpenMQConsumer extends BaseRabbitMQConsumer{
	private static final Logger log = LoggerFactory.getLogger(Luh1604OpenMQConsumer.class);
	
	private static final String QUEUE_NAME = Luh1604Open.class.getCanonicalName();
	
	private static final Luh1604Service service = SpringUtil.getBean(Luh1604Service.class);
	
	public Luh1604OpenMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.szz1604.open", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					Luh1604Open open = JSON.parseObject(String.valueOf(msg.getPayload()), Luh1604Open.class);
					service.saveOpen(open);
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
