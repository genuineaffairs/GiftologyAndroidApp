package com.unikove.giftology.activityscreens;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.google.analytics.tracking.android.EasyTracker;
import com.unikove.fb.SessionStore;
import com.unikove.fb.Utility;
import com.unikove.giftology.GiftsToSendAdapter;
import com.unikove.giftology.R;
import com.unikove.giftology.data.GiftsToSendDetail;
import com.unikove.giftology.reciever.NotificationReciever;
import com.unikove.giftology.util.CachingUtility;
import com.unikove.giftology.util.ConnectionUtility;
import com.unikove.giftology.util.GAUtility;
import com.unikove.giftology.util.GiftologyUtility;

public class GiftsToSend extends Activity {
	TextView txt, toplabel;
	ListView listview;
	ImageView image;
	public static String name;
	String userId, userName, userFirstName, userLastName, userDOB, userEmail,
			userCity;
	public static int TIMEOUT_MILLISEC = 0;
	ArrayList<GiftsToSendDetail> al = new ArrayList<GiftsToSendDetail>();
	ProgressBar pb;
	GiftsToSendAdapter gtsa;
	Typeface tf;
	int height, width, imageSize;
	public boolean dataSavedLogout = false;
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	private ProgressDialog pd;

	// public boolean networkTimeOut=false;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.giftstosend);

		// networkTimeOut=false;

		GAUtility.trackView(GiftsToSend.this, "Gifts To Send");
		// EasyTracker.getInstance().activityStart(this);
		EventsScreen.flagScreen = "SendGift";
		txt = (TextView) findViewById(R.id.textView);
		toplabel = (TextView) findViewById(R.id.toplabel);
		image = (ImageView) findViewById(R.id.grid_item_image);
		listview = (ListView) findViewById(R.id.listView);
		tf = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		String[] name_arr = null;
		
		toplabel.setText(EventsScreen.personClickedName);
		if (EventsScreen.personClickedName != null) {
			name_arr = EventsScreen.personClickedName.split(" ");
		}
		enableHttpCaching();
		name = "";

