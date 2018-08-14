package com.yum.kfc.brand.common.utils;

import java.util.Random;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class SaltUtil {
	
	private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_=+[]{}|<>;:.";
	private static final int LEN = CHARS.length();
	
	public static String encryption(String askId, String key) {
		byte[] ebs = StringUtil.desEnc(askId, key);
		String token = Hex.encodeHexString(ebs);
		return token;
	}
	
	public static String decryption(String encryptionText, String key) {
		String decryStr = null;
		try {
			decryStr = StringUtil.desDec(Hex.decodeHex(encryptionText.toCharArray()), key);
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return decryStr;
	}
	
	
	public static String newSalt(int len){
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		for(int i=0; i<len; i++){
			sb.append(CHARS.charAt(r.nextInt(LEN)));
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		//浏览器token
		String userId = "14AE9762BFDF3EE81E8FCCD496BBAE2DB4043B34209";
		String token = SaltUtil.encryption(SaltUtil.newSalt(6)+ DigestUtils.md5Hex(userId), BreakfastAction.BROWSER_TOKEN_KEY);
		System.out.println(token);
		//后台解析
		String md5UserId = SaltUtil.decryption(token,  BreakfastAction.BROWSER_TOKEN_KEY);
		System.out.println(md5UserId);
		md5UserId = md5UserId.substring(6);
		System.out.println(md5UserId);
		String trueUserId = DigestUtils.md5Hex(userId);
		System.out.println(md5UserId.equals(trueUserId));
	}*/

}
