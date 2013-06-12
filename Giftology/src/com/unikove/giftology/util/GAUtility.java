package com.unikove.giftology.util;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;

public class GAUtility {

	public static void trackView(Context ctx, String sScreenName) {
		EasyTracker.getInstance().setContext(ctx);
		EasyTracker.getTracker().sendView(sScreenName);

	}

	public static void trackEvent(Context ctx, String sCategory,
			String sAction, String sLabel, Long lOptVal) {
		EasyTracker.getInstance().setContext(ctx);
		EasyTracker.getTracker().sendEvent(sCategory, sAction, sLabel, lOptVal);
	}

}
 