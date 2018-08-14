package com.yum.kfc.brand.common.utils.spring;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.StringValueResolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.jdf.ssm.ErrorCodeRuntimeException;
import com.hp.jdf.ssm.svc.ServiceException;
import com.hp.jdf.ssm.util.StringUtil;

/**
 * 
 * &lt;!--bean class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer"--><br>
 * &lt;bean class="com.yum.kfc.brand.util.spring.CryptPropertySourcesPlaceholderConfigurer"><br>
 * 
 * &#64;{encryptedText}
 * 
 * @author DING Weimin (wei-min.ding@hpe.com) Jul 18, 2016 3:02:16 PM
 *
 */
public class CryptPropertySourcesPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {
	
	private static final Logger log = LoggerFactory.getLogger(CryptPropertySourcesPlaceholderConfigurer.class);
	
	private static final String DEFAULT_KEY = "jason977";
	private static final String INCLUDE_KEY = "include";

	public CryptPropertySourcesPlaceholderConfigurer() {
		super();
	}

	@Override
	protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess, StringValueResolver valueResolver) {
		super.doProcessProperties(beanFactoryToProcess, new CryptStringValueResolver(valueResolver));
	}
	
	private Object lock = new Object();	
	@Override
	protected Properties mergeProperties() throws IOException {
		Properties p = super.mergeProperties();
		
		synchronized (lock) {
			List<Properties> ips = new ArrayList<Properties>();
			
			Iterator<Entry<Object, Object>> i = p.entrySet().iterator();
			while (i.hasNext()){
				Entry<Object, Object> e = i.next();
				String v = (String)e.getValue();
				e.setValue(CryptPropertySourcesPlaceholderConfigurer.decrypt(v));
				List<Properties> ip = getIncludes((String)e.getKey(), (String)e.getValue());
				if (ip!=null) ips.addAll(ip);
			}
			
			for(Properties ip: ips){
				p.putAll(ip);
			}
		}
		
		return p;
	}



	static class CryptStringValueResolver implements StringValueResolver{

		StringValueResolver delegate;
		
		CryptStringValueResolver(StringValueResolver r){
			delegate = r;
		}
		
		@Override
		public String resolveStringValue(String strVal) {
			String v = delegate.resolveStringValue(strVal);
			v = decrypt(v);
			return v;
		}
	}

	static String decrypt(String v) throws ErrorCodeRuntimeException {
		if (v!=null){
			int b = v.indexOf("@{");
			if (b<0) return v;
			int cb = 0;
			StringBuilder sb = new StringBuilder(v.length());
			int e = -1;
			while(b>=0){
				sb.append(v.substring(cb, b));
				e = v.indexOf("}", b+2);
				if (e<0) {
					e = v.length();
					sb.append(v.substring(b));
					break;
				}
				cb = e+1;
				String et = v.substring(b+2, e);
				String dt;
				try {
					dt = StringUtil.desDec(Hex.decodeHex(et.toCharArray()), DEFAULT_KEY);
				} catch (DecoderException e1) {
					throw new ServiceException("bad configuration", e1).setContext(StringUtil.getMsgOrClzName(e1));
				}
				
				sb.append(dt);
				
				b=v.indexOf("@{", e+1);
			}
			if (e<v.length()-1) sb.append(v.substring(e+1));
			v = sb.toString();
		}
		return v;
	}
	
	/**
	 * support absolute properties file path only<br>
	 * support only one level of include<br>
	 * in future, MAY support relative path, class path, etc
	 * 
	 * @param properties
	 * @param key
	 * @param value
	 */
	static List<Properties> getIncludes(String key, String value){
		if (!INCLUDE_KEY.equals(key)) return null;
		if (value==null||(value=value.trim()).isEmpty()||!value.startsWith("[")) return null;
		
		JSONArray cs = JSON.parseArray(value);
		List<Properties> ps = new ArrayList<Properties>(cs.size());
		for(Object c: cs){
			String path = (String)c;
			if (path==null||(path=path.trim()).isEmpty()) continue;
			
			Properties p = new Properties();
			FileInputStream fis = null;
			try{
				//TODO support relative path, class path etc, now, only absolute path supported
				fis = new FileInputStream(path);
				p.load(fis);
				Iterator<Entry<Object, Object>> i = p.entrySet().iterator();
				while (i.hasNext()){
					Entry<Object, Object> e = i.next();
					String v = (String)e.getValue();
					e.setValue(CryptPropertySourcesPlaceholderConfigurer.decrypt(v));
				}
				ps.add(p);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("incorrect included properties file ["+path+"]", e);
			} catch (IOException e) {
				throw new RuntimeException("failed to load included properties file ["+path+"]", e);
			}finally{
				if (fis!=null){
					try{
						fis.close();
					}catch(Exception e){
						log.warn("failed to close file input stream ["+path+"]");
					}
				}
			}
		}
		
		return ps; 
	}
	
}
