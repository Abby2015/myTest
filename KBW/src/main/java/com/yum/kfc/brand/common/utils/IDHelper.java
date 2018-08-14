package com.yum.kfc.brand.common.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.jdf.ssm.ApplicationConfig;
import com.hp.jdf.ssm.util.DateUtil;
import com.hp.jdf.ssm.util.NetUtil;
import com.hp.jdf.ssm.util.StringUtil;


/**
 * @author DING Weimin (wei-min.ding@hpe.com) Apr 27, 2017 10:26:16 AM
 *
 */
public class IDHelper {
	
	private static final Logger log = LoggerFactory.getLogger(IDHelper.class);

	//I/l similar to 1, O similar to 0, Z similar to 2, so exclude them 
	private static final char[] AZ = new char[]{'0','1','2','3','4','5','6','7','8','9',
												'A','B','C','D','E','F','G','H',/*'I',*/'J',
												'K',/*'L',*/'M','N',/*'O',*/'P','Q','R','S','T',
												'U','V','W','X','Y',/*'Z'*/};
	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	private static final List azList = new ArrayList(){{for(char c: AZ) add(c);}};
	
	private static final ConcurrentHashMap<Long, AtomicInteger> seq = new ConcurrentHashMap<Long, AtomicInteger>(1024, 0.75f, 32);
	private static final ConcurrentHashMap<String, Long> ids = new ConcurrentHashMap<String, Long>(1024, 0.75f, 32);
	
	private static volatile long lastCleanTime = 0L;
	private static volatile boolean cleaning = false;
	
	private static final Long MAX_TIME_DIFF_MS = Long.parseLong(ApplicationConfig.getProperty("api.id.timediff.max","60000"));
	//private static final Long MAX_TIME_DIFF_MS = Long.parseLong("60000");
	
