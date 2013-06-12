package com.unikove.giftology;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unikove.fb.Utility;
import com.unikove.giftology.activityscreens.MyGiftsRedeem;
import com.unikove.giftology.data.MyGiftsDetail;
import com.unikove.giftology.data.MyGiftsRedeemDetail;
import com.unikove.giftology.util.ConnectionUtility;
import com.unikove.giftology.util.GiftologyUtility;

public class MyGiftsAdapter extends ArrayAdapter<MyGiftsDetail> {
	private final Activity context;
	ArrayList<MyGiftsDetail> al;
	public static ArrayList<MyGiftsRedeemDetail> al_giftRedeem = new ArrayList<MyGiftsRedeemDetail>();
	Typeface tf;
	public static MyGiftsDetail giftdet;
	ProgressDialog pd;
	String giftID;

	public MyGiftsAdapter(Activity context, ArrayList<MyGiftsDetail> description) {
		super(context, R.layout.mygifts_rowitem, description);
		this.context = context;
		this.al = description;

		tf = Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.mygifts_rowitem, null, true);
		final TextView name = (TextView) rowView.findViewById(R.id.name);
		name.setTypeface(tf);
		final TextView price = (TextView) rowView.findViewById(R.id.price);
		price.setTypeface(tf);
		final RelativeLayout image = (RelativeLayout) rowView
				.findViewById(R.id.rel_inner);
		final TextView expiry = (TextView) rowView.findViewById(R.id.expiry);
		expiry.setTypeface(tf);

		final MyGiftsDetail gd = al.get(position);
		name.setText(gd.getName());
		price.setText(gd.getPRICE());
		expiry.setText("Expires " + gd.getEXPIRY());
		if (!(gd.getBITMAP() == null)) {
			Drawable d = new BitmapDrawable(context.getResources(),
					gd.getBITMAP());
			image.setBackgroundDrawable(d);
		} else {
			image.setBackgroundResource(R.drawable.thumbnail);
		}

		rowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ConnectionUtility.isInternetAvailable(context)) {
					giftID = gd.getID();
					new LoadGiftsTask().execute("Start");
				} else {
					Toast.makeText(context, "Internet not available",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		return rowView;
	}

	private class LoadGiftsTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(context, "", "Loading...", true, false);
			enableHttpCaching(context);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			try {
				JSONObject jObject = new JSONObject(
						Utility.mFacebook.request("me"));
				callWebservice(jObject.getString("id"));
				result = "done";
			} catch (java.net.SocketTimeoutException e) {
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context,
								"Network error.Please try again later",
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
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
			pd.dismiss();
			if (!result.equals("")) {
				// EasyTracker.getTracker().sendEvent("MyGifts",
				// "Gifts Selected to Redeem", "", (long) 0);
				context.startActivity(new Intent(context, MyGiftsRedeem.class));
				context.overridePendingTransition(R.anim.zoom_enter,
						R.anim.zoom_exit);
			}
		}
	}

	public void callWebservice(String receiverID) throws Exception {// 100002950256522
		String str = "";
		// try {
		/*
		 * HttpParams httpParams = new BasicHttpParams();
		 * HttpConnectionParams.setConnectionTimeout(httpParams,30000);
		 * HttpConnectionParams.setSoTimeout(httpParams, 30000); HttpClient
		 * client = new DefaultHttpClient(httpParams);
		 */// ResponseCache.setDefault(new GiftologyFBCache());
		// HttpPost httpPost = new
		// HttpPost("http://master.mygiftology.net/gifts/ws_redeem.json?rand=sumit&key=8c744f645901c282a5ef6aa3451e672c&receiver_fb_id="+receiverID+"&gift_id="+giftID);
		// HttpPost httpPost = new
		// HttpPost(KeyGenerator.encodedURL("http://master.mygiftology.net/gifts/ws_redeem.json?")+"&receiver_fb_id="+receiverID+"&gift_id="+giftID);
		String url = GiftologyUtility.GIFTOLOGYSERVER
				+ "gifts/ws_redeem.json?rand=sumit&key=8c744f645901c282a5ef6aa3451e672c"
				+ "&receiver_fb_id=" + receiverID + "&gift_id=" + giftID;
		// HttpResponse response = client.execute(httpPost);
		// Log.i("Giftology.Debug","Before fillarraylist "+url);

		
		// If the response does not enclose an entity, there is no need
		
			/*
			 * InputStream instream = entity1.getContent(); BufferedReader br =
			 * new BufferedReader(new InputStreamReader(instream));
			 * StringBuilder sb = new StringBuilder(); String line; while ((line
			 * = br.readLine()) != null) { sb.append(line); }
			 */
			str = new ConnectionUtility().getGiftologyDataGetKeyLess(url).toString();

			fillArrayList(str);
			// Log.i("Giftology.Debug","Before fillarraylist "+str);
		
		/*
		 * } catch (Exception e) { e.printStackTrace(); }
		 */
	}

	public void fillArrayList(String s) {
		al_giftRedeem.clear();
		try {
			// Log.i("Giftology.Debug","In json -1");
			JSONObject jsonObject = new JSONObject(s);
			JSONObject jsonObjectElement = jsonObject.getJSONObject("gift");
			// Log.i("Giftology.Debug","In json 0");
			MyGiftsRedeemDetail mgd = new MyGiftsRedeemDetail();

			JSONObject jsonObjectGifts = jsonObjectElement
					.getJSONObject("Gift");
			mgd.setCOUPONCODE(jsonObjectGifts.getString("code"));
			mgd.setMESSAGE(jsonObjectGifts.getString("gift_message"));
			// Log.i("Giftology.Debug","In json 1");
			JSONObject jsonProduct = jsonObjectElement.getJSONObject("Product");
			mgd.setTANDC(jsonProduct.getString("terms"));
			mgd.setREDEEMINST(jsonProduct.getString("redeem_instr"));
			// Log.i("Giftology.Debug","In json 2");
			JSONObject jsonVendor = jsonProduct.getJSONObject("Vendor");
			mgd.setSmall_Pic_URL(GiftologyUtility.GIFTOLOGYSERVER
					+ jsonVendor.getString("thumb_image"));
			mgd.setLarge_Pic_URL(GiftologyUtility.GIFTOLOGYSERVER
					+ jsonVendor.getString("wide_image"));

			// Log.i("Giftology.Debug","In json 3");
			JSONObject jsonSender = jsonObjectElement.getJSONObject("Sender");
			mgd.setFROM(jsonSender.getString("username"));
			// Log.i("Giftology.Debug","In json 4");
			al_giftRedeem.add(mgd);

		} catch (JSONException e1) {
			// Log.i("Giftology.Debug","In json exception" +e1);
			e1.printStackTrace();
		}
	}
	/*
	 * public boolean isInternetAvailable(){ ConnectivityManager connec =
	 * (ConnectivityManager)
	 * context.getSystemService(context.CONNECTIVITY_SERVICE);
	 * android.net.NetworkInfo wifi =
	 * connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	 * android.net.NetworkInfo mobile =
	 * connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); if
	 * (wifi.isConnected()) { return true; } else if (mobile.isConnected()) {
	 * return true; } return false; }
	 */
	
	private void enableHttpCaching(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			try {
				File httpCacheDir = new File(context
						.getCacheDir(), "httpgifto");
				long httpCacheSize = 15 * 1024 * 1024; // 15 MiB
				android.net.http.HttpResponseCache.install(httpCacheDir, httpCacheSize);

			} catch (java.io.IOException e) {

			}
		} else {
			File httpCacheDir = new File(context.getCacheDir(),
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
