package com.unikove.giftology.reciever;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

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
	private static boolean BIRTHDAYGIFTNOTGIVEN = false;
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

	private void checkFacebookExists(Context context)
	{
		if (Utility.mFacebook == null) {
			Utility.mFacebook = new Facebook(HomeScreen.APP_ID); // Create the
																
			Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
			SessionStore.restore(Utility.mFacebook, context);
			restoreCredentials(Utility.mFacebook, context);
		}

	}

	private void doBirthdayNotificationPreProcessing(Context context)
	{
		//get current date
				Calendar currentDate = Calendar.getInstance();
		notificationPreferences = context.getApplicationContext()
				.getSharedPreferences(NKEY, Context.MODE_PRIVATE);
		if (notificationPreferences != null) {
			
			BIRTHDAYGIFTNOTGIVEN = notificationPreferences.getBoolean(
					BIRTHDAYGIFTNOTGIVENKEY, true);

		}

		if (((currentDate.getTime().getHours() == 0)&& (currentDate.getTime().getMinutes() >= 4)&& (!BIRTHDAYGIFTNOTGIVEN)))  {
			BIRTHDAYGIFTNOTGIVEN = true;
			Editor editor = context.getApplicationContext()
					.getSharedPreferences(NKEY, Context.MODE_PRIVATE).edit();
			editor.putBoolean(BIRTHDAYGIFTNOTGIVENKEY, true);
			editor.commit();
		}

		if (currentDate.getTime().getHours() == 18
				&& currentDate.getTime().getMinutes() == 0) {
			BIRTHDAYGIFTNOTGIVEN = true;
			Editor editor = context.getApplicationContext()
					.getSharedPreferences(NKEY, Context.MODE_PRIVATE).edit();
			editor.putBoolean(BIRTHDAYGIFTNOTGIVENKEY, true);
			editor.commit();
		}
	}
	
	public void doBirthdayNotification(Context context)
	{
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Boolean bithday_notification = sharedPrefs.getBoolean("bithday_notification",true);
      if(bithday_notification){
        String message;
		message = runCommand(context, EVENT);
		if (!(message.equalsIgnoreCase(lastEventMessage))) {
		if (!message.equalsIgnoreCase(EMPTYSTRING)) {
			lastEventMessage = message;
			String sendmessage;
			if (message.equalsIgnoreCase(YOURFRIENDS)) {
				sendmessage = message + BIRTHDAYS;
			} else {
				sendmessage = message + BIRTHDAY;
			}
			makeNotifications(sendmessage, context);
			BIRTHDAYGIFTNOTGIVEN = false;
		}
		}
      }
	}
	
	public void friendNotification(Context context)
	{
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
		Boolean friend_notification = sharedPrefs.getBoolean("friend_notification",true);
        
		String message;
		countForFriend = 0;
		if(friend_notification){
		message = runCommand(context, FRIEND);

		if (!(message.equalsIgnoreCase(lastFriendMessage))) {
			if (!message.equalsIgnoreCase(EMPTYSTRING)) {
				lastFriendMessage = message;
				String sendmessage = message + FRIENDJOINED;
				if (FIRSTFRIENDTIME) {

					makeNotifications(sendmessage, context);
				}
				
				FIRSTFRIENDTIME = true;
			}
		}
		}
	}
	
	public void giftNotification(Context context)
	{
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
		Boolean my_gift = sharedPrefs.getBoolean("my_gift",true);
        
		String message;
		if(my_gift){
		message = runCommand(context, GIFT);
		if (!(message.equalsIgnoreCase(lastGiftMessage))) {
			if (!message.equalsIgnoreCase(EMPTYSTRING)) {
				lastGiftMessage = message;
				String sendmessage = message.split(";")[1]
						+ HASSENTGIFT;
				if (FIRSTIME) {

					makeNotifications(sendmessage, context);
				}
				FIRSTIME = true;

			}
		}

		}
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		//message which goes to notification
		String message;
		//check if facebook object exists? if not recreate it
		
		checkFacebookExists(arg0);
		
				//count for friend to reach 30 ie check every 30 minutes for new friend joining
		countForFriend++;
		
		
		//notification preference to store data related to notification , stores if birthdays are given
		
		doBirthdayNotificationPreProcessing(arg0);
		

		if (isInternetAvailable(arg0)) {
		
				if (BIRTHDAYGIFTNOTGIVEN) {
					doBirthdayNotification(arg0);

				}
			

			if (countForFriend == DURATIONFORFRIENDCHECK) {
				friendNotification(arg0);
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
				giftNotification(arg0);
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
		//	Log.i("Giftology.Notification", message);
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
