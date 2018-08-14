package com.yum.kfc.brand.camp1504.mqc;

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
import com.yum.kfc.brand.camp1504.pojo.FooldayOpen;
import com.yum.kfc.brand.camp1504.service.FooldayService;

/**
 * @author luolix
 *
 */
public class FooldayOpenRabbitMQConsumer extends BaseRabbitMQConsumer{

	private static final Logger log = LoggerFactory.getLogger(FooldayOpenRabbitMQConsumer.class);
	
	private static final String QUEUE_NAME = FooldayOpen.class.getCanonicalName();
	
	private static final FooldayService fooldayService = SpringUtil.getBean(FooldayService.class);
	
	public FooldayOpenRabbitMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.camp1504.open", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					FooldayOpen fooldayOpen = JSON.parseObject(String.valueOf(msg.getPayload()), FooldayOpen.class);
					fooldayService.saveOpen(fooldayOpen);
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
