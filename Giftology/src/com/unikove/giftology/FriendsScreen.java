package com.unikove.giftology;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.unikove.fb.BaseRequestListener;
import com.unikove.fb.SessionStore;
import com.unikove.fb.Utility;
import com.unikove.giftology.reciever.NotificationReciever;

public class FriendsScreen extends Activity implements /*OnItemClickListener,*/Runnable {
	private Handler mHandler;
	protected ListView friendsList;
	protected static JSONArray jsonArray;
	protected String graph_or_fql;
	private ProgressDialog pd;
	String api_Response;
	boolean friendList_Fetched;
	ArrayList<FriendDetail> al_friend=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> al_friend_search=new ArrayList<FriendDetail>();
	EditText searchbox;
	ImageView search;
	TextView heading;
	Typeface tf;
	static FriendListAdapter fla;
	static FriendListAdapter flad;
	public boolean dataSavedLogout=false;
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	public boolean progressRunning=true;
	public boolean timeOut=false;
	public static boolean fromFriendScreen=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_list);

		EventsScreen.screen=2;
		GAUtility.trackView(this, "FriendsScreen");
		friendsList=(ListView)findViewById(R.id.friends_list);
		search=(ImageView)findViewById(R.id.search);
		searchbox=(EditText)findViewById(R.id.editText);
		heading=(TextView)findViewById(R.id.heading);
		tf=Typeface.createFromAsset(getAssets(),"fonts/arial.ttf");heading.setTypeface(tf);

		
		mHandler = new Handler();
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		startTimer();
		pd = ProgressDialog.show(FriendsScreen.this, "", "Loading Friends...", true,false);
		Thread thread = new Thread(this);
		thread.start();
		search.setOnClickListener(new ImageView.OnClickListener(){
			public void onClick(View v) {
				al_friend_search.clear();
				String word=searchbox.getText().toString();
				for(int i=0;i<al_friend.size();i++){
					FriendDetail fd=al_friend.get(i);
					if(fd.getName().toLowerCase().contains(word.toLowerCase())){
						al_friend_search.add(fd);
					}
				}
				flad=new FriendListAdapter(FriendsScreen.this, al_friend_search);
				friendsList.setAdapter(flad);
			}

		});

		searchbox.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {

			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				al_friend_search.clear();
				String word=searchbox.getText().toString();
				for(int i=0;i<al_friend.size();i++){
					FriendDetail fd=al_friend.get(i);
					if(fd.getName().toLowerCase().contains(word.toLowerCase())){
						al_friend_search.add(fd);
					}
				}
				flad=new FriendListAdapter(FriendsScreen.this, al_friend_search);
				friendsList.setAdapter(flad);
			}
		});
		
	

	}
	
	// For preventing app from crash applying patch
		
		
		public boolean restoreCredentials(Facebook facebook) {
		//	Log.i("Giftology.Debug","In restore credentials");
			SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
			facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
			facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
			return facebook.isSessionValid();
		}
		
	@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			
			Utility.mFacebook = new Facebook(HomeScreen.APP_ID);                                  // Create the Facebook Object using the app id.
			Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
			SessionStore.restore(Utility.mFacebook, FriendsScreen.this);	
			restoreCredentials(Utility.mFacebook);
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.txtAbout:
			if(isInternetAvailable()){
				
				//EasyTracker.getTracker().sendEvent("Menu Click", "Clicked About Us", "", (long) 0);
				new AboutUSAsync().execute("Start");
			}
			else{
				Toast.makeText(FriendsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.txtLogout:
			if(isInternetAvailable()){
				
				new LogoutAsynTask().execute("Start");
			}
			else{
				Toast.makeText(FriendsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;

		}
		return true;
	}

	private class LogoutAsynTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			try {
				pd=ProgressDialog.show(FriendsScreen.this,"","Logging Out...",true,false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				pd.dismiss();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				
				//Log.i("Giftology.Debug",getApplicationContext().toString());
				String response=Utility.mFacebook.logout(getApplicationContext());
				
				GAUtility.trackEvent(FriendsScreen.this, "Friends Screen", "Logout Button Clicked", "Menu", (long)0);
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
				startActivity(new Intent(FriendsScreen.this,HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
	
	public boolean saveCredentials(Facebook facebook) {
		Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}


	private class AboutUSAsync extends AsyncTask<String, Integer, String> {
		TextView txt;
		ProgressBar pb;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			final Dialog dialog = new Dialog(FriendsScreen.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.about);
			txt=(TextView)dialog.findViewById(R.id.textViewdesc);txt.setTypeface(tf);
			pb=(ProgressBar)dialog.findViewById(R.id.progressBar1);
			Button cancel=(Button)dialog.findViewById(R.id.cancel);cancel.setTypeface(tf);
			TextView heading=(TextView)dialog.findViewById(R.id.aboutHeading);heading.setTypeface(tf);

			if(HomeScreen.aboutUsText.equals("")){
				txt.setText("");
				pb.setVisibility(View.VISIBLE);
			}
			else{
				txt.setText(HomeScreen.aboutUsText);
			}

			cancel.setOnClickListener(new ImageView.OnClickListener(){
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});

			dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			String result="";
			GAUtility.trackEvent(FriendsScreen.this, "Friend Screen", "About Us Clicked", "About US", (long)0);
				
			if(HomeScreen.aboutUsText.equals("")){
				try {
					HttpParams httpParams = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParams,30000);
					HttpConnectionParams.setSoTimeout(httpParams, 30000);
					HttpClient client = new DefaultHttpClient(httpParams);
//					HttpPost httpPost = new HttpPost("http://www.giftology.com/retailers/ws_about_us.json?rand=sumit&key=8c744f645901c282a5ef6aa3451e672c");
					HttpPost httpPost = new HttpPost(KeyGenerator.encodedURL("http://www.giftology.com/retailers/ws_about_us.json?"));
					
					HttpResponse response = client.execute(httpPost);
					HttpEntity entity1 = response.getEntity();
					// If the response does not enclose an entity, there is no need
					if (entity1 != null) {
						InputStream instream = entity1.getContent();
						BufferedReader br = new BufferedReader(new InputStreamReader(instream));
						StringBuilder sb = new StringBuilder();
						String line;
						while ((line = br.readLine()) != null) {
							sb.append(line);
						} 
						result=sb.toString();
					}
				} 
				catch(java.net.SocketTimeoutException e){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							
							Toast.makeText(FriendsScreen.this, "Network error.Please try again later", Toast.LENGTH_LONG).show();
							pd.dismiss();
						}
					});
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(!result.equals("")){
				JSONObject json;
				String about_us = null;
				try {
					json = new JSONObject(result);
					String about_us_content=json.getString("about_us_content");
					JSONObject json2=new JSONObject(about_us_content);
					about_us=json2.getString("about_us");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				pb.setVisibility(View.GONE);
				HomeScreen.aboutUsText=Html.fromHtml(about_us).toString();
				txt.setText(HomeScreen.aboutUsText);
			}
			else{
				pb.setVisibility(View.GONE);
			}
		}
	}
	/*
	 * fetching friends list
	 */
	public void populate_array_list(){
		try {
			if(!(api_Response.equals(""))){
				jsonArray = new JSONArray(api_Response);
				//				Toast.makeText(FriendsScreen.this,jsonArray.length()+"", Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for(int i=0;i<jsonArray.length();i++){
			FriendDetail fd=new FriendDetail();
			try {
				fd.setName(jsonArray.getJSONObject(i).getString("name"));
				fd.setPic_URL(jsonArray.getJSONObject(i).getString("pic"));
				fd.setID(""+jsonArray.getJSONObject(i).getLong("uid"));
				fd.setDOB(jsonArray.getJSONObject(i).getString("birthday_date"));
				al_friend.add(fd);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 *  callback after friends are fetched via fql query.
	 */
	public class FriendsRequestListener extends BaseRequestListener {
		public void onComplete(final String response, final Object state) {
			api_Response=response;
			friendList_Fetched=true;
			handler.sendEmptyMessage(0);
		}
		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),Toast.LENGTH_SHORT).show();
		}
	}
	public void run() {
		String query="select name, uid, pic,birthday_date from user where uid in (select uid2 from friend where uid1=me()) order by name";
		Bundle param = new Bundle();
		param.putString("method", "fql.query");
		param.putString("query", query);
		Utility.mAsyncRunner.request(null, param,new FriendsRequestListener());
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		
			if(pd!=null){
				progressRunning=false;
				pd.dismiss();
			}
			if(!timeOut){
				populate_array_list();
				fla=new FriendListAdapter(FriendsScreen.this,al_friend);
				friendsList.setAdapter(fla);
			}
			timeOut=false;
		}
	};
	public static void callnotify(){
		if(!(fla==null)){
			fla.notifyDataSetChanged();
		}
		if(!(flad==null)){
			flad.notifyDataSetChanged();
		}
	}
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
			// TODO Auto-generated catch block
			return false;
		}
	}
	public void startTimer(){
		progressRunning=true;
		timeOut=false;
		
		TimerTask task = new TimerTask() {
			public void run() {
				ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
				List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
				String currentactivity=taskInfo.get(0).topActivity.getClassName();
				if(currentactivity.equals("com.unikove.giftology.FriendsScreen")){
					if(progressRunning){
						if(pd!=null){
							pd.dismiss();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									timeOut=true;
									/*Utility.mFacebook = new Facebook(HomeScreen.APP_ID);                 
									
									Log.i("FACEBOOK.DEBUG","Facebook: "+ Utility.mFacebook.toString());
									
									restoreCredentials(Utility.mFacebook) ;*/
									
									/*Utility.mFacebook = new Facebook(HomeScreen.APP_ID);                                  // Create the Facebook Object using the app id.
									Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
									SessionStore.restore(Utility.mFacebook, FriendsScreen.this);	
									restoreCredentials(Utility.mFacebook);*/
									
									Toast.makeText(FriendsScreen.this, "Network error.Please try again later", Toast.LENGTH_LONG).show();
								}
							});
						}
					}
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(task,30000);

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
