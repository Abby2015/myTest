package com.yum.kfc.brand.common.utils;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.MemCachedClient;

/**
 * 
 * @author DING Weimin (wei-min.ding@hpe.com) Aug 7, 2017 12:56:11 PM
 *
 */
public class MemcacheClientErrorHandler implements ErrorHandler {
	
	private static final Logger log = LoggerFactory.getLogger(MemcacheClientErrorHandler.class);

	@Override
	public void handleErrorOnInit(MemCachedClient client, Throwable error) {
		log.warn("init::"+client+"::"+error);
	}

	@Override
	public void handleErrorOnGet(MemCachedClient client, Throwable error, String cacheKey) {
		log.warn("get::"+cacheKey+"::"+client+"::"+error);
	}

	@Override
	public void handleErrorOnGet(MemCachedClient client, Throwable error, String[] cacheKeys) {
		log.warn("get::"+Arrays.asList(cacheKeys)+"::"+client+"::"+error);
	}

	@Override
	public void handleErrorOnSet(MemCachedClient client, Throwable error, String cacheKey) {
		log.warn("set::"+cacheKey+"::"+client+"::"+error);
	}

	@Override
	public void handleErrorOnDelete(MemCachedClient client, Throwable error, String cacheKey) {
		log.warn("delete::"+cacheKey+"::"+client+"::"+error);
	}

	@Override
	public void handleErrorOnFlush(MemCachedClient client, Throwable error) {
		log.warn("flush::"+client+"::"+error);
	}

	@Override
	public void handleErrorOnStats(MemCachedClient client, Throwable error) {
		log.warn("stats::"+client+"::"+error);
	}

}
