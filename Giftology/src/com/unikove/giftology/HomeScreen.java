package com.unikove.giftology;


import java.io.File;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.integralblue.httpresponsecache.HttpResponseCache;
import com.integralblue.httpresponsecache.compat.java.io.IOException;
import com.unikove.fb.SessionStore;
import com.unikove.fb.Utility;
import com.unikove.giftology.reciever.NotificationReciever;

public class HomeScreen extends TabActivity {
//	public static boolean fromBackPress=false;
//	private static final String APP_ID = "452167464830046";
	public static final String APP_ID = "105463376223556";
	private static final String[] PERMISSIONS = new String[]{ "offline_access","user_location","user_birthday", "publish_stream", "user_photos", "publish_checkins",
		"photo_upload","friends_birthday","read_friendlists","friends_location","manage_friendlists"};

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

	private static final int RECEIVERCALLDURATION=60*1000;
	private static int FLAG=0;
	
	
	public static Facebook facebook;

	ImageButton fb_connect;
	TextView text_below_fbConnect;
	Typeface tf;

	JSONObject jObject;
	JSONObject json = new JSONObject();
	JSONArray jsonArray;

	ProgressDialog pd;
	String api_Response;
	
	public static String aboutUsText="";

	public static SharedPreferences prefs;
	
	public static Context appLoginContext;

	public boolean saveCredentials(Facebook facebook) {
		Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
		//Log.i("Giftology.Debug","In restore credentials");
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		enableHttpCaching();
		
		/*prefs = getApplicationContext().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);

		Utility.mFacebook = new Facebook(APP_ID);                                  // Create the Facebook Object using the app id.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		SessionStore.restore(Utility.mFacebook, this);	
		restoreCredentials(Utility.mFacebook);
		if (Utility.mFacebook.isSessionValid()) {
			if_fb_loggedin();
		}
		else{
			if_fb_not_loggedin();
		}*/
	}
	
	
	public void onResume(){
		super.onResume();
//		fromBackPress=false;
		
		prefs = getApplicationContext().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);

