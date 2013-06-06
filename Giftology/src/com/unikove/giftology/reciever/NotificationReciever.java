package com.unikove.giftology.reciever;

import java.io.IOException;
import java.util.Calendar;

import org.apache.http.HttpEntity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.giftology.notifications.service_notifications.GiftologyNotification;
import com.giftology.notifications.service_rest.Invoker;
import com.giftology.notifications.service_rest.NewCelebrationCommand;
import com.giftology.notifications.service_rest.NewFriendCommand;
import com.giftology.notifications.service_rest.NewGiftCommand;
import com.giftology.notifications.service_rest.RESTConnect;
import com.giftology.notifications.service_rest.RESTConnectFactory;
import com.unikove.fb.SessionStore;
import com.unikove.fb.Utility;
import com.unikove.giftology.activityscreens.EventsScreen;
import com.unikove.giftology.activityscreens.HomeScreen;
import com.unikove.giftology.caching.CachingUtility;
import com.unikove.giftology.connectivity.ConnectionUtility;
import com.unikove.giftology.util.GiftologyUtility;

public class NotificationReciever extends BroadcastReceiver {

	private static final String GIFT = "gift";
	private static final String FRIEND = "friend";
	private static final String EVENT = "event";
	private static final String NOTIFICATION = "notification";
	public static int countForFriend = 0;
	public static int countForEvent = 0;
	public static int DURATIONFORFRIENDCHECK = 30;
	public static int DURATIONFOREVENTCHECK = 288;
	public static String lastGiftMessage = "";
	public static String lastFriendMessage = "";
	public static String lastEventMessage = "";
	public static String EMPTYSTRING = "";
	private static boolean FIRSTIME = false;
	private static boolean FIRSTFRIENDTIME = false;
	private final String FRIENDJOINED = " has joined Giftology.Give him a gift!";
	private final String BIRTHDAY = " has birthday today.";
	private final String HASSENTGIFT = " has sent you a gift!";
	private static boolean BIRTHDAYGIFTNOTGIVEN = true;
	private static Calendar lastDayDate = Calendar.getInstance();
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	private final String BIRTHDAYS = " have birthday today.";
	private final String YOURFRIENDS = "Your Friends";
	private SharedPreferences notificationPreferences;
	private static final String NKEY = "preferences_key";
	private static final String BIRTHDAYGIFTNOTGIVENKEY = "birthday_key";
	private String userID = null;

	public boolean restoreCredentials(Facebook facebook, Context context) {
		// Log.i("Giftology.Debug","In restore credentials");
		SharedPreferences sharedPreferences = context.getApplicationContext()
				.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		String message;

		if (Utility.mFacebook == null) {
			Utility.mFacebook = new Facebook(HomeScreen.APP_ID); // Create the
																	// Facebook
																	// Object
																	// using the
																	// app id.
			Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
			SessionStore.restore(Utility.mFacebook, arg0);
			restoreCredentials(Utility.mFacebook, arg0);
		}

		countForFriend++;
		Calendar currentDate = Calendar.getInstance();

		notificationPreferences = arg0.getApplicationContext()
				.getSharedPreferences(NKEY, Context.MODE_PRIVATE);
		if (notificationPreferences != null) {
			BIRTHDAYGIFTNOTGIVEN = notificationPreferences.getBoolean(
					BIRTHDAYGIFTNOTGIVENKEY, true);

		}

		if (currentDate.getTime().getHours() == 0
				&& currentDate.getTime().getMinutes() == 4) {
			BIRTHDAYGIFTNOTGIVEN = true;
			Editor editor = arg0.getApplicationContext()
					.getSharedPreferences(NKEY, Context.MODE_PRIVATE).edit();
			editor.putBoolean(BIRTHDAYGIFTNOTGIVENKEY, true);
			editor.commit();
		}

		if (currentDate.getTime().getHours() == 18
				&& currentDate.getTime().getMinutes() == 0) {
			BIRTHDAYGIFTNOTGIVEN = true;
			Editor editor = arg0.getApplicationContext()
					.getSharedPreferences(NKEY, Context.MODE_PRIVATE).edit();
			editor.putBoolean(BIRTHDAYGIFTNOTGIVENKEY, true);
			editor.commit();
		}

		if (isInternetAvailable(arg0)) {
			if (lastDayDate.before(currentDate)) {
				if (BIRTHDAYGIFTNOTGIVEN) {

					message = runCommand(arg0, EVENT);
					if (!message.equalsIgnoreCase(EMPTYSTRING)) {
						lastEventMessage = message;
						String sendmessage;
						if (message.equalsIgnoreCase(YOURFRIENDS)) {
							sendmessage = message + BIRTHDAYS;
						} else {
							sendmessage = message + BIRTHDAY;
						}
						makeNotifications(sendmessage, arg0);

					}
					lastDayDate = currentDate;
					BIRTHDAYGIFTNOTGIVEN = false;
					Editor editor = arg0.getApplicationContext()
							.getSharedPreferences(NKEY, Context.MODE_PRIVATE)
							.edit();
					editor.putBoolean(BIRTHDAYGIFTNOTGIVENKEY, false);
					editor.commit();

				}
			}

			if (countForFriend == DURATIONFORFRIENDCHECK) {
				countForFriend = 0;
				message = runCommand(arg0, FRIEND);

				if (!(message.equalsIgnoreCase(lastFriendMessage))) {
					if (!message.equalsIgnoreCase(EMPTYSTRING)) {
						lastFriendMessage = message;
						String sendmessage = message + FRIENDJOINED;
						if (FIRSTFRIENDTIME) {

							makeNotifications(sendmessage, arg0);
						}
						
						FIRSTFRIENDTIME = true;
					}
				}
			}
			/*
			 * if (countForEvent == DURATIONFOREVENTCHECK) {
			 * 
			 * message = runCommand(arg0, EVENT); if
			 * (!(message.equalsIgnoreCase(lastEventMessage))) { if
			 * (!message.equalsIgnoreCase(EMPTYSTRING)) { lastEventMessage =
			 * message; String sendmessage = message + BIRTHDAY;
			 * 
			 * 
			 * makeNotifications(sendmessage, arg0);
			 * 
			 * } } }
			 */
			if (countForFriend != 0) {
				message = runCommand(arg0, GIFT);
				if (!(message.equalsIgnoreCase(lastGiftMessage))) {
					if (!message.equalsIgnoreCase(EMPTYSTRING)) {
						lastGiftMessage = message;
						String sendmessage = message.split(";")[1]
								+ HASSENTGIFT;
						if (FIRSTIME) {

							makeNotifications(sendmessage, arg0);
						}
						FIRSTIME = true;

					}
				}
			}
		}
	}

