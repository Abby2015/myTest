package com.yum.kfc.brand.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsynJob implements Runnable {
   
	private static Logger logger = LoggerFactory.getLogger(AsynJob.class);

    private final Runnable  runable;



    public AsynJob(Runnable runable) {
        super();
        this.runable = runable;
    }

    @Override
    public void run() {
    	logger.info("asyn job start run ....");
        try {
            runable.run();
        } catch (Exception e) {
            logger.debug("asyn job sun error: [{}]", e.getMessage());
        }
        logger.info("asyn job has finished ....");
    }
}
