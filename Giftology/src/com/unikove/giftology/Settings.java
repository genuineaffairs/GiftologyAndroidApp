package com.unikove.giftology;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;
import com.unikove.fb.Utility;
import com.unikove.giftology.reciever.NotificationReciever;

public class Settings extends Activity{
	JSONObject jObject;
	ImageView profilePic;
	TextView name,heading;
	String nameoftheuser;
	Bitmap bmp;
	int height,width,imageSize;
	ProgressBar pb;
	ProgressDialog pd;
	Button logout;
	Typeface tf;
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	public boolean dataSavedLogout=false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		GAUtility.trackView(Settings.this, "Settings");
		tf=Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		heading=(TextView)findViewById(R.id.profile);heading.setTypeface(tf);
		name=(TextView)findViewById(R.id.textView1);
		profilePic=(ImageView)findViewById(R.id.imageView1);
		pb=(ProgressBar)findViewById(R.id.progressBar1);
		logout=(Button)findViewById(R.id.button1);

		logout.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					GAUtility.trackEvent(Settings.this, "Settings", "Log out", "Logout", (long)0);
					new LogoutAsynTask().execute("Start");
				}
				else{
					Toast.makeText(Settings.this, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});

		new LoadProfile().execute("Start");
	}
	/*
	 * loading profile of the logged in user
	 */
	private class LoadProfile extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pb.setVisibility(View.VISIBLE);
			name.setVisibility(View.INVISIBLE);

			logout.setVisibility(View.INVISIBLE);

			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			height = dm.heightPixels;
			width = dm.widthPixels;

			/*if(height==1184 && width==720){
				imageSize=190;
			}
			if(height==1280 && width==720){
				imageSize=190;
			}
			else{
				imageSize=190;
			}*/

			if(returnScreen(width, height)==2){
				imageSize=85;
			}
			else if(returnScreen(width, height)==3){
				imageSize=123;
			}
			else if(Settings.returnScreen(width, height)==4){
				imageSize=191;
			}
			else if(Settings.returnScreen(width, height)==5){
				imageSize=161;
			}
			else if(Settings.returnScreen(width, height)==6){
				imageSize=216;
			}
			else if(Settings.returnScreen(width, height)==7){
				imageSize=191;
			}

			profilePic.getLayoutParams().height=imageSize;
			profilePic.getLayoutParams().width=imageSize;

		}

		@Override
		protected String doInBackground(String... params) {
			try {
				if(HomeScreen.prefs==null){
					HomeScreen.prefs= getApplicationContext().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
				}
				nameoftheuser=HomeScreen.prefs.getString("UserName", "");
				URL image_value= new URL(HomeScreen.prefs.getString("UserPicUrl", ""));
				bmp=BitmapFactory.decodeStream(image_value.openConnection().getInputStream());

			}
			catch(Exception e){
				Log.i("Error","error");
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			if(!(bmp==null)){
				name.setVisibility(View.VISIBLE);
				name.setText(nameoftheuser);
				profilePic.setImageBitmap(bmp);
			}
			pb.setVisibility(View.GONE);
			logout.setVisibility(View.VISIBLE);
		}
	}
	public boolean saveCredentials(Facebook facebook) {
		Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}
	private class LogoutAsynTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=ProgressDialog.show(Settings.this,"","Logging Out...",true,false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String response=Utility.mFacebook.logout(getApplicationContext());
				//clearApplicationData();
				Log.i("Response",response);
				if(response.equals("true")){
					dataSavedLogout=saveCredentials(Utility.mFacebook);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			if(dataSavedLogout){
				dataSavedLogout=false;
				startActivity(new Intent(Settings.this,HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
				stopAlarmManagerNotificationOnLogout();
				pd.dismiss();
				finish();
			}
		}
	}
	
	public void stopAlarmManagerNotificationOnLogout()
	{
	Intent intentstop = new Intent(this, NotificationReciever.class);
	PendingIntent senderstop = PendingIntent.getBroadcast(this,
	            0, intentstop, 0);
	AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);

	alarmManagerstop.cancel(senderstop);
	NotificationManager notificationManager = 
			  (NotificationManager) this.getSystemService("notification");
	notificationManager.cancelAll();
	}
	
	public static int returnScreen(int width,int height){
		int sizeValue=0;
		if((modValue(320-width)<30) && (modValue(480-height)<30)){        //for mdpi(320*480)
			sizeValue=2;
		}
		if((modValue(480-width)<30) && (modValue(800-height)<30)){        //for hdpi(480*800)
			sizeValue=3;
		}
		if((modValue(720-width)<30) || (modValue(1280-height)<30)){        //for xhdpi(720*1280)
			sizeValue=4;
		}
		if((modValue(600-width)<30) || (modValue(1024-height)<30)){        //for large-hdpi(600*1024)
			sizeValue=5;
		}
		if((modValue(800-width)<30) && (modValue(1280-height)<30)){        //for large-hdpi(600*1024)
			sizeValue=6;
		}
		if((modValue(720-width)<10) && (modValue(1280-height)<10)){        //for large-hdpi(600*1024)
			sizeValue=7;
		}


		return sizeValue;

	}
	public static int modValue(int x){
		int modVal=0;
		if(x<0){
			modVal=x-(2*x);
		}
		else{
			modVal=x;
		}
		return modVal;
	}
	public boolean isInternetAvailable(){
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
	}
	public void clearApplicationData() {
		File cache = getCacheDir();
		File appDir = new File(cache.getParent());
		if(appDir.exists()){
			String[] children = appDir.list();
			for(String s : children){
				if(!s.equals("lib")){
					deleteDir(new File(appDir, s));
					Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s +" DELETED *******************");
				}
			}
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
}
