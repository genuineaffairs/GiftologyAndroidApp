package com.unikove.giftology;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.cache.CacheResponseStatus;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ch.boye.httpclientandroidlib.impl.client.cache.CacheConfig;
import ch.boye.httpclientandroidlib.impl.client.cache.CachingHttpClient;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HeaderParser.CacheControlHandler;
import com.unikove.fb.SessionStore;
import com.unikove.fb.Utility;
import com.unikove.giftology.caching.GiftologyHttpClientCaching;
import com.unikove.giftology.reciever.NotificationReciever;

public class MyGifts extends Activity {
	JSONObject jObject;
	ImageView profilePic;
	TextView name,heading,totalcostofgifts;
	String nameoftheuser;
	Bitmap bmp;
	int height,width,imageSize;
	ProgressBar pb1,pb2;
	Typeface tf;
	ArrayList<MyGiftsDetail> al=new ArrayList<MyGiftsDetail>();
	MyGiftsAdapter mga;
	ListView listview;
	RelativeLayout listViewRelative;
	TextView askfriendstext;
	public boolean dataSavedLogout=false;
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	private ProgressDialog pd;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mygifts);
		EventsScreen.screen=3;
		GAUtility.trackView(this, "MyGifts");
		tf=Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		heading=(TextView)findViewById(R.id.profile);heading.setTypeface(tf);
		listview=(ListView)findViewById(R.id.listView);
		listViewRelative=(RelativeLayout)findViewById(R.id.rellistview);
		askfriendstext=(TextView)findViewById(R.id.emptyaskfriend);askfriendstext.setTypeface(tf);

		new LoadProfile().execute("Start");
		new LoadGiftsTask().execute("Start");
	}
	
	// For preventing app from crash applying patch
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Utility.mFacebook = new Facebook(HomeScreen.APP_ID);                                  // Create the Facebook Object using the app id.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		SessionStore.restore(Utility.mFacebook, MyGifts.this);	
		restoreCredentials(Utility.mFacebook);
	}
	
		public boolean restoreCredentials(Facebook facebook) {
			//Log.i("Giftology.Debug","In restore credentials");
			SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
			facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
			facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
			return facebook.isSessionValid();
		}
	/*
	 * loading logged in user profile
	 */
	private class LoadProfile extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			name=(TextView)findViewById(R.id.textView1);
			totalcostofgifts=(TextView)findViewById(R.id.textView2);
			profilePic=(ImageView)findViewById(R.id.imageView123);
			pb1=(ProgressBar)findViewById(R.id.progressBar1);
			pb1.setVisibility(View.VISIBLE);
			name.setVisibility(View.INVISIBLE);name.setTypeface(tf);
			totalcostofgifts.setVisibility(View.GONE);totalcostofgifts.setTypeface(tf);

			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			height = dm.heightPixels;
			width = dm.widthPixels;


			if(Settings.returnScreen(width, height)==2){
				imageSize=85;
			}
			else if(Settings.returnScreen(width, height)==3){
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
			pb1.setVisibility(View.GONE);
		}
	}
	/*
	 * laoding gifts received by any user
	 */
	private class LoadGiftsTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//			Collections.reverse(al);
			mga=new MyGiftsAdapter(MyGifts.this,al);
			pb2=(ProgressBar)findViewById(R.id.progressBar2);
			pb2.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			String id="";
			try {
				jObject = new JSONObject(Utility.mFacebook.request("me"));
				id=jObject.getString("id");

				callwebservice(id);
				Collections.reverse(al);
			}
			catch(java.net.SocketTimeoutException e){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						/*Utility.mFacebook = new Facebook(HomeScreen.APP_ID);                 
						
						Log.i("FACEBOOK.DEBUG","Facebook: "+ Utility.mFacebook.toString());
						
						restoreCredentials(Utility.mFacebook) ;
						*/
						
						
						
						Toast.makeText(MyGifts.this, "Network error", Toast.LENGTH_LONG).show();
					}
				});

				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			//			callwebservice(id);
			//			Collections.reverse(al);
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pb2.setVisibility(View.GONE);
			mga.notifyDataSetChanged();
			listview.setAdapter(mga);
			new ImageDownloadAsync().execute("Start");
		}
	}
	/*
	 * loading image
	 */
	private class ImageDownloadAsync extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			String url=params[0];
			//Log.i("", url);
			for(int i=0;i<al.size();i++){
				MyGiftsDetail gdet=al.get(i);
				URL urlimage;
				
				try {
					urlimage = new URL(gdet.getSmall_Pic_URL().replace(" ", "%20"));
					//Log.i("Giftology.Debug","URL of IMAGE: "+ urlimage.toString());
					gdet.setBITMAP(GiftsToSend.getImageBitmap(urlimage));
					al.set(i, gdet);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				publishProgress(0);
			}
			System.gc();
			return "Done";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			mga.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mga.notifyDataSetChanged();
			listview.setAdapter(mga);
		}
	}
	
	/*
	 * calling webservice to retrieve all gifts received
	 */
	public void callwebservice(String id) throws Exception{//100002950256522
		String str="";
		//		try {
		
		
		CacheConfig cacheConfig = new CacheConfig();
		 cacheConfig.setMaxCacheEntries(1000);
		 cacheConfig.setMaxObjectSizeBytes(1024 * 1024);
		cacheConfig.setSharedCache(false);

		 HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,30000);
			HttpConnectionParams.setSoTimeout(httpParams, 30000);

		 HttpClient cachingClient = new CachingHttpClient(new DefaultHttpClient(httpParams), cacheConfig);

		 HttpContext localContext = new BasicHttpContext();
		
		HttpPost httpPost = new HttpPost(KeyGenerator.encodedURL("http://giftology.com/gifts/ws_list.json?receiver_fb_id="+id+"&"));
		
		HttpResponse response = cachingClient.execute(httpPost,localContext);
	
		CacheResponseStatus responseStatus = (CacheResponseStatus) localContext.getAttribute(
				 CachingHttpClient.CACHE_RESPONSE_STATUS);
		
		Log.i("Giftology.Debug",responseStatus.toString());
		HttpEntity entity1 = response.getEntity();
		// If the response does not enclose an entity, there is no need
		if (entity1 != null) {
			InputStream instream = entity1.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(instream));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				//Log.i("Giftology.Debug",line);
			} 
			str=sb.toString();
			if(str.equals("{\"gifts\":[]}")){
				runOnUiThread(new Runnable() {
					public void run() { 	
						listViewRelative.setVisibility(View.GONE);
						askfriendstext.setVisibility(View.VISIBLE);
					}
				});

			}
			else{
				runOnUiThread(new Runnable() {
					public void run() { 	
						listViewRelative.setVisibility(View.VISIBLE);
						askfriendstext.setVisibility(View.GONE);
					}
				});
				fillArrayList(str);
			}
			//Log.i("","response  xml file:" +sb.toString()); 
		}
		/*} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	public void fillArrayList(String s){
		al.clear();
		try {
			JSONObject jsonObject=new JSONObject(s);
			JSONArray jsonArray=jsonObject.getJSONArray("gifts");

			for(int i=0;i<jsonArray.length();i++){
				MyGiftsDetail mgd=new MyGiftsDetail();

				JSONObject jsonObjectElement=jsonArray.getJSONObject(i);

				JSONObject jsonObjectGifts=jsonObjectElement.getJSONObject("Gift");
				mgd.setID(jsonObjectGifts.getString("id"));
				mgd.setEXPIRY(jsonObjectGifts.getString("expiry_date"));

				JSONObject jsonVendor=jsonObjectElement.getJSONObject("Vendor");
				mgd.setName(jsonVendor.getString("name"));
				mgd.setSmall_Pic_URL("http://www.giftology.com/"+jsonVendor.getString("thumb_image"));
				mgd.setLarge_Pic_URL("http://www.giftology.com/"+jsonVendor.getString("wide_image"));

				JSONObject jsonSender=jsonObjectElement.getJSONObject("Sender");
				mgd.setFROM(jsonSender.getString("username"));

				JSONObject jsonProduct=jsonObjectElement.getJSONObject("Product");
				mgd.setPRICE(jsonProduct.getString("min_value"));

				al.add(mgd);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
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
				new AboutUSAsync().execute("Start");
			}
			else{
				Toast.makeText(MyGifts.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.txtLogout:
			if(isInternetAvailable()){
				new LogoutAsynTask().execute("Start");
			}
			else{
				Toast.makeText(MyGifts.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;

		}
		return true;
	}

	private class LogoutAsynTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=ProgressDialog.show(MyGifts.this,"","Logging Out...",true,false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				
				GAUtility.trackEvent(MyGifts.this, "My Gifts Screen", "Logout Button Clicked", "Menu", (long)0);
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
				startActivity(new Intent(MyGifts.this,HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

			final Dialog dialog = new Dialog(MyGifts.this);
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
			GAUtility.trackEvent(MyGifts.this, "My Gifts Screen", "About Us Clicked", "Menu", (long)0);
		
			if(HomeScreen.aboutUsText.equals("")){
				try {
					HttpParams httpParams = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParams,30000);
					HttpConnectionParams.setSoTimeout(httpParams, 30000);
					HttpClient client = new DefaultHttpClient(httpParams);
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
							Toast.makeText(MyGifts.this, "Network error.Please try again later", Toast.LENGTH_LONG).show();
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