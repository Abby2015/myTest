package com.yum.kfc.brand.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author DING Weimin (wei-min.ding@hp.com) Jul 27, 2015 2:29:16 PM
 *
 */
public class MybatisTblShardStrategy {

	/**
	 * decode src as GBK. if src include non-ascii char, should use this method instead of byCRC32Mod10()
	 * 
	 * to check column's collation, use below sql
	 * SELECT name, collation_name FROM sys.databases;
	 * SELECT CONVERT (varchar, DATABASEPROPERTYEX('database_name','collation'));
	 * SELECT name, collation_name FROM sys.columns WHERE name = N'<insert character data type column name>';
	 * 
	 * here using GBK assuming column's collation is in Chinese_PRC_CI_AS
	 * 
	 * @param src
	 * @return
	 */
	public static int byMD5CRC32Mod10(String src){
		String md5;
		try {
			md5 = DigestUtils.md5Hex(src.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			//should not happen
			md5 = DigestUtils.md5Hex(src.getBytes());
		}
		//System.out.println("MD5: "+src+"\t"+md5);
		return byCRC32Mod10(md5);
	}
	
	/**
	 * decode src as ISO-8859-1. if src include non-ascii char, should use byMD5CRC32Mod10()
	 * 
	 * @param src
	 * @return
	 */
	public static int byCRC32Mod10(String src){
		CRC32 c = new CRC32();
		try {
			c.update(src.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			//should not happen
			c.update(src.getBytes());
		}
		long cv = c.getValue();
		//System.out.println("CRC32: "+src+"\t"+cv);
		return (int)(cv%10);
	}
	
	public static String byCRC32Mod10ForUser(String src){
		return "_"+byCRC32Mod10(src);
	}
	
	public static String byCRC32Mod10ForCpn(String src){
		return "_"+byCRC32Mod10(src);
	}
	
	public static char byLastChar(String src){
		return src.charAt(src.length()-1);
	}
	
	public static String byLast2Chars(String src){
		return src.substring(src.length()-2);
	}
	
	/**
	 * decode src as GBK. if src include non-ascii char, should use this method instead of byCRC32LastChar()
	 * 
	 * @param src
	 * @return
	 */
	public static char byMD5LastChar(String src){
		String md5;
		try {
			md5 = DigestUtils.md5Hex(src.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			//should not happen
			md5 = DigestUtils.md5Hex(src.getBytes());
		}
		//System.out.println("MD5: "+src+"\t"+md5);
		return byLastChar(md5);
	}
	
	/**
	 * decode src as ISO-8859-1. if src include non-ascii char, should use byMD5LastChar()
	 * 
	 * @param src
	 * @return
	 */
	public static char byCRC32LastChar(String src){
		CRC32 c = new CRC32();
		try {
			c.update(src.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			//should not happen
			c.update(src.getBytes());
		}
		long cv = c.getValue();
		//System.out.println("CRC32: "+src+"\t"+cv);
		return byLastChar(""+cv);
	}
	
	/**
	 * return last digit char('0'~'9') represented integer, i.e., '0' will return 0, 
	 * 	if no digit char found, then return len(src) mod 10, length count by char, not by byte.
	 * @param src
	 * @return
	 */
	public static int byLastDigitOrLenMod10(String src){
		for(int i=src.length()-1; i>=0; i--){
			char c = src.charAt(i);
			if ('0'<=c && c<='9') return c-'0';
		}
		return src.length()%10;
	}
	
	public static String byLast2DigitsOrCRC32Mod100(String src){
		int dc = 0;
		char[] cc = new char[2];
		for(int i=src.length()-1; i>=0 && dc<2; i--){
			char c = src.charAt(i);
			if ('0'<=c && c<='9') {
				cc[1-dc++]=c;
			}
		}
		if (dc==2) return new String(cc);


		CRC32 c = new CRC32();
		try {
			c.update(src.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			//should not happen
			c.update(src.getBytes());
		}
		long cv = c.getValue();
		//System.out.println("CRC32: "+src+"\t"+cv);
		return ""+(int)(cv%100);
	}
	
}
