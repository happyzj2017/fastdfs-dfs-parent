package com.netwaymedia.dfs.utils;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5Utils {

	private static final Logger log = LoggerFactory.getLogger(MD5Utils.class);

	private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private MD5Utils() {
		
	}
	/**
	 * 加密
	 * 
	 * @param seq
	 * @return
	 */
	public static String md5(String seq) {
		String digest = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md = md5.digest(seq.getBytes());
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			digest = new String(str);
		} catch (Exception e) {
			log.error("md5 digest error", e);
			digest = null;
		}

		return digest;
	}

	public static void main(String[] args) {
		String pass = "1111";

		System.out.println(MD5Utils.md5(pass));
	}

}
