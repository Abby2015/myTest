package com.yum.kfc.brand.book1712.mqc;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.yum.kfc.brand.book1712.pojo.Book1712Share;
import com.yum.kfc.brand.book1712.service.Book1712Service;

public class Book1712ShareMQConsumer extends BaseRabbitMQConsumer{
	private static final Logger log = LoggerFactory.getLogger(Book1712ShareMQConsumer.class);
	
	private static final String QUEUE_NAME = Book1712Share.class.getCanonicalName();
	
	private static final Book1712Service service = SpringUtil.getBean(Book1712Service.class);
//	private static final MemcacheUtil memcacheUtil = SpringUtil.getBean(MemcacheUtil.class);
//	private static final boolean USE_KBS_CACHE = Boolean.parseBoolean(ApplicationConfig.getProperty("api.cacheutil.kbs", "false"));
	private static final int DELAY_EXPIRE_CACHE_MS = Integer.parseInt(ApplicationConfig.getProperty("api.camp.book.share.cache.delay.ms", "500"));
	
	public Book1712ShareMQConsumer(){
		CONSUMERS = Integer.parseInt(ApplicationConfig.getProperty("rmq.queue.consumers.book1712.share", "1"));
	}
	
	@Override
	protected void consume() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		RabbitMQHelper.consume(QUEUE_NAME, new MessageConsumer(){
			@Override
			public void consume(Message msg) throws ConsumeException{
				try{
					switch(msg.getType()){
					case CREATE:
						Book1712Share share = JSON.parseObject(String.valueOf(msg.getPayload()), Book1712Share.class);
						Date now = new Date();
						Date ct = share.getCreateTime();
						share.setCreateTime(now);
						service.saveBookShare(share);
						
						log.warn("3 schedule to expire cache [{}], kbs[{}], create time from [{}] to [{}]", "BOOK1712_SHARES_"+share.getShareId(), true, ct==null?null:ct.getTime(), now.getTime());
						delayExpireBookShareCache("BOOK1712_SHARES_"+share.getShareId());
						delayExpireBookShareCache("BOOK1712_SHARE_"+share.getShareId());
						
						if (CacheUtil.get(CacheUtil.getDefaultCacheName(), "BOOK1712_SELL_COUNT")!=null){
							CacheUtil.incr(CacheUtil.getDefaultCacheName(), "BOOK1712_SELL_COUNT", 1);
						}
						
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

	private static final ScheduledExecutorService ses = Executors.newScheduledThreadPool(8);
	private static void delayExpireBookShareCache(final String key){
		expireBookSharesCache(key);
		
		ses.schedule(new Runnable(){
			@Override
			public void run() {
				expireBookSharesCache(key);
			}
		}, DELAY_EXPIRE_CACHE_MS, TimeUnit.MILLISECONDS);
	}

	private static void expireBookSharesCache(final String key) {
		log.warn("3 begin to expire cache [{}], kbs[{}]", key, true);
		CacheUtil.expire(CacheUtil.getDefaultCacheName(), key);
		log.warn("3 done expire cache [{}], kbs[{}]", key, true);
	}
}
