package com.yum.kfc.brand.common.web.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.hp.jdf.ssm.rmq.BaseRabbitMQConsumer;
import com.hp.jdf.ssm.rmq.RabbitMQHelper;
import com.yum.kfc.brand.book1712.mqc.Book1712OrderMQConsumer;
import com.yum.kfc.brand.book1712.mqc.Book1712ShareMQConsumer;
import com.yum.kfc.brand.common.mqc.CspAzureRabbitMQConsumer;
import com.yum.kfc.brand.common.mqc.WXmsgMQConsumer;
import com.yum.kfc.brand.kbm.mqc.AiUserCouponMQConsumer;


/**
 * @author luolix
 *
 */
public class RabbitMQConsumerServletContextListener implements ServletContextListener{
	@SuppressWarnings("unused")
	private ServletContext servletContext;
	
	private List<BaseRabbitMQConsumer> rmqConsumers = new ArrayList<BaseRabbitMQConsumer>();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		servletContext = sce.getServletContext();
		
		if (RabbitMQHelper.RABBIT_MQ_ENABLED){
			@SuppressWarnings("rawtypes")
			List<Class> mqcs = Arrays.asList(new Class[]{
				CspAzureRabbitMQConsumer.class,
				WXmsgMQConsumer.class,
//				Dzp1711OrderMQConsumer.class,
//				Dzp1711DrawMQConsumer.class,
				Book1712OrderMQConsumer.class,
				Book1712ShareMQConsumer.class,
				AiUserCouponMQConsumer.class
			});
			for(@SuppressWarnings("rawtypes") Class clz: mqcs){
				BaseRabbitMQConsumer mqc;
				try {
					mqc = (BaseRabbitMQConsumer)clz.newInstance();
					mqc.launch();
					rmqConsumers.add(mqc);
				} catch (InstantiationException e) {
					throw new RuntimeException("failed to instance class ["+clz+"]", e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("failed to instance class ["+clz+"]", e);
				}
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		for(BaseRabbitMQConsumer mc: rmqConsumers){
			mc.shutdown();
		}
		
		RabbitMQHelper.shutdown();
	}

}