		try {
			if (name_arr[0].length() <= 2) {
				name = name_arr[0] + " " + name_arr[1];
			} else {
				name = name_arr[0];
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;
		width = dm.widthPixels;

		if (Settings.returnScreen(width, height) == 2) {
			imageSize = 85;
		} else if (Settings.returnScreen(width, height) == 3) {
			imageSize = 123;
		} else if (Settings.returnScreen(width, height) == 4) {
			imageSize = 191;
		} else if (Settings.returnScreen(width, height) == 5) {
			imageSize = 161;
		} else if (Settings.returnScreen(width, height) == 6) {
			imageSize = 216;
		} else if (Settings.returnScreen(width, height) == 7) {
			imageSize = 191;
		}

		image.getLayoutParams().height = imageSize;//
		image.getLayoutParams().width = imageSize;//

		txt.setText("We think you should give " + name + " a gift");
		image.setImageBitmap(Utility.model.getImage(
				EventsScreen.personClickedID, EventsScreen.personClickedPicURL));

		/*
		 * image.setOnClickListener(new ImageView.OnClickListener(){ public void
		 * onClick(View v) { startActivity(new
		 * Intent(GiftsToSend.this,ShareTheNews.class)); } });
		 */
		new LoadGiftsTask().execute("Start");
	}

	// For preventing app from crash applying patch
	public void onResume() {
		super.onResume();

		Utility.mFacebook = new Facebook(HomeScreen.APP_ID); // Create the
																// Facebook
																// Object using
																// the app id.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		SessionStore.restore(Utility.mFacebook, GiftsToSend.this);
		restoreCredentials(Utility.mFacebook);
	}

	public boolean restoreCredentials(Facebook facebook) {
		// Log.i("Giftology.Debug","In restore credentials");
		SharedPreferences sharedPreferences = getApplicationContext()
				.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	/*
	 * fetching gifts to sent to selected friend
	 */
	private class LoadGiftsTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// networkTimeOut=false;
			gtsa = new GiftsToSendAdapter(GiftsToSend.this, al);
			pb = (ProgressBar) findViewById(R.id.progressBar1);
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			try {
				if(ConnectionUtility.isInternetAvailable(getApplicationContext()))
				{
					callwebservice(EventsScreen.personClickedID);
				}
				else{
					throw new SocketTimeoutException();
				}
			
			} catch (java.net.SocketTimeoutException e) {
				// networkTimeOut=true;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(GiftsToSend.this,
								"Network error.Please try again later",
								Toast.LENGTH_LONG).show();
						pd.dismiss();
					}
				});
			} catch (Exception e) {
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
			super.onPostExecute(result);
			pb.setVisibility(View.GONE);
			gtsa.notifyDataSetChanged();
			/*
			 * if(al.size()==0){ // if(networkTimeOut)
			 * Toast.makeText(GiftsToSend.this,
			 * "Network error.Please try again later",
			 * Toast.LENGTH_LONG).show(); else Toast.makeText(GiftsToSend.this,
			 * "No more gifts to send now for this user",
			 * Toast.LENGTH_SHORT).show(); }
			 */
			listview.setAdapter(gtsa);
			new ImageDownloadAsync().execute("Start");
		}
	}

	/*
	 * downloading images for friends
	 */
	private class ImageDownloadAsync extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// String url=params[0];
			// Log.i("",url);
			for (int i = 0; i < al.size(); i++) {
				GiftsToSendDetail gdet = al.get(i);
				URL urlimage;
				try {
					urlimage = new URL(gdet.getSmall_Pic_URL().replaceAll(" ",
							"%20"));
					Bitmap bitmap = getImageBitmap(urlimage);
					// while(bitmap==null){
					bitmap = getImageBitmap(urlimage);
					// }
					gdet.setBITMAP(bitmap);
					al.set(i, gdet);
					bitmap = null;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				publishProgress(0);
			}
			// System.gc();
			return "Done";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			gtsa.notifyDataSetChanged();
		}

		@Override
		
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			gtsa.notifyDataSetChanged();
			// listview.setAdapter(gtsa);
			
		}
	}

	/*
	 * calling webservice to fetch gifts available for friends
	 */
	public void callwebservice(String id) throws Exception {// 100001767226408
		String str = "";
		// try {
		/*
		 * HttpParams httpParams = new BasicHttpParams();
		 * HttpConnectionParams.setConnectionTimeout(httpParams,30000);
		 * HttpConnectionParams.setSoTimeout(httpParams, 30000); HttpClient
		 * client = new DefaultHttpClient(httpParams);
		 */
		// HttpPost httpPost = new
		// HttpPost("http://master.mygiftology.net/products/ws_list.json?receiver_fb_id="+id+"&rand=sumit&key=8c744f645901c282a5ef6aa3451e672c");
		/*
		 * HttpPost httpPost = new HttpPost(KeyGenerator.encodedURL(
		 * "http://master.mygiftology.net/products/ws_list.json?receiver_fb_id="
		 * +id+"&"));
		 */
		String url = GiftologyUtility.GIFTOLOGYSERVER
				+ "products/ws_list.json?receiver_fb_id=" + id + "&";
		// HttpResponse response = client.execute(httpPost);
	
		// If the response does not enclose an entity, there is no need
		
			/*
			 * InputStream instream = entity1.getContent(); BufferedReader br =
			 * new BufferedReader(new InputStreamReader(instream));
			 * StringBuilder sb = new StringBuilder(); String line; while ((line
			 * = br.readLine()) != null) { sb.append(line); }
			 */
			str = new ConnectionUtility().getGiftologyDataGet(url).toString();
			newfillArrayList(str);

			// Log.i("Giftology.Debug", str);
		
		/*
		 * } catch (Exception e) { e.printStackTrace(); }
		 */
	}

	public void newfillArrayList(String giftDataforUser) {

		JSONObject jsonObject = (JSONObject) JSONValue.parse(giftDataforUser);

		// Log.i(GiftologyUtility.TAG,
		// "In newfillArrayList"+jsonObject.getClass().getCanonicalName());

		JSONArray subjsonArray = (JSONArray) jsonObject.get("products");

		// Log.i(GiftologyUtility.TAG,
		// "Whole jSON array 1"+subjsonArray.toJSONString().substring(0,
		// subjsonArray.toJSONString().length()/4));
		GiftsToSendDetail giftsToSendDetail;
		for (Object objectx : subjsonArray) {
			 giftsToSendDetail = new GiftsToSendDetail();
			JSONObject subjsonObject = (JSONObject) objectx;
			JSONObject productSubJSON = (JSONObject) subjsonObject
					.get("Product");

			giftsToSendDetail.setID(productSubJSON.get("id").toString());
			giftsToSendDetail.setTERMSCONDITION(productSubJSON.get("terms")
					.toString().replace("\\/", "/").replace("\\r\\n", ""));
			giftsToSendDetail.setMAXPRICE(productSubJSON.get("max_price")
					.toString());
			giftsToSendDetail.setMINPRICE(productSubJSON.get("min_price")
					.toString());
			giftsToSendDetail.setMINVALUE(productSubJSON.get("min_value")
					.toString());
			giftsToSendDetail.setVALIDITY(productSubJSON.get("days_valid")
					.toString());

			JSONObject vendorSubJSON = (JSONObject) subjsonObject.get("Vendor");
			giftsToSendDetail.setABOUTPRODUCT(vendorSubJSON.get("description")
					.toString().replace("\\/", "/").replace("\\r\\n", ""));
			giftsToSendDetail.setName(vendorSubJSON.get("name").toString());
			giftsToSendDetail.setSmall_Pic_URL(GiftologyUtility.GIFTOLOGYSERVER
					+ vendorSubJSON.get("thumb_image").toString());
			giftsToSendDetail.setLarge_Pic_URL(GiftologyUtility.GIFTOLOGYSERVER
					+ vendorSubJSON.get("wide_image").toString());

			al.add(giftsToSendDetail);
		}
	}

	/*
	 * public void fillArrayList(String s){ //not good code String
	 * arr[]=s.split("Product\":"); for(int i=1;i<arr.length;i++){ boolean
	 * idSTored=false; GiftsToSendDetail gd=new GiftsToSendDetail(); String
	 * a=arr[i]; String b=a.replace("{","").replace("}","").replace("\"",
	 * "").trim(); String c=""; if(b.contains("[")){ c=b.replace("[",""); }
	 * else{ c=b; } String d=""; if(c.contains("]")){ d=c.replace("]",""); }
	 * else{ d=c; } // Log.i("Giftology.Debug",d); String
	 * arr_t1[]=d.split(",terms:"); // Log.i("Giftology.Debug",
	 * "Terms and Conditions: "+ arr_t1[1]+" 2 " );
	 * if(arr_t1[1].contains(",redeem_instr:")){ String
	 * arr_t2[]=arr_t1[1].split(",redeem_instr:"); String
	 * termsStr=arr_t2[0].split(",short_terms:")[0]; String
	 * termsString=termsStr.replace("\\/", "/").replace("\\r\\n", "");
	 * if(termsString.equals("")){ //Log.i("Giftology.Debug",
	 * "Terms and Conditions: "+termsString ); } //Log.i("Giftology.Debug",
	 * "Terms and Conditions: "+termsString );
	 * gd.setTERMSCONDITION(termsString); }
	 * 
	 * if(d.contains("description")){ String arr8[]=d.split("description:");
	 * //Log.i("Giftology.Debug",Integer.toString(arr8.length));
	 * if(arr8.length>1){
	 * 
	 * String descriptionx=arr8[1].split(",short_")[0]; String
	 * description=descriptionx.replace("\\/", "/").replace("\\r\\n", "");
	 * gd.setABOUTPRODUCT(description); //Log.i("bigImage",bigImage); } }
	 * 
	 * String arr2[]=d.split(","); for(int j=0;j<arr2.length;j++){ String
	 * e=arr2[j]; if(e.contains("id")){ String arr3[]=e.split(":");
	 * if(!idSTored){ if(arr3.length>1){ String id=arr3[1]; gd.setID(id);
	 * idSTored=true; //Log.i("id",id); } } } if(e.contains("name")){
	 * if(arr2[j-1].contains("Vendor:id")){ String arr4[]=e.split(":");
	 * if(arr4.length>1){ String name=arr4[1]; gd.setName(name);
	 * //Log.i("name",name); } } } if(e.contains("max_price")){ String
	 * arr5[]=e.split(":"); if(arr5.length>1){ String price=arr5[1];
	 * gd.setMAXPRICE(price); //Log.i("price",price); } }
	 * 
	 * if(e.contains("min_price")){ String arr5[]=e.split(":");
	 * if(arr5.length>1){ String price=arr5[1]; gd.setMINPRICE(price);
	 * //Log.i("price",price); } }
	 * 
	 * if(e.contains("min_value")){ String arr5[]=e.split(":");
	 * if(arr5.length>1){ String price=arr5[1]; gd.setMINVALUE(price);
	 * //Log.i("price",price); } }
	 * 
	 * if(e.contains("days_valid")){ String arr5[]=e.split(":");
	 * if(arr5.length>1){ String days=arr5[1]; gd.setVALIDITY(days);
	 * //Log.i("days",days); } }
	 * 
	 * if(e.contains("terms")){ String arr5[]=e.split(":"); if(arr5.length>1){
	 * String tandc=arr5[1]; gd.setTERMSCONDITION(tandc);
	 * //Log.i("price",tandc); } }
	 * 
	 * if(e.contains("thumb_image")){ String arr6[]=e.split(":");
	 * if(arr6.length>1){ String smallImage=arr6[1];
	 * gd.setSmall_Pic_URL(GiftologyUtility
	 * .GIFTOLOGYSERVER+"files/"+smallImage.substring(7));
	 * //Log.i("smallImage",smallImage); } } if(e.contains("wide_image")){
	 * String arr7[]=e.split(":"); if(arr7.length>1){ String bigImage=arr7[1];
	 * gd.setLarge_Pic_URL(GiftologyUtility.GIFTOLOGYSERVER+"files/"+bigImage.
	 * substring(7)); //Log.i("bigImage",bigImage); } }
	 * 
	 * } al.add(gd); } //Log.i("abc","def"); }
	 */
	/*public String numberToMonth(String number) {
		String month = "";
		if (number.equals("01")) {
			month = "Jan";
		}
		if (number.equals("02")) {
			month = "Feb";
		}
		if (number.equals("03")) {
			month = "Mar";
		}
		if (number.equals("04")) {
			month = "Apr";
		}
		if (number.equals("05")) {
			month = "May";
		}
		if (number.equals("06")) {
			month = "June";
		}
		if (number.equals("07")) {
			month = "July";
		}
		if (number.equals("08")) {
			month = "Aug";
		}
		if (number.equals("09")) {
			month = "Sept";
		}
		if (number.equals("10")) {
			month = "Oct";
		}
		if (number.equals("11")) {
			month = "Nov";
		}
		if (number.equals("12")) {
			month = "Dec";
		}

		return month;
	}
*/
	public static synchronized Bitmap getImageBitmap(URL url) {
		Bitmap bitmap = null;
		URLConnection connection = null;
		try {

			connection = url.openConnection();
			// connection.setDefaultUseCaches(true);
			// connection.addRequestProperty("Cache-Control",
			// "max-stale="+24*60*60);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			connection.connect();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InputStream is = null;
		try {

			is = connection.getInputStream();
			

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BufferedInputStream bis = new BufferedInputStream(is,8192);
		

		try {
			bitmap = BitmapFactory.decodeStream(bis);
			is.close();
			bis.close();
			
		} catch (Exception e) {
			bitmap = null;
		}
		
		bis = null;
		is = null;
		Runtime.getRuntime().gc();
		System.gc();
		return bitmap;
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
			if (ConnectionUtility.isInternetAvailable(getApplicationContext())) {
				new AboutUSAsync().execute("Start");
			} else {
				Toast.makeText(GiftsToSend.this, "Internet not available",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.txtLogout:
			if (ConnectionUtility.isInternetAvailable(getApplicationContext())) {
				new LogoutAsynTask().execute("Start");
			} else {
				Toast.makeText(GiftsToSend.this, "Internet not available",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.menu_settings:
			// TODO
			if(ConnectionUtility.isInternetAvailable(getApplicationContext())){
				 Intent preferenceIntent = new Intent(this, Preference.class);
		            startActivity(preferenceIntent );
		           
			}
			else{
				Toast.makeText(GiftsToSend.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;

		}
		
		return true;
	}

	private class LogoutAsynTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(GiftsToSend.this, "", "Logging Out...",
					true, false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String response = Utility.mFacebook
						.logout(getApplicationContext());
				GAUtility.trackEvent(GiftsToSend.this, "Gifts To Send",
						"Log Out", "Menu", (long) 0);
				new CachingUtility().deleteCacheFile(getApplicationContext());
				// clearApplicationData();
				// Log.i("Response",response);
				if (response.equals("true")) {
					dataSavedLogout = saveCredentials(Utility.mFacebook);
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
			if (dataSavedLogout) {
				dataSavedLogout = false;
				startActivity(new Intent(GiftsToSend.this, HomeScreen.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

				stopAlarmManagerNotificationOnLogout();
				pd.dismiss();
				finish();
			}
		}
	}

	public void stopAlarmManagerNotificationOnLogout() {
		Intent intentstop = new Intent(this, NotificationReciever.class);
		PendingIntent senderstop = PendingIntent.getBroadcast(this, 0,
				intentstop, 0);
		AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);

		alarmManagerstop.cancel(senderstop);
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService("notification");
		notificationManager.cancelAll();
	}

	public boolean saveCredentials(Facebook facebook) {
		Editor editor = getApplicationContext().getSharedPreferences(KEY,
				Context.MODE_PRIVATE).edit();
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

			final Dialog dialog = new Dialog(GiftsToSend.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.about);
			txt = (TextView) dialog.findViewById(R.id.textViewdesc);
			txt.setTypeface(tf);
			pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
			Button cancel = (Button) dialog.findViewById(R.id.cancel);
			cancel.setTypeface(tf);
			TextView heading = (TextView) dialog
					.findViewById(R.id.aboutHeading);
			heading.setTypeface(tf);

			if (HomeScreen.aboutUsText.equals("")) {
				txt.setText("");
				pb.setVisibility(View.VISIBLE);
			} else {
				txt.setText(HomeScreen.aboutUsText);
			}

			cancel.setOnClickListener(new ImageView.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});

			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			EasyTracker.getInstance().setContext(GiftsToSend.this);

			EasyTracker.getTracker().sendEvent("Menu Click",
					"Clicked About Us", "", (long) 0);
			if (HomeScreen.aboutUsText.equals("")) {
				try {
					/*
					 * HttpParams httpParams = new BasicHttpParams();
					 * HttpConnectionParams
					 * .setConnectionTimeout(httpParams,30000);
					 * HttpConnectionParams.setSoTimeout(httpParams, 30000);
					 * HttpClient client = new DefaultHttpClient(httpParams); //
					 * HttpPost httpPost = new HttpPost(
					 * "http://master.mygiftology.net/retailers/ws_about_us.json?rand=sumit&key=8c744f645901c282a5ef6aa3451e672c"
					 * ); HttpPost httpPost = new
					 * HttpPost(KeyGenerator.encodedURL
					 * ("http://master.mygiftology.net/retailers/ws_about_us.json?"
					 * ));
					 */
					String url = GiftologyUtility.GIFTOLOGYSERVER
							+ "retailers/ws_about_us.json?";

					// HttpResponse response = client.execute(httpPost);
					
					// If the response does not enclose an entity, there is no
					// need
					
						/*
						 * InputStream instream = entity1.getContent();
						 * BufferedReader br = new BufferedReader(new
						 * InputStreamReader(instream)); StringBuilder sb = new
						 * StringBuilder(); String line; while ((line =
						 * br.readLine()) != null) { sb.append(line); }
						 */
						result =new ConnectionUtility().getGiftologyDataGet(url)
								.toString();
					
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(GiftsToSend.this,
									"Network error.Please try again later",
									Toast.LENGTH_LONG).show();
						}
					});
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
			if (!result.equals("")) {
				org.json.JSONObject json;
				String about_us = null;
				try {
					json = new org.json.JSONObject(result);
					String about_us_content = json
							.getString("about_us_content");
					org.json.JSONObject json2 = new org.json.JSONObject(
							about_us_content);
					about_us = json2.getString("about_us");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				pb.setVisibility(View.GONE);
				HomeScreen.aboutUsText = Html.fromHtml(about_us).toString();
				txt.setText(HomeScreen.aboutUsText);
			} else {
				pb.setVisibility(View.GONE);
			}
		}
	}
	/*
	 * public boolean isInternetAvailable(){ ConnectivityManager connec =
	 * (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
	 * android.net.NetworkInfo wifi =
	 * connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	 * android.net.NetworkInfo mobile =
	 * connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); if
	 * (wifi.isConnected()) { return true; } else if (mobile.isConnected()) {
	 * return true; } return false; }
	 */
	/*
	 * public void clearApplicationData() { File cache = getCacheDir(); File
	 * appDir = new File(cache.getParent()); if(appDir.exists()){ String[]
	 * children = appDir.list(); for(String s : children){ if(!s.equals("lib")){
	 * deleteDir(new File(appDir, s)); Log.i("TAG",
	 * "**************** File /data/data/APP_PACKAGE/" + s
	 * +" DELETED *******************"); } } } }
	 */

	/*
	 * public static boolean deleteDir(File dir) { if (dir != null &&
	 * dir.isDirectory()) { String[] children = dir.list(); for (int i = 0; i <
	 * children.length; i++) { boolean success = deleteDir(new File(dir,
	 * children[i])); if (!success) { return false; } } } return dir.delete(); }
	 */
	
	private void enableHttpCaching() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			try {
				File httpCacheDir = new File(getApplicationContext()
						.getCacheDir(), "httpgifto");
				long httpCacheSize = 15 * 1024 * 1024; // 15 MiB
				android.net.http.HttpResponseCache.install(httpCacheDir, httpCacheSize);

			} catch (java.io.IOException e) {

			}
		} else {
			File httpCacheDir = new File(getApplicationContext().getCacheDir(),
					"httpgifto");
			try {
				com.integralblue.httpresponsecache.HttpResponseCache.install(
						httpCacheDir, 15 * 1024 * 1024);
			} catch (java.io.IOException e) {
				//Log.i(GiftologyUtility.TAG,e.toString());
			}
		}
	}

}
