package com.yum.kfc.brand.common.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.hp.jdf.ssm.util.StringUtil;

public class ShareUtil {
	public static String generateShareId(String userId, String objType, String objId) {
		String forID = userId+"\t"+objType+"\t"+objId;
		String key = (StringUtil.reverse(userId)).substring(0, 16);
		String longShareId = Hex.encodeHexString(StringUtil.aesEnc(forID, key));
		String shortShareId = DigestUtils.md5Hex(longShareId);
		
		return shortShareId;
	}
}
