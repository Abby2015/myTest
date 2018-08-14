package com.yum.kfc.brand.common.utils.spring;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.hp.jdf.ssm.util.StringUtil;

/**
 * @author DING Weimin (wei-min.ding@hpe.com) Jul 22, 2016 4:13:22 PM
 *
 */
public class SuperAppReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

	public SuperAppReloadableResourceBundleMessageSource() {
		super();
	}

	/**
	 * revised based on spring-context-4.0.6.RELEASE.jar: {@link org.springframework.context.support.ReloadableResourceBundleMessageSource#refreshProperties(String, PropertiesHolder)}<br>
	 *  change: no suffix append to filename: file:///opt/brandconfig/superapp.config
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
		
		if ((!filename.equals("file:///opt/brandconfig/campaign/camp.config")) && (!filename.equals("file:///opt/brandconfig/campaign/superapp.config"))) {
			return super.refreshProperties(filename, propHolder);
		}
		
		long cacheMillis = -1;
		ResourceLoader resourceLoader = null;
		Map<String, PropertiesHolder> cachedProperties = null;
		Field f;
		try {
			@SuppressWarnings("rawtypes")
			Class parentClz = ReloadableResourceBundleMessageSource.class;
			
			f = parentClz.getDeclaredField("cacheMillis");
			f.setAccessible(true);
			cacheMillis = (Long)f.get(this);
			
			f = parentClz.getDeclaredField("resourceLoader");
			f.setAccessible(true);
			resourceLoader = (ResourceLoader)f.get(this);
			
			f = parentClz.getDeclaredField("cachedProperties");
			f.setAccessible(true);
			cachedProperties = (Map<String, PropertiesHolder>)f.get(this);
		} catch (Exception e) {
			throw new RuntimeException("failed to get fields: "+ StringUtil.getMsgOrClzName(e), e);
		}
		
		long refreshTimestamp = (cacheMillis < 0 ? -1 : System.currentTimeMillis());

		//neither PROPERTIES_SUFFIX nor XML_SUFFIX appended,
		// this is the only difference compared to org.springframework.context.support.ReloadableResourceBundleMessageSource.refreshProperties(String, PropertiesHolder)
		//Resource resource = this.resourceLoader.getResource(filename + PROPERTIES_SUFFIX);
		//if (!resource.exists()) {
		//	resource = this.resourceLoader.getResource(filename + XML_SUFFIX);
		//}
		Resource resource = resourceLoader.getResource(filename);

		if (resource.exists()) {
			long fileTimestamp = -1;
			if (cacheMillis >= 0) {
				// Last-modified timestamp of file will just be read if caching with timeout.
				try {
					fileTimestamp = resource.lastModified();
					if (propHolder != null && propHolder.getFileTimestamp() == fileTimestamp) {
						if (logger.isDebugEnabled()) {
							logger.debug("Re-caching properties for filename [" + filename + "] - file hasn't been modified");
						}
						propHolder.setRefreshTimestamp(refreshTimestamp);
						return propHolder;
					}
				}
				catch (IOException ex) {
					// Probably a class path resource: cache it forever.
					if (logger.isDebugEnabled()) {
						logger.debug(
								resource + " could not be resolved in the file system - assuming that is hasn't changed", ex);
					}
					fileTimestamp = -1;
				}
			}
			try {
				Properties props = loadProperties(resource, filename);
				propHolder = new PropertiesHolder(props, fileTimestamp);
			}
			catch (IOException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Could not parse properties file [" + resource.getFilename() + "]", ex);
				}
				// Empty holder representing "not valid".
				propHolder = new PropertiesHolder();
			}
		}

		else {
			// Resource does not exist.
			if (logger.isDebugEnabled()) {
				logger.debug("No properties file found for [" + filename + "] - neither plain properties nor XML");
			}
			// Empty holder representing "not found".
			propHolder = new PropertiesHolder();
		}

		propHolder.setRefreshTimestamp(refreshTimestamp);
		cachedProperties.put(filename, propHolder);
		return propHolder;
	}

	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		String result = super.resolveCodeWithoutArguments(code, locale);
		return CryptPropertySourcesPlaceholderConfigurer.decrypt(result);
	}

	private Object lock = new Object();
	@SuppressWarnings("unchecked")
	@Override
	protected PropertiesHolder getMergedProperties(Locale locale) {
		PropertiesHolder ph = null;
		
		Map<Locale, PropertiesHolder> cachedMergedProperties = null;
		
		@SuppressWarnings("rawtypes")
		Class parentClz = ReloadableResourceBundleMessageSource.class;
		try {
			Field f = parentClz.getDeclaredField("cachedMergedProperties");
			f.setAccessible(true);
			cachedMergedProperties = (Map<Locale, PropertiesHolder>)f.get(this);
		} catch (Exception e) {
			throw new RuntimeException("failed to get fields: "+ StringUtil.getMsgOrClzName(e), e);
		}
		
		PropertiesHolder mergedHolder = cachedMergedProperties.get(locale);
		if (mergedHolder != null) {
			return mergedHolder;
		} else {
			ph = super.getMergedProperties(locale);
		}
		
		synchronized (lock) {
			Properties p = ph.getProperties();
			
			List<Properties> ips = new ArrayList<Properties>();
			
			Iterator<Entry<Object, Object>> i = p.entrySet().iterator();
			while (i.hasNext()){
				Entry<Object, Object> e = i.next();
				String v = (String)e.getValue();
				e.setValue(CryptPropertySourcesPlaceholderConfigurer.decrypt(v));
				List<Properties> ip = CryptPropertySourcesPlaceholderConfigurer.getIncludes((String)e.getKey(), (String)e.getValue());
				if (ip!=null) ips.addAll(ip);
			}
			
			for(Properties ip: ips){
				p.putAll(ip);
			}
		}
		
		return ph;
	}
	
}
