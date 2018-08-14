package com.yum.kfc.brand.common.utils.cache;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.danga.MemCached.MemCachedClient;

@Component
public class MemcacheUtil {
	private static final String CACHE_KEY_PREFIX = "KFC.CAMP.";
	
	@Autowired
	private MemCachedClient memcachedClient;
	
	public Object getCache(String key, int seconds, CacheOp copSave, DataGenerator dg){
		String fullKey = CACHE_KEY_PREFIX + key;
		Object o = memcachedClient.get(fullKey);
		if (o==null && dg!=null){
			o = dg.generate();
			if (o!=null){
				Date expiry = getExpireDate(seconds);
				switch (copSave){
				case ADD:
					memcachedClient.add(fullKey, String.valueOf(o), expiry);
					break;
				case SET:
					memcachedClient.set(fullKey,  String.valueOf(o), expiry);
					break;
				default:
					throw new RuntimeException(copSave+" not support in this method");
				}
			}
		}
		return o;
	}
	
	public int addOrIncrCache(String key, int value, int seconds){
		String fullKey = CACHE_KEY_PREFIX + key;
		Date expiry = getExpireDate(seconds);
		long result = value;
		if(!memcachedClient.add(fullKey, String.valueOf(value), expiry)){
			result = memcachedClient.incr(fullKey);
		}
		
		return (int) result;
	}
	
	public long incrCache(String key){
		String fullKey = CACHE_KEY_PREFIX + key;
		return memcachedClient.incr(fullKey);
	}
	
	public void expireCache(String key){
		String fullKey = CACHE_KEY_PREFIX + key;
		memcachedClient.delete(fullKey);
	}
	
	public boolean addOrSetKey(String key, String value, int seconds, CacheOp copSave){
		String fullKey = CACHE_KEY_PREFIX + key;
		Date expiry = getExpireDate(seconds);
		switch (copSave){
		case ADD:
			return memcachedClient.add(fullKey, value, expiry);
		case SET:
			return memcachedClient.set(fullKey,  value, expiry);
		default:
			throw new RuntimeException(copSave+" not support in this method");
		}
	}
	
	private Date getExpireDate(int seconds){
		long timeStamp = 0;
		if(seconds <= 7*24*60*60){
			timeStamp = Long.parseLong(""+(System.currentTimeMillis() + seconds*1000));
		}else {
			timeStamp = Long.parseLong(""+(System.currentTimeMillis() + 7*24*60*60*1000));
		}
		
		Date expiry = new Date(timeStamp);
		
		return expiry;
	}
}
