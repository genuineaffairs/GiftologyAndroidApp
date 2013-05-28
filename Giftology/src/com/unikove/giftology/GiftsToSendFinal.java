package com.unikove.giftology;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;
import com.google.analytics.tracking.android.EasyTracker;
import com.unikove.fb.Utility;
import com.unikove.giftology.reciever.NotificationReciever;

public class GiftsToSendFinal extends Activity{
	TextView sendTimetxt,price,warningTxt;
	Typeface tf;
	RelativeLayout image;
	GiftsToSendDetail gd;
	Button sendTo;
	ProgressBar pb;
	String name="";
	public static String messageToShare,termsconditionString;
	Dialog dialog,dialog1,dialogSelectTime,dialogSendMsg;
	ProgressDialog pd;
	ImageView termsAndCondition;
	TextView tandCText;
	public static String sendNow,receiverBday,dateToSend;
	public boolean dataSavedLogout=false;
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.giftstosendfinal);
		GAUtility.trackView(GiftsToSendFinal.this, "GifToSendFinal");
		//EasyTracker.getInstance().activityStart(this);
		tf=Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		sendTimetxt=(TextView)findViewById(R.id.textView01);sendTimetxt.setTypeface(tf);
		price=(TextView)findViewById(R.id.price);price.setTypeface(tf);
		warningTxt=(TextView)findViewById(R.id.warningText);warningTxt.setTypeface(tf);
		image = (RelativeLayout)findViewById(R.id.rel_inner);
		sendTo=(Button)findViewById(R.id.sendButton);sendTo.setTypeface(tf);
		termsAndCondition=(ImageView)findViewById(R.id.tandcimage);
		tandCText=(TextView)findViewById(R.id.termsandconditiontext);

		tandCText.setText("Terms & Conditions");

		gd=GiftsToSendAdapter.giftdet;

		new ImageDownloadAsync().execute("Start");

		if(price!=null && gd!=null){
			price.setText(gd.getMINVALUE());
		}

		String[] name_arr=EventsScreen.personClickedName.split(" ");

		if(name_arr[0].length()<=2){
			name=name_arr[0]+" "+name_arr[1];
		}
		else{
			name=name_arr[0];
		}
		sendTo.setText("Send to "+name);
		warningTxt.setText("This gift card is valid for "+gd.getVALIDITY()+" days.");

		sendTo.setOnClickListener(new ImageView.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					GAUtility.trackEvent(GiftsToSendFinal.this, "Gifts To Send Final", "Sent to friend button clicked", "Friend Selected", (long)0);
				
					showAlertSelectTime();
				}
				else{
					Toast.makeText(GiftsToSendFinal.this, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});

		termsAndCondition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new Dialog(GiftsToSendFinal.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.termsandcondition);
				final WebView webview=(WebView)dialog.findViewById(R.id.webView);
				final TextView heading=(TextView)dialog.findViewById(R.id.textView);
				heading.setText("Terms & Conditions");
				webview.loadDataWithBaseURL(null, GiftsToSendFinal.termsconditionString, "text/html", "UTF-8", null);

				dialog.show();

			}
		});
	}
	/*
	 * showing popup to select time of sending gift
	 */
	public void showAlertSelectTime(){
		dialogSelectTime = new Dialog(GiftsToSendFinal.this);
		dialogSelectTime.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSelectTime.setContentView(R.layout.giftsendschedulepopup);
		final TextView heading=(TextView)dialogSelectTime.findViewById(R.id.textView);heading.setTypeface(tf);
		final TextView texta=(TextView)dialogSelectTime.findViewById(R.id.textView1);texta.setTypeface(tf);
		final TextView textb=(TextView)dialogSelectTime.findViewById(R.id.textView2);textb.setTypeface(tf);
		final TextView textc=(TextView)dialogSelectTime.findViewById(R.id.textView3);textc.setTypeface(tf);
		final CheckBox sendnow=(CheckBox)dialogSelectTime.findViewById(R.id.checkBox1);sendnow.setChecked(false);
		final CheckBox senditlater=(CheckBox)dialogSelectTime.findViewById(R.id.checkBox2);senditlater.setChecked(false);
		final CheckBox sendonbirthday=(CheckBox)dialogSelectTime.findViewById(R.id.checkBox3);sendonbirthday.setChecked(false);
		final DatePicker datePicker=(DatePicker)dialogSelectTime.findViewById(R.id.datePicker);datePicker.setVisibility(View.GONE);
		final Button done=(Button)dialogSelectTime.findViewById(R.id.done);done.setTypeface(tf);
		final Button cancel=(Button)dialogSelectTime.findViewById(R.id.cancel);cancel.setTypeface(tf);
		
		//not working when making checkbox checked in xml.
		sendnow.setChecked(true);

		sendnow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(sendnow.isChecked()){
					sendnow.setClickable(false);
					senditlater.setClickable(true);
					sendonbirthday.setClickable(true);
					senditlater.setChecked(false);
					sendonbirthday.setChecked(false);
					datePicker.setVisibility(View.GONE);
				}
			}
		});
		senditlater.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(senditlater.isChecked()){
					sendnow.setClickable(true);
					senditlater.setClickable(false);
					sendonbirthday.setClickable(true);
					sendnow.setChecked(false);
					sendonbirthday.setChecked(false);
					datePicker.setVisibility(View.VISIBLE);
				}
			}
		});
		sendonbirthday.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(sendonbirthday.isChecked()){
					sendnow.setClickable(true);
					senditlater.setClickable(true);
					sendonbirthday.setClickable(false);
					sendnow.setChecked(false);
					senditlater.setChecked(false);
					datePicker.setVisibility(View.GONE);
				}
			}
		});
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				GAUtility.trackEvent(GiftsToSendFinal.this, "Gifts To Send Final", "Gifts to send final, Done button in alert clicked", "Friend Selected", (long)0);
				
				if(isInternetAvailable()){
					boolean flag=true;
					if(sendnow.isChecked()||senditlater.isChecked()||sendonbirthday.isChecked()){
						if(sendnow.isChecked()){
							sendNow="1";
							receiverBday="";
							dateToSend="";
						}
						else if(senditlater.isChecked()){
							sendNow="0";
							receiverBday="";
							
							
							   
							dateToSend=datePicker.getYear()+"/"+padding(datePicker.getMonth()+1)+"/"+padding(datePicker.getDayOfMonth());
							String str_date=dateToSend;
							DateFormat formatter ; 
							Date date ; 
							   formatter = new SimpleDateFormat("yyyy/MM/dd");
							   try {
								date = formatter.parse(str_date);
								
								Date currentDate=Calendar.getInstance().getTime();
								
								//Log.i("Giftology.Debug",date.toString());
								//Log.i("Giftology.Debug",currentDate.toString());
								if(!currentDate.before(date))
								{
									flag=false;
									 Toast.makeText(GiftsToSendFinal.this, "Please select a date after current date", Toast.LENGTH_SHORT).show();
								//	int currentYear=Calendar.getInstance().getTime().
								
								}
								 Calendar now = Calendar.getInstance();   // This gets the current date and time.
							     now.get(Calendar.YEAR);                 // This returns the year as an int.  
								 //Log.i("Giftology.Debug",Integer.toString(now.get(Calendar.YEAR)));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								//Log.i("Giftology.Debug","CRASH!!!SKADOOSH!");
							}
							
						}
						else if(sendonbirthday.isChecked()){
							sendNow="0";
							dateToSend="";
							String bday=EventsScreen.personClickedDOB;
							if((bday!=null)||(!bday.equals(""))){
								String bdayArr[]=bday.split("/");
								if(bdayArr.length==3){
									receiverBday=bdayArr[2]+"/"+bdayArr[0]+"/"+bdayArr[1];
								}
								else if(bdayArr.length==2){
									 Calendar now = Calendar.getInstance();   // This gets the current date and time.
								     now.get(Calendar.YEAR);                 // This returns the year as an int.  
									 //Log.i("Giftology.Debug",Integer.toString(now.get(Calendar.YEAR)));
									receiverBday=Integer.toString(now.get(Calendar.YEAR))+"/"+bdayArr[0]+"/"+bdayArr[1];
								}
							}
						}
						if(flag)
						{
						dialogSelectTime.cancel();
						sendMessagePopUp();
						}
					}
					else{
						Toast.makeText(GiftsToSendFinal.this, "Please select one item", Toast.LENGTH_SHORT).show();
					}
				}
				else{
					Toast.makeText(GiftsToSendFinal.this, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				GAUtility.trackEvent(GiftsToSendFinal.this,"Gifts to send final", "Cancel button in alert clicked","",  (long) 0);
				dialogSelectTime.cancel();
				
			}
		});

		dialogSelectTime.show();
	}
	
	
	
	public String randomMessageGenerator()
	{
		String[] messageList={"I saw this gift and couldn't help getting it for you.","Here’s a gift just for being the best!","I hope this gift makes you feel as great as you are."};

		Random randomGenerator=new Random();
		int number=randomGenerator.nextInt(3);
		return messageList[number];
	}
	/*
	 * showing pop up to write msg for sending
	 */
	
	
	public void sendMessagePopUp(){
		dialogSendMsg = new Dialog(GiftsToSendFinal.this);
		dialogSendMsg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSendMsg.setContentView(R.layout.sendmessage);
		final TextView heading=(TextView)dialogSendMsg.findViewById(R.id.textView);heading.setTypeface(tf);
		final EditText editText=(EditText)dialogSendMsg.findViewById(R.id.editText);editText.setTypeface(tf);
		Button done=(Button)dialogSendMsg.findViewById(R.id.done);done.setTypeface(tf);
		Button cancel=(Button)dialogSendMsg.findViewById(R.id.cancel);cancel.setTypeface(tf);
		editText.setHint(randomMessageGenerator()+"...");
		done.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					GiftsToSendFinal.messageToShare="";
					if(editText.getText().toString().isEmpty())
					{
						messageToShare=editText.getHint().toString();
					}else
					{
						messageToShare=editText.getText().toString();
						
					}
					if(!messageToShare.equals("")){
						dialogSendMsg.cancel();
						new SendGiftAsync().execute("Start");
					}
					else{
						editText.setHint("Please write somthing nice...");
						
					}
					//						new PostTask().execute("Start");
				}
				else{
					Toast.makeText(GiftsToSendFinal.this, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancel.setOnClickListener(new ImageView.OnClickListener(){
			@Override
			public void onClick(View v) {
				dialogSendMsg.cancel();
			}
		});

		dialogSendMsg.show();
	}
	/*
	 * webservice calling to send gift
	 */
	private class SendGiftAsync extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(sendNow.equals("1")){
				pd = ProgressDialog.show(GiftsToSendFinal.this, "", "Sending Gift...", true,false);
			}
			if(sendNow.equals("0")){
				pd = ProgressDialog.show(GiftsToSendFinal.this, "", "Scheduling Gift...", true,false);
			}
		}
		@Override
		protected String doInBackground(String... params) {
			String result="";
			String url=params[0];
			
			try {
				if(HomeScreen.prefs==null){
					HomeScreen.prefs = getApplicationContext().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
				}
				if(HomeScreen.prefs.getBoolean("isUserRegistered", true)){
					try {
						
						result=callwebservice(HomeScreen.prefs.getString("userID", ""), EventsScreen.personClickedID, HomeScreen.prefs.getString("productID", ""));
					}
					catch(java.net.SocketTimeoutException e){
						runOnUiThread(new Runnable() {
						@Override
							public void run() {
								Toast.makeText(GiftsToSendFinal.this, "Network error.Please try again later", Toast.LENGTH_LONG).show();
								pd.dismiss();
						}
						});
					}
					catch (Exception e) {
						
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
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
			if(result.equals("1")){
				//				new PostTask().execute("Start");
				if(sendNow.equals("1")){
					Toast.makeText(GiftsToSendFinal.this, "Gift successfully sent!",Toast.LENGTH_LONG).show();
				}
				if(sendNow.equals("0")){
					Toast.makeText(GiftsToSendFinal.this, "Your gift is scheduled to be sent.",Toast.LENGTH_LONG).show();
				}
				startActivity(new Intent(GiftsToSendFinal.this,ShareTheNews.class));
				overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
			}
			else if(result.equals("2")){
				Toast.makeText(GiftsToSendFinal.this, "Ooops, our bad ! Seems like we ran out of gift vouchers for this vendor.  Will you select another vendor ?",Toast.LENGTH_LONG).show();
				finish();
			}
			else{
				Toast.makeText(GiftsToSendFinal.this, "Ooops.. Gift not sent. Please try again",Toast.LENGTH_LONG).show();
				startActivity(new Intent(GiftsToSendFinal.this,GiftsToSendFinal.class));
				overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
				finish();
			}
		}
	}
	/*
	 * webservice hit to download image of gift
	 */
	private class ImageDownloadAsync extends AsyncTask<String, Integer, String> {
		Bitmap bmp=null;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pb=(ProgressBar)findViewById(R.id.progressBar);
			pb.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(String... params) {
			String url=params[0];

			URL urlimage;
			try {
				urlimage = new URL(gd.getLarge_Pic_URL().replaceAll(" ","%20"));
				bmp=GiftsToSend.getImageBitmap(urlimage);

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
			if(!(bmp==null)){
				Drawable d = new BitmapDrawable(getResources(),bmp);
				image.setBackgroundDrawable(d);
			}
		}
	}
	/*
	 * calling webservice to send gift
	 */
	public String callwebservice(String senderId,String receiverFBId,String productId) throws Exception{//100001767226408
		String giftsend="0";
		String str="";
		String url="";
		//		try {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,30000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		HttpClient client = new DefaultHttpClient(httpParams);
		String senderRegistrationId=HomeScreen.prefs.getString("userID", "");
		if(sendNow.equals("1")){
			url=KeyGenerator.encodedURL("http://giftology.com/gifts/ws_send.json?sender_id="+senderRegistrationId+"&receiver_fb_id="+receiverFBId+"&product_id="+productId+
			"&gift_amount="+gd.getMINVALUE()+"&gift_message="+GiftsToSendFinal.messageToShare+"&post_to_fb=1&send_now=1"+"&");
		}
		else if(sendNow.equals("0")){
			if(!receiverBday.equals("")){
				url=KeyGenerator.encodedURL("http://giftology.com/gifts/ws_send.json?sender_id="+senderRegistrationId+"&receiver_fb_id="+receiverFBId+"&product_id="+productId+
				"&gift_amount="+gd.getMINVALUE()+"&gift_message="+GiftsToSendFinal.messageToShare+"&post_to_fb=1&send_now=0"+
				"&receiver_birthday="+receiverBday+"&");
			}
			else if(!dateToSend.equals("")){
				url=KeyGenerator.encodedURL("http://giftology.com/gifts/ws_send.json?sender_id="+senderRegistrationId+"&receiver_fb_id="+receiverFBId+"&product_id="+productId+
				"&gift_amount="+gd.getMINVALUE()+"&gift_message="+GiftsToSendFinal.messageToShare+"&post_to_fb=1&send_now=0"+
				"&date_to_send="+dateToSend+"&");
			}
		}
		if(url.contains(" ")){
			url=url.replaceAll(" ","%20");
		}
		
		HttpPost httpPost = new HttpPost(url);
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
			str=sb.toString();//{"gifts":{"result":"1"}}
			//{"gifts":{"error":{"9":"Ooops, our bad ! Seems like we ran out of gift vouchers for this vendor.  Will you select another vendor ?"}}}
			if(str.contains("{\"gifts\":{\"result\":\"1\"}}")){
				giftsend="1";
			}
			else if (str.contains("ran out of gift vouchers")) {
				giftsend="2";
			}
			Log.i("","response  xml file:" +sb.toString()); 
		}
		
		return giftsend;
	}
	private class PostTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(GiftsToSendFinal.this, "", "Posting on "+EventsScreen.personClickedName+" Wall...");
		}

		@Override
		protected String doInBackground(String... params) {
			String url=params[0];
			posttofriendswall(EventsScreen.personClickedID,GiftsToSendFinal.messageToShare, "Wall Post");
			return "abc";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//			GiftsToSendFinal.messageToShare="";
			pd.dismiss();
			startActivity(new Intent(GiftsToSendFinal.this,ShareTheNews.class));
			overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
		}
	}
	public void posttofriendswall(String id,String msg,String description){

		String response;
		try {
			response = Utility.mFacebook.request(id);
			Bundle parameters = new Bundle();
			parameters.putString("message", msg);
			response = Utility.mFacebook.request(id+"/feed", parameters, "POST");
			Log.d("FACEBOOK RESPONSE",response);
			if (response == null || response.equals("") || response.equals("false")) {
				showToast("Message Not Posted...");
			}
			else{
				showToast("Message Posted on "+EventsScreen.personClickedName+" wall...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public void showToast(final String message){
		Toast.makeText(GiftsToSendFinal.this, message, Toast.LENGTH_SHORT).show();
	}
	public String padding(int a){
		String input=""+a;
		String output="";
		if(input.length()==1){
			output="0"+input;
		}
		else{
			output=input;
		}
		return output;
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
				Toast.makeText(GiftsToSendFinal.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.txtLogout:
			if(isInternetAvailable()){
				new LogoutAsynTask().execute("Start");
			}
			else{
				Toast.makeText(GiftsToSendFinal.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;

		}
		return true;
	}

	private class LogoutAsynTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=ProgressDialog.show(GiftsToSendFinal.this,"","Logging Out...",true,false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				
				
				String response=Utility.mFacebook.logout(getApplicationContext());
				GAUtility.trackEvent(GiftsToSendFinal.this, "Gifts To Send Final", "Log Out", "Menu", (long)0);
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
				startActivity(new Intent(GiftsToSendFinal.this,HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

			final Dialog dialog = new Dialog(GiftsToSendFinal.this);
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
			EasyTracker.getInstance().setContext(GiftsToSendFinal.this);
			EasyTracker.getTracker().sendEvent("Menu Click", "Clicked ABout Us", "", (long) 0);
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
							Toast.makeText(GiftsToSendFinal.this, "Network error.Please try again later", Toast.LENGTH_LONG).show();
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