	/*private void updateGiftCache(Context context) {
		String url = GiftologyUtility.GIFTOLOGYSERVER
				+ "gifts/ws_list.json?receiver_fb_id=" + userID + "&";
		HttpEntity entity1 = ConnectionUtility.getHttpEntity(url);

		// If the response does not enclose an entity, there is no need
		StringBuilder sb;
		try {
			sb = ConnectionUtility.parseEntity(entity1);
			CachingUtility.setHttpEntityStringBuilder(context, sb);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block

		} catch (IOException e) {
			// TODO Auto-generated catch block

		}

	}*/

	private String runCommand(Context context, String commandType) {

		if (HomeScreen.prefs == null) {

			HomeScreen.prefs = context.getApplicationContext()
					.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
			// Log.i("Giftology.Notification","HomeScreen Prefs"+HomeScreen.prefs.getAll().toString());

		}
		try {
			userID = HomeScreen.prefs.getString("UserId", null);

		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
		// Connect to giftology-ws via command. Catch exception.
		RESTConnect restConnect = RESTConnectFactory.giftologyRESTConnect();
		Invoker invoker;
		// Log.i("Giftology.Notification","in run command "+userID);
		if (commandType.equalsIgnoreCase(GIFT)) {

			NewGiftCommand newGiftCommand = new NewGiftCommand(restConnect);

			invoker = new Invoker(newGiftCommand);

			return invoker.call(userID);
		} else if (commandType.equalsIgnoreCase(FRIEND)) {
			// Log.i("Giftology.Debug","In FRIEND RECEIVER");
			NewFriendCommand newFriendCommand = new NewFriendCommand(
					restConnect);

			invoker = new Invoker(newFriendCommand);

			return invoker.call(userID);
		} else {
			// Log.i("Giftology.Debug","In EVENT RECEIVER");

			NewCelebrationCommand newCelebrationCommand = new NewCelebrationCommand(
					restConnect);

			invoker = new Invoker(newCelebrationCommand);

			return invoker.call(userID);
		}
	}

	private void makeNotifications(String message, Context context) {
		try {
			Log.i("Giftology.Notification", message);
			Intent intent = new Intent(context, EventsScreen.class);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0,
					intent, 0);

			// Build notification by calling GiftologyNotification
			// (builder)class

			Notification noti = new GiftologyNotification().getNotification(
					message, context, pIntent);

			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(NOTIFICATION);

			notificationManager.notify(0, noti);
		} catch (Exception e) {
			// Log.i("Giftology.Notification","CRASH:makeNotifications");
		}
	}

	public boolean isInternetAvailable(Context context) {
		try {
			ConnectivityManager connec = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);
			android.net.NetworkInfo wifi = connec
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			android.net.NetworkInfo mobile = connec
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (wifi.isConnected()) {
				return true;
			} else if (mobile.isConnected()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
