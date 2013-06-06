package com.unikove.giftology;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class KeyGenerator {

	private static final String S_KEY = "GiftologyMobile422";

	/*
	 * generate random string
	 */
	public static String randomString() {
		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(6);
		for (int i = 0; i < 5; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}

	/*
	 * generate md5 key for above generated random key
	 */
	public static String md5(String s1) {
		String s = S_KEY + s1;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes(), 0, s.length());
			String hash = new BigInteger(1, digest.digest()).toString(16);
			return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*
	 * generating key and md5 appended URL
	 */
	public static String encodedURL(String url) {
		String encodedURL = "";
		String randomString = randomString();
		String md5String = md5(randomString);

		encodedURL = url + "rand=" + randomString + "&key=" + md5String;

		return encodedURL;
	}
}