	private static final ThreadLocal<SimpleDateFormat> mmssSSSfmt = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("mmssSSS");
		}
	};
	
	private static final String LOCAL_IP = NetUtil.getLocalIP();
	
	private static final char[] IPIP;
	
	static {
		String[] ip = LOCAL_IP.split("\\.");
		IPIP = String.format("%02X%02X", Integer.parseInt(ip[2]), Integer.parseInt(ip[3])).toCharArray();
		
		new Timer("cleanOrderIdGeneration", true).scheduleAtFixedRate(new TimerTask(){public void run(){clean();}}, MAX_TIME_DIFF_MS/2, MAX_TIME_DIFF_MS/2);
	}
	
	public static String newOrderID(char campaign){
		return Character.toUpperCase(campaign)+newID15();
	}
	
	public static String newOrderID(char campaign, String userFlag){
		return Character.toUpperCase(campaign)+newID15()+userFlag.toUpperCase();
	}
	
	public static String newID15(){
		Date now = new Date();
		return newID15(now, now.getTime());
	}
	
	@SuppressWarnings("deprecation")
	private static String newID15(Date timestamp, long originalTS){
		//if(Math.abs(System.currentTimeMillis()-originalTS)>1000) throw new ServiceException("Internal Server Error", ""+ErrCode.GENERAL_SERVER_ERROR).setContext("too long time trying");
		
		if(System.currentTimeMillis()-lastCleanTime>MAX_TIME_DIFF_MS/2){
			if(!cleaning) clean();
		}
		
		char[] ID = new char[15]; //YMDHTTTTTIPIPXX
		
		ID[0] = AZ[(timestamp.getYear()+1900-2017+1)];//Y
		ID[1] = AZ[timestamp.getMonth()+1];//M
		ID[2] = AZ[timestamp.getDate()];//D
		ID[3] = AZ[timestamp.getHours()];//H
		
		String mszzz = mmssSSSfmt.get().format(timestamp);
		char[] TTTTT = base32hex_5(Integer.parseInt(mszzz));
		for(int i=0;i<5;i++) ID[4+i]=TTTTT[i];
		
		for(int i=0;i<4;i++) ID[9+i]=IPIP[i];
		
		AtomicInteger v = seq.putIfAbsent(timestamp.getTime(), new AtomicInteger(0));
		if (v==null){
			ID[13]=ID[14]='0';
		} else {
			int i = v.incrementAndGet();
			if (i<1024){
				char[] XX = base32hex_2(i);
				ID[13]=XX[0];ID[14]=XX[1];
			} else {
				return newID15(new Date(timestamp.getTime()+1), originalTS);
			}
		}
		
		String id = new String(ID);
		Long d = ids.putIfAbsent(id, timestamp.getTime());
		if (d==null) return id;
		else {
			log.warn("{} :used ID met {}\t{}", Thread.currentThread().getName(), id, d);
			return newID15(new Date(timestamp.getTime()+1), originalTS);
		}
	}
	
	private static char[] base32hex_5(int base10) {
		char[] c = {'0','0','0','0','0'};
		return base32hex(base10, c, 5);
	}
	
	private static char[] base32hex_2(int base10) {
		char[] c = {'0','0'};
		return base32hex(base10, c, 2);
	}

	private static char[] base32hex(int base10, char[] container, int length) {
		int a=31;
		int i=0;
		while(true){
			container[length-(++i)]=AZ[base10 & a];
			base10 >>= 5;
			if (base10==0) break;
		}
		return container;
	}
	
	@SuppressWarnings("deprecation")
	public static Map<String, Object> humanReadOrderId(String orderId){
		if (StringUtil.isEmptyWithTrim(orderId)) return null;
		if (orderId.length()<16) throw new IllegalArgumentException("invalid id");

		Map<String, Object> id = new HashMap<String, Object>();
		
		id.put("CAMPAIGN", orderId.charAt(0));
		
		Date d = new Date(azList.indexOf(orderId.charAt(1))-1+2017-1900,
						 azList.indexOf(orderId.charAt(2))-1,
						 azList.indexOf(orderId.charAt(3)),
						 azList.indexOf(orderId.charAt(4)),
						 0);
		String mmsszzz = ""+fromBase32hex(orderId.substring(5, 10));
		for(int i=0;i<7-mmsszzz.length();i++) mmsszzz="0"+mmsszzz;
		d = new Date(d.getTime()+Integer.parseInt(mmsszzz.substring(0,2))*60*1000
								+Integer.parseInt(mmsszzz.substring(2,4))*1000
								+Integer.parseInt(mmsszzz.substring(4))
								);
		id.put("TIME", d);
		id.put("TIME_HUMAN", DateUtil.formatDate(d, "yyyy-MM-dd HH:mm:ss.SSS"));

		id.put("IP", Integer.parseInt(orderId.substring(10, 12),16)+"."+Integer.parseInt(orderId.substring(12, 14),16));

		id.put("SEQ", fromBase32hex(orderId.substring(14, 16)));
		
		if(orderId.length()>16){
			id.put("USER_FLAG", orderId.substring(16));
		}
		
		return id;
	}
	
	public static int fromBase32hex(String base32hex){
		if (base32hex==null||(base32hex=base32hex.trim()).isEmpty()) return 0;
		int base10=0;
		for(char c: base32hex.toCharArray()){
			base10 <<= 5;
			base10 |= azList.indexOf(c);
		}
		return base10;
	}

	private static void clean(){
		if (cleaning) {
			return;
		}
		
		cleaning=true;
		lastCleanTime = System.currentTimeMillis();
		
		try{
			int s1=0, s2=0;
			long begin=0;
			if (log.isTraceEnabled()){
				begin = System.currentTimeMillis();
				s1=seq.size();
				s2=ids.size();
			}
			 
			int d1=0, d2=0;
			long now = System.currentTimeMillis();
			Iterator<Entry<Long, AtomicInteger>>  it = seq.entrySet().iterator();
			while(it.hasNext()){
				Entry<Long, AtomicInteger> e = it.next();
				if(now-e.getKey()>MAX_TIME_DIFF_MS){
					it.remove();
					d1++;
				}
			}
	
			Iterator<Entry<String, Long>>  it2 = ids.entrySet().iterator();
			while(it2.hasNext()){
				Entry<String, Long> e = it2.next();
				if(now-e.getValue()>MAX_TIME_DIFF_MS){
					it2.remove();
					d2++;
				}
			}
			
			if (log.isTraceEnabled()) {
				int s12 = seq.size();
				int s22 = ids.size();
				log.trace("done clean order id, using {}ms, IDs: {}/{}/-{} {}/{}/-{}", (System.currentTimeMillis()-begin), s1, s12, d1, s2, s22, d2);
			}
		}catch(Exception e){
			log.error("failed when clean order id, {}", StringUtil.getMsgOrClzName(e, true));
			if (log.isTraceEnabled()) log.trace("failed when clean order id, {}", StringUtil.getMsgOrClzName(e, true), e);
		}finally{
			cleaning=false;
		}
	}

}
