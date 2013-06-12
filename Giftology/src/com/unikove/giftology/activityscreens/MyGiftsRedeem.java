package com.unikove.giftology.activityscreens;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;
import com.google.analytics.tracking.android.EasyTracker;
import com.unikove.fb.Utility;
import com.unikove.giftology.MyGiftsAdapter;
import com.unikove.giftology.R;
import com.unikove.giftology.data.MyGiftsRedeemDetail;
import com.unikove.giftology.reciever.NotificationReciever;
import com.unikove.giftology.util.CachingUtility;
import com.unikove.giftology.util.ConnectionUtility;
import com.unikove.giftology.util.GiftologyUtility;

public class MyGiftsRedeem extends Activity {
	ImageView termsAndCondition, redeeminstructions;
	public static String termsconditionString, redeeminstructionsString;
	Dialog dialog;
	Button couponCode;
	ProgressBar pb;
	MyGiftsRedeemDetail mgrd;
	ImageView image;
	TextView TandCText, RedeemText, toplabel;
	Typeface tf;
	public boolean dataSavedLogout = false;
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	ProgressDialog pd;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mygiftsredeem);
		EasyTracker.getInstance().activityStart(this);
		tf = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		termsAndCondition = (ImageView) findViewById(R.id.tandcimage);
		redeeminstructions = (ImageView) findViewById(R.id.redeeminstructionsimage);
		couponCode = (Button) findViewById(R.id.couponcode);
		couponCode.setTypeface(tf);
//		message = (Button) findViewById(R.id.message);
//		message.setTypeface(tf);
		image = (ImageView) findViewById(R.id.vendorimage);
		pb = (ProgressBar) findViewById(R.id.progressBar);
		TandCText = (TextView) findViewById(R.id.termsandconditiontext);
		TandCText.setTypeface(tf);
		RedeemText = (TextView) findViewById(R.id.redeeminstructionstext);
		RedeemText.setTypeface(tf);
		toplabel = (TextView) findViewById(R.id.toplabel);
		toplabel.setTypeface(tf);

		enableHttpCaching();
		TandCText.setText("Terms & Conditions");

		mgrd = MyGiftsAdapter.al_giftRedeem.get(0);

		couponCode.setText(mgrd.getCOUPONCODE());
		

		new ImageDownloadAsync().execute("Start");

		termsAndCondition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new Dialog(MyGiftsRedeem.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.termsandcondition);
				final TextView heading = (TextView) dialog
						.findViewById(R.id.textView);
				heading.setText("Terms & Conditions");
				final WebView webview = (WebView) dialog
						.findViewById(R.id.webView);
				EasyTracker.getTracker().sendEvent("MyGiftsRedeem",
						"Terms and Conditions Clicked", "", (long) 0);
				webview.loadDataWithBaseURL(null, mgrd.getTANDC(), "text/html",
						"UTF-8", null);

				dialog.show();

			}
		});

		redeeminstructions.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new Dialog(MyGiftsRedeem.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.termsandcondition);
				final TextView heading = (TextView) dialog
						.findViewById(R.id.textView);
				final WebView webview = (WebView) dialog
						.findViewById(R.id.webView);
				heading.setText("How to Redeem");
				EasyTracker.getTracker().sendEvent("MyGiftsRedeem",
						"How to Redeem Clicked", "", (long) 0);
				webview.loadDataWithBaseURL(null, mgrd.getREDEEMINST(),
						"text/html", "UTF-8", null);

				dialog.show();

			}
		});
	}

	private class ImageDownloadAsync extends AsyncTask<String, Integer, String> {
		Bitmap bmp = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pb = (ProgressBar) findViewById(R.id.progressBar);
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			// String url=params[0];

			URL urlimage;
			try {
				urlimage = new URL(mgrd.getLarge_Pic_URL().replaceAll(" ",
						"%20"));
				bmp = GiftsToSend.getImageBitmap(urlimage);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			System.gc();
			return "Done";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pb.setVisibility(View.GONE);
			if (!(bmp == null)) {
				Drawable d = new BitmapDrawable(getResources(), bmp);
				image.setImageDrawable(d);
			}
			
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
			if (ConnectionUtility.isInternetAvailable(getApplicationContext())) {
				new AboutUSAsync().execute("Start");
			} else {
				Toast.makeText(MyGiftsRedeem.this, "Internet not available",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.txtLogout:
			if (ConnectionUtility.isInternetAvailable(getApplicationContext())) {
				new LogoutAsynTask().execute("Start");
			} else {
				Toast.makeText(MyGiftsRedeem.this, "Internet not available",
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
				Toast.makeText(MyGiftsRedeem.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return true;
	}

	private class LogoutAsynTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(MyGiftsRedeem.this, "", "Logging Out...",
					true, false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String response = Utility.mFacebook
						.logout(getApplicationContext());
				new CachingUtility().deleteCacheFile(getApplicationContext());
				// clearApplicationData();
				//Log.i("Response", response);
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
				startActivity(new Intent(MyGiftsRedeem.this, HomeScreen.class)
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

			final Dialog dialog = new Dialog(MyGiftsRedeem.this);
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
			if (HomeScreen.aboutUsText.equals("")) {
				try {
					/*
					 * HttpParams httpParams = new BasicHttpParams();
					 * HttpConnectionParams
					 * .setConnectionTimeout(httpParams,30000);
					 * HttpConnectionParams.setSoTimeout(httpParams, 30000);
					 * HttpClient client = new DefaultHttpClient(httpParams);
					 */
					// HttpPost httpPost = new
					// HttpPost("http://master.mygiftology.net/retailers/ws_about_us.json?rand=sumit&key=8c744f645901c282a5ef6aa3451e672c");
					// HttpPost httpPost = new
					// HttpPost(KeyGenerator.encodedURL("http://master.mygiftology.net/retailers/ws_about_us.json?"));
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
						result = new ConnectionUtility().getGiftologyDataGet(url)
								.toString();
					
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MyGiftsRedeem.this,
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
				JSONObject json;
				String about_us = null;
				try {
					json = new JSONObject(result);
					String about_us_content = json
							.getString("about_us_content");
					JSONObject json2 = new JSONObject(about_us_content);
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
	 * public boolean isInternetAvailable(){ try { ConnectivityManager connec =
	 * (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
	 * android.net.NetworkInfo wifi =
	 * connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	 * android.net.NetworkInfo mobile =
	 * connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); if
	 * (wifi.isConnected()) { return true; } else if (mobile.isConnected()) {
	 * return true; } return false; } catch (Exception e) { // TODO
	 * Auto-generated catch block return false; } }
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
			//	Log.i(GiftologyUtility.TAG,e.toString());
			}
		}
	}

}
