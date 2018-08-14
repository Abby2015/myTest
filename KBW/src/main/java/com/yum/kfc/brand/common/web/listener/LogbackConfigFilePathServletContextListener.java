package com.yum.kfc.brand.common.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.hp.jdf.ssm.util.LogbackUtil;

/**
 * this listener shall be the first listener!
 * 
 * @author DING Weimin (wei-min.ding@hpe.com) Sep 27, 2016 12:01:04 PM
 *
 */
public class LogbackConfigFilePathServletContextListener implements ServletContextListener{

	static {
		final String LOGBACK_CONFIG_FILE_PATH = "/opt/brandconfig/campaign/logback.xml";
		
		LogbackUtil.useConfigFile(LOGBACK_CONFIG_FILE_PATH);
	}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
