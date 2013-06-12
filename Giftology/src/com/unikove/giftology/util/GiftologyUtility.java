package com.unikove.giftology.util;

public class GiftologyUtility {

	private static boolean production_flag = true;
	public static String TAG = "Giftology.Debug";
	public static int SERVERTIMEOUT = 30000;
	public static String GIFTOLOGYSERVER;
	static {
		if (production_flag) {
			GIFTOLOGYSERVER = "http://giftology.com/";
		} else {
			GIFTOLOGYSERVER = "http://master.mygiftology.net/";
		}
	}
}
