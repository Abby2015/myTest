package com.yum.kfc.brand.luh1604.mqc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.DataTruncation;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.rmq.BaseRabbitMQConsumer;
import com.hp.jdf.ssm.rmq.ConsumeException;
import com.hp.jdf.ssm.rmq.Message;
import com.hp.jdf.ssm.rmq.MessageConsumer;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.yum.kfc.brand.luh1604.api.impl.Luh1604ApiImpl;

public class DeleteLuhPicMQConsumer extends BaseRabbitMQConsumer{
private static final Logger log = LoggerFactory.getLogger(AzureRabbitMQConsumer.class);
	
	private static final String QUEUE_NAME = "DeleteLuhPic";
	
	private static final Type TYPE_MAP_STR_STR = new TypeReference<Map<String, String>>(){}.getType();
	
	public DeleteLuhPicMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.luhPic.delete", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					Map<String, String> params = JSON.parseObject(String.valueOf(msg.getPayload()), TYPE_MAP_STR_STR);
					String fileAbsolutePath = params.get("photoFilePath");
					
					Luh1604ApiImpl.deleteFileByTime(fileAbsolutePath);
				
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
