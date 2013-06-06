package com.unikove.giftology;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unikove.fb.Utility;
import com.unikove.giftology.activityscreens.EventsScreen;
import com.unikove.giftology.activityscreens.GiftsToSend;
import com.unikove.giftology.activityscreens.HomeScreen;
import com.unikove.giftology.activityscreens.ShareTheNews;
import com.unikove.giftology.connectivity.ConnectionUtility;
import com.unikove.giftology.data.GiftsToSendDetail;
import com.unikove.giftology.util.GiftologyUtility;

public class GiftsToSendFinalPrev extends Activity {
	TextView sendTimetxt, price, warningTxt;
	Typeface tf;
	RelativeLayout image;
	GiftsToSendDetail gd;
	Button sendTo;
	ProgressBar pb;
	String name = "";
	public static String messageToShare, termsconditionString;
	Dialog dialog, dialog1;
	ProgressDialog pd;
	ImageView termsAndCondition;
	TextView tandCText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.giftstosendfinal);

		tf = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		sendTimetxt = (TextView) findViewById(R.id.textView01);
		sendTimetxt.setTypeface(tf);
		price = (TextView) findViewById(R.id.price);
		price.setTypeface(tf);
		warningTxt = (TextView) findViewById(R.id.warningText);
		warningTxt.setTypeface(tf);
		image = (RelativeLayout) findViewById(R.id.rel_inner);
		sendTo = (Button) findViewById(R.id.sendButton);
		sendTo.setTypeface(tf);
		termsAndCondition = (ImageView) findViewById(R.id.tandcimage);
		tandCText = (TextView) findViewById(R.id.termsandconditiontext);

		tandCText.setText("Terms & Conditions");

		gd = GiftsToSendAdapter.giftdet;

		new ImageDownloadAsync().execute("Start");

		price.setText(gd.getMINVALUE());

		String[] name_arr = EventsScreen.personClickedName.split(" ");

		if (name_arr[0].length() <= 2) {
			name = name_arr[0] + " " + name_arr[1];
		} else {
			name = name_arr[0];
		}
		sendTo.setText("Send to " + name);
		warningTxt.setText("This gift card is valid for " + gd.getVALIDITY()
				+ " days.");

		sendTo.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new Dialog(GiftsToSendFinalPrev.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.sendmessage);
				final EditText editText = (EditText) dialog
						.findViewById(R.id.editText);
				Button done = (Button) dialog.findViewById(R.id.done);
				Button cancel = (Button) dialog.findViewById(R.id.cancel);
				editText.setHint("Write something nice to " + name + "...");
				done.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						GiftsToSendFinalPrev.messageToShare = "";
						messageToShare = editText.getText().toString();
						if (!messageToShare.equals("")) {
							new SendGiftAsync().execute("Start");
						} else {
							editText.setHint("Please write somthing nice...");
						}
						// new PostTask().execute("Start");
					}
				});
				cancel.setOnClickListener(new ImageView.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});

				dialog.show();
			}
		});

		termsAndCondition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new Dialog(GiftsToSendFinalPrev.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.termsandcondition);
				final WebView webview = (WebView) dialog
						.findViewById(R.id.webView);
				final TextView heading = (TextView) dialog
						.findViewById(R.id.textView);
				heading.setText("Terms & Conditions");
				webview.loadDataWithBaseURL(null,
						GiftsToSendFinalPrev.termsconditionString, "text/html",
						"UTF-8", null);

				dialog.show();

			}
		});
	}

	private class SendGiftAsync extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.cancel();
			pd = ProgressDialog.show(GiftsToSendFinalPrev.this, "",
					"Sending Gift...", true, false);
		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			// String url=params[0];
			if (HomeScreen.prefs == null) {
				HomeScreen.prefs = getApplicationContext()
						.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
			}
			if (HomeScreen.prefs.getBoolean("isUserRegistered", true)) {
				result = callwebservice(
						HomeScreen.prefs.getString("userID", ""),
						EventsScreen.personClickedID,
						HomeScreen.prefs.getString("productID", ""));
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
			if (result.equals("1")) {
				// new PostTask().execute("Start");
				Toast.makeText(GiftsToSendFinalPrev.this,
						"Gift Successfully Sent!", Toast.LENGTH_LONG).show();
				startActivity(new Intent(GiftsToSendFinalPrev.this,
						ShareTheNews.class));
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			} else {
				Toast.makeText(GiftsToSendFinalPrev.this,
						"Ooops.. Gift not sent. Please try again",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(GiftsToSendFinalPrev.this,
						GiftsToSendFinalPrev.class));
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		}
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
				urlimage = new URL(gd.getLarge_Pic_URL());
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
				image.setBackgroundDrawable(d);
			}
		}
	}

	public String callwebservice(String senderId, String receiverFBId,
			String productId) {// 100001767226408
		String giftsend = "0";
		String str = "";
		try {
			// HttpParams httpParams = new BasicHttpParams();
			// HttpConnectionParams.setConnectionTimeout(httpParams,300000);
			// HttpConnectionParams.setSoTimeout(httpParams, 300000);
			// HttpClient client = new DefaultHttpClient(httpParams);
			String senderRegistrationId = HomeScreen.prefs.getString("userID",
					"");
			// String
			// x=GiftologyUtility.GIFTOLOGYSERVER+"gifts/ws_send.json?sender_id="+senderRegistrationId+"&receiver_fb_id="+receiverFBId+"&product_id="+productId+"&gift_amount="+gd.getMINVALUE()+"&gift_message="+GiftsToSendFinalPrev.messageToShare+"&post_to_fb=1"+"&rand=sumit&key=8c744f645901c282a5ef6aa3451e672c";
			// String
			// x="http://master.mygiftology.net/gifts/ws_send.json?sender_id=15&receiver_fb_id=1822033405&product_id=91&gift_amount=500&gift_message=hi&post_to_fb=1&rand=sumit&key=8c744f645901c282a5ef6aa3451e672c";

			String url = GiftologyUtility.GIFTOLOGYSERVER
					+ "gifts/ws_send.json?sender_id=" + senderRegistrationId
					+ "&receiver_fb_id=" + receiverFBId + "&product_id="
					+ productId + "&gift_amount=" + gd.getMINVALUE()
					+ "&gift_message=" + GiftsToSendFinalPrev.messageToShare
					+ "&post_to_fb=1"
					+ "&rand=sumit&key=8c744f645901c282a5ef6aa3451e672c";
			
				/*
				 * InputStream instream = entity1.getContent(); BufferedReader
				 * br = new BufferedReader(new InputStreamReader(instream));
				 * StringBuilder sb = new StringBuilder(); String line; while
				 * ((line = br.readLine()) != null) { sb.append(line); }
				 */
				str = ConnectionUtility.getGiftologyDataGet(url).toString();// {"gifts":{"result":"1"}}
				if (str.contains("1")) {
					giftsend = "1";
				}
				// Log.i("","response  xml file:" +sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			giftsend = "0";
		}
		return giftsend;
	}

	private class PostTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog
					.show(GiftsToSendFinalPrev.this, "", "Posting on "
							+ EventsScreen.personClickedName + " Wall...");
		}

		@Override
		protected String doInBackground(String... params) {
			// String url=params[0];
			posttofriendswall(EventsScreen.personClickedID,
					GiftsToSendFinalPrev.messageToShare, "Wall Post");
			return "abc";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// GiftsToSendFinal.messageToShare="";
			pd.dismiss();
			startActivity(new Intent(GiftsToSendFinalPrev.this,
					ShareTheNews.class));
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		}
	}

	public void posttofriendswall(String id, String msg, String description) {

		String response;
		try {
			response = Utility.mFacebook.request(id);
			Bundle parameters = new Bundle();
			parameters.putString("message", msg);
			response = Utility.mFacebook.request(id + "/feed", parameters,
					"POST");
			Log.d("FACEBOOK RESPONSE", response);
			if (response == null || response.equals("")
					|| response.equals("false")) {
				showToast("Message Not Posted...");
			} else {
				showToast("Message Posted on " + EventsScreen.personClickedName
						+ " wall...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showToast(final String message) {
		Toast.makeText(GiftsToSendFinalPrev.this, message, Toast.LENGTH_SHORT)
				.show();
	}
}
