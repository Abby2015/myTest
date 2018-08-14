package com.yum.kfc.brand.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 
 * @author luolix
 */
public final class DateUtil {

	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final String YEAR = "year";
	public static final String MONTH = "month";
	public static final String DATE = "date";
	public static final String HOUR = "hour";
	public static final String MINUTE = "minute";
	public static final String SECOND = "second";
	

	public static String getCurrentTime() {
		return format.format(new Date(System.currentTimeMillis()));
	}
	
	public static String getCurrentDate() {
		return dayFormat.format(new Date(System.currentTimeMillis()));
	}

	public static String formatDate(Date date) {
		return format.format(date);
	}
	
	public static String formatDate(Date date, String format){
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	public static Date parseDate(String strDate) {
		try {
			return format.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date parseDate(DateFormat format, String strDate) {
		try {
			return format.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 得到UTC时间
	 * @return
	 */
	public static Date getCurrentUTCDate(Date date) {
		// 1、取得本地时间：
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 2、取得时间偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 3、取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, (-1)*(zoneOffset + dstOffset));
		return cal.getTime();
	}
	
	
	/**
	 * 得到UTC时间
	 * @param dateStr
	 * @return
	 */
	public static Date stringToUTCDate(String dateStr, int timezoneOffset){
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateToUTCDate(date, timezoneOffset);
	}
	
	/**
	 * 得到UTC时间
	 * @return
	 */
	public static Date dateToUTCDate(Date date, int timezoneOffset) {
		// 1、取得本地时间：
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 2、取得时间偏移量：
//		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 3、取得夏令时差：
//		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		//cal.add(java.util.Calendar.MILLISECOND, (-1)*(zoneOffset + dstOffset));
		cal.add(Calendar.MILLISECOND, -1*(timezoneOffset*3600*1000));
		return cal.getTime();
	}
	
	
	public static Date UTCDateToDate(Date date, int timezoneOffset){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(date);
		cal.add(Calendar.MILLISECOND, 1*(timezoneOffset*3600*1000));
		return cal.getTime();
	}

	public static String UTCDateToString(Date date, int timezoneOffset){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(date);
		cal.add(Calendar.MILLISECOND, 1*(timezoneOffset*3600*1000));
		return format.format(cal.getTime());
	}
	
	
	public static String getNewDate(String dateTimeStr, String DateType,int calTemp){
		try{
			Date date = DateUtil.parseDate(dateTimeStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			if(DateType.equalsIgnoreCase(YEAR)){
				calendar.add(Calendar.YEAR, calTemp);
			}else if(DateType.equalsIgnoreCase(MONTH)){
				calendar.add(Calendar.MONTH, calTemp);
			}else if(DateType.equalsIgnoreCase(DATE)){
				calendar.add(Calendar.DATE, calTemp);
			}else if(DateType.equalsIgnoreCase(HOUR)){
				calendar.add(Calendar.HOUR, calTemp);
			}else if(DateType.equalsIgnoreCase(MINUTE)){
				calendar.add(Calendar.MINUTE, calTemp);
			}else if(DateType.equalsIgnoreCase(SECOND)){
				calendar.add(Calendar.SECOND, calTemp);
			}
			return formatDate(calendar.getTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getReservedDatePeriod(List<String> occupyDateList , int calendarType , int duration) {
		List<String> ret = new ArrayList<String>(); 
		Calendar calendar = Calendar.getInstance();
		String preDate = null;
		for(String tempDateStr : occupyDateList){
			if(!ret.contains(tempDateStr)){
				ret.add(tempDateStr);
			}
			for(int i = 1 ; i < duration ; i++){
				calendar.setTime(parseDate(dayFormat, tempDateStr));
				calendar.add(calendarType , -i);
				preDate = dayFormat.format(calendar.getTime());
				if(!ret.contains(preDate)){
					ret.add(preDate);
				}
			}
		}
		return ret;
	}
	
	public static String getDateSerialNumber(int timezoneOffset){
		String localTime = DateUtil.UTCDateToString(DateUtil.getCurrentUTCDate(new Date()), timezoneOffset);
		localTime = localTime.replace("-", "").replace(" ", "").replace(":", "");
		return localTime;
	}
	
	public static Date getNextDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
	}
	
	public static Date getBeforeDawn() {
		String currentDate = getCurrentDate()+" 00:00:00";
		return parseDate(currentDate);
	}
	
	public static Date getMidNight() {
		String currentDate = getCurrentDate()+" 23:59:59";
		return parseDate(currentDate);
	}

	public static Date getMidNight(Date date) {
		String currentDate = dayFormat.format(date)+" 23:59:59";
		return parseDate(currentDate);
	}
	
	public static void main(String[] args) {
		
		String canDate = DateUtil.UTCDateToString(DateUtil.getCurrentUTCDate(new Date()), -5);
		System.out.println(canDate.substring(0,10));
		System.out.println(canDate.substring(10));
		System.out.println(getDateSerialNumber(8));
		
		
		Date utcDate = DateUtil.getCurrentUTCDate(new Date());
		System.out.println(DateUtil.formatDate(utcDate));
		String newDate = DateUtil.getNewDate(canDate, DateUtil.MINUTE, 30);
		System.out.println(newDate);
		/*System.out.println(DateUtil.formatDate(DateUtil.getCurrentUTCDate(new Date())));
		Date niaho = dateToUTCDate(new Date(), -5);
		Date niaho2 = getCurrentUTCDate(new Date());
		System.out.println(format.format(niaho));
		System.out.println(format.format(niaho2));
		int timeZoneOffset = -5;//(-1)*(date.getTimezoneOffset()/60);
		System.out.println(timeZoneOffset);
		String currentDate = "2014-03-12 13:48:18";//getCurrentDate();//
		System.out.println(currentDate);
		Date startDate = stringToUTCDate(currentDate, timeZoneOffset);
		System.out.println(format.format(startDate));
		String localDate = UTCDateToString(startDate, timeZoneOffset);
		System.out.println(localDate);*/
		Date date = new Date();
		String d = DateUtil.formatDate(date, "yyyyMMddHHmmssSSS");
		System.out.println(d);
	}

}