		Utility.mFacebook = new Facebook(APP_ID);                                  // Create the Facebook Object using the app id.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		SessionStore.restore(Utility.mFacebook, this);	
		restoreCredentials(Utility.mFacebook);
		GAUtility.trackView(this, "HomeScreen");
		/*
		 *if user is already logged in 
		 *
		 */
		
		
		if (Utility.mFacebook.isSessionValid()) {
			
			if_fb_loggedin();
		}
		/*
		 * if user is not logged in 
		 */
		else{
			if_fb_not_loggedin();
		}
	}			


	public void if_fb_loggedin(){
//		Toast.makeText(HomeScreen.this, "Events Screen", Toast.LENGTH_LONG).show();
		
		try{
			if(FLAG==0)
			{
			FLAG=1;
			callNotifications();
			}
		}catch(Exception e)
		{
			//Toast.makeText(HomeScreen.this, "No notifications available", Toast.LENGTH_SHORT).show();
		}
		startActivity(new Intent(HomeScreen.this,EventsScreen.class));
		overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
		finish();
	}
	public void if_fb_not_loggedin(){
		setContentView(R.layout.homescreen);
		initialize_homeScreen();
//		Toast.makeText(HomeScreen.this, "Home Screen", Toast.LENGTH_LONG).show();
	}

	private void callNotifications() {
		
		try {
			Intent alarmIntent = new Intent(this, NotificationReciever.class);
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			
			alarmManager.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(),RECEIVERCALLDURATION, pendingIntent);
		} catch (Exception e) {
			
		}
	
	}

	public void initialize_homeScreen(){
		tf=Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		fb_connect=(ImageButton)findViewById(R.id.fb_connect);
		text_below_fbConnect=(TextView)findViewById(R.id.textView);text_below_fbConnect.setTypeface(tf);
		
		fb_connect.setOnClickListener(new ImageView.OnClickListener(){
			public void onClick(View v) {
				if(isInternetAvailable()){
					//Log.i("Giftology.Debug","IN IMAGE CLICK LISTENER");
					GAUtility.trackEvent(HomeScreen.this, "Home Screen", "Login Button Clicked", "FB Button", (long)0);
					Utility.mFacebook.authorize(HomeScreen.this, PERMISSIONS, new LoginDialogListener());
				}
				else{
					
					GAUtility.trackEvent(HomeScreen.this, "Home Screen", "Login Failed/Internet", "FB Button", (long)0);
					
					Toast.makeText(HomeScreen.this,"Internet not available",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		try {
			Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
		}


	class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			saveCredentials(Utility.mFacebook);

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("isUserRegistered", false); 
			editor.commit();
			
			
			try {
				jObject = new JSONObject(Utility.mFacebook.request("me"));

				SharedPreferences.Editor editor_new = prefs.edit();

				editor_new.putString("UserName", jObject.getString("name"));
				editor_new.putString("UserId", jObject.getString("id"));

				String userName=jObject.getString("id");
				String url="http://graph.facebook.com/" + userName + "/picture?type=large" ;
				editor_new.putString("UserPicUrl", url);
				
				editor_new.commit();
				
				
				/*runOnUiThread(new Runnable() {
					public void run() {
						showToast("Data Saved Done");
					}
				});*/
			}
			catch(Exception e){
				//Log.i("Giftology.Debug",e.toString());
			}
			
			//new LoadProfile().execute("Start");
			//Log.i("Giftology.Debug",getApplicationContext().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE).getString("UserId", null));
			
		
			startActivity(new Intent(HomeScreen.this,HomeScreen.class));
			
			overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
			finish();
		}
		public void onFacebookError(FacebookError error) {
			showToast("Authentication with Facebook failed!");
			GAUtility.trackEvent(HomeScreen.this, "Home Screen", "Clicked FB Button/Facebook Failure", "FB Button", (long)0);
			
		
//			finish();
		}
		public void onError(DialogError error) {
			showToast("Authentication with Facebook failed!");
			GAUtility.trackEvent(HomeScreen.this, "Home Screen", "Clicked FB Button/Facebook Error", "FB Button", (long)0);
			
//			finish();
		}
		public void onCancel() {
			showToast("Authentication with Facebook cancelled!");
			GAUtility.trackEvent(HomeScreen.this, "Home Screen", "Clicked FB Button/Facebook Cancel", "FB Button", (long)0);
			
			//finish();
		}
	}
	private void showToast(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
/*	private class LoadProfile extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... params) {
			try {
				jObject = new JSONObject(Utility.mFacebook.request("me"));

				SharedPreferences.Editor editor = prefs.edit();

				editor.putString("UserName", jObject.getString("name"));
				editor.putString("UserId", jObject.getString("id"));

				String userName=jObject.getString("username");
				String url="http://graph.facebook.com/" + userName + "/picture?type=large" ;
				editor.putString("UserPicUrl", url);
				
				editor.commit();
				Log.i("Giftology.Debug",prefs.getAll().toString());
				runOnUiThread(new Runnable() {
					public void run() {
						showToast("Data Saved Done");
					}
				});
			}
			catch(Exception e){
				Log.i("Giftology.Debug","error");
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}*/
	public boolean isInternetAvailable(){
		try {
			ConnectivityManager connec = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
			android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (wifi.isConnected()) {
				return true;
			} 
			else if (mobile.isConnected()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	
	private void enableHttpCaching()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            try {
              File httpCacheDir = new File(getApplicationContext().getCacheDir()
                      , "http");
              long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
              HttpResponseCache.install(httpCacheDir, httpCacheSize);
            }  catch (java.io.IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}        
        }
        else
        {
            File httpCacheDir = new File(getApplicationContext().getCacheDir()
                    , "http");
            try {
                com.integralblue.httpresponsecache.HttpResponseCache.install
                    (httpCacheDir, 10 * 1024 * 1024);
            }  catch (java.io.IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
        }
    }


}
