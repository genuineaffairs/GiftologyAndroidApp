package com.unikove.giftology;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.SSLSessionCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.unikove.fb.BaseDialogListener;
import com.unikove.fb.BaseRequestListener;
import com.unikove.fb.FriendsGetProfilePics;
import com.unikove.fb.SessionStore;
import com.unikove.fb.Utility;
import com.unikove.giftology.reciever.NotificationReciever;

public class EventsScreen extends Activity implements OnItemClickListener,Runnable {
	protected static JSONArray jsonArray;
	String status,dec_or_jan="";
	String date_today,date_tomorrow;
	private ProgressDialog pd;
	public boolean friendListFetched=false;
	public static String apiResponse="";
	ArrayList<FriendDetail> al=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> al_today=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> al_tomorrow=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> al_this_week=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> al_this_month=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> al_next_month=new ArrayList<FriendDetail>();
	ArrayList<String> dob_al_today=new ArrayList<String>();
	ArrayList<String> dob_al_tomorrow=new ArrayList<String>();
	ArrayList<String> dob_al_this_week=new ArrayList<String>();
	ArrayList<String> dob_al_this_month=new ArrayList<String>();
	ArrayList<String> dob_al_next_month=new ArrayList<String>();
	ArrayList<FriendDetail> dec_al_today=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> dec_al_tomorrow=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> dec_al_this_week=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> dec_al_this_month=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> dec_al_next_month=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> jan_al_today=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> jan_al_tomorrow=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> jan_al_this_week=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> jan_al_this_month=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail> jan_al_next_month=new ArrayList<FriendDetail>();
	ArrayList<String>  name_al=new ArrayList<String>();
	ArrayList<String>  dob_al=new ArrayList<String>();
	ArrayList<FriendDetail>  dec_al=new ArrayList<FriendDetail>();
	ArrayList<FriendDetail>  jan_al=new ArrayList<FriendDetail>();
	Calendar crntDate = Calendar.getInstance();
	SimpleDateFormat frmter= new SimpleDateFormat("MM/dd/yyyy");
	String crnt_date = frmter.format(crntDate.getTime());
	GridView today,tomorrow,this_week,this_month,next_month;
	//ImageView more_options;
	RelativeLayout popup;
	RelativeLayout today_lin,tomorrow_lin,this_week_lin,this_month_lin,next_month_lin;
	LinearLayout lin_grid1,lin_grid2,lin_grid3,lin_grid4,lin_grid5;
	TextView image_cal_today,image_cal_tomorrow,next_month_label;
	public static FriendListAdapter1 fla1;
	public static FriendListAdapter2 fla2;
	public static FriendListAdapter3 fla3;
	public static FriendListAdapter4 fla4;
	public static FriendListAdapter5 fla5;
	public static int height,width;
	public int rowSize,imageSize;
	TextView heading,heading1,heading2,heading3,heading4,heading5;
	Typeface tf;
	public static String personClickedName,personClickedPicURL,personClickedID,flagScreen,personClickedDOB,personClickedTime;
	public static int screen;
	LinearLayout toplayout;
	Button button1,button2,button3;
	public static SharedPreferences userNamePref;
	
	JSONObject jObject;
	TextView refresh,settings,about;
	public boolean dataSavedLogout=false;
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

	public boolean progressRunning=true;
	public boolean timeOut=false;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*if(HomeScreen.fromBackPress){
			HomeScreen.fromBackPress=false;
			finish();
		}*/
		
		
		flagScreen="Events";
		GAUtility.trackView(this, "EventScreen");
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;
		width = dm.widthPixels;
		
		if(Settings.returnScreen(width, height)==2){
			rowSize=140;
			imageSize=85;
		}
		else if(Settings.returnScreen(width, height)==3){
			rowSize=205;
			imageSize=123;
		}
		else if(Settings.returnScreen(width, height)==4){
			rowSize=300;
			imageSize=191;
		}
		else if(Settings.returnScreen(width, height)==5){
			rowSize=245;
			imageSize=161;
		}
		else if(Settings.returnScreen(width, height)==6){
			rowSize=275;
			imageSize=216;
		}
		else if(Settings.returnScreen(width, height)==7){
			rowSize=300;
			imageSize=191;
		}

		if(isInternetAvailable()){
			setContentView(R.layout.nointernetscreen);

			
			
			al.clear();
			Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
			
			//Log.i("Giftology.Debug",Utility.mAsyncRunner.toString());
			startTimer();
			pd = ProgressDialog.show(this, "", "Loading Friends...", true,false);
			Thread thread = new Thread(this);
			thread.start();
		}
		else{
			setContentView(R.layout.nointernetscreen);
			Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_LONG).show();
		}

	
	}

	/*private void callNotifications() {
		
		Intent alarmGiftIntent = new Intent(EventsScreen.this, NotificationReciever.class);
		//alarmGiftIntent.putExtra("userId", HomeScreen.prefs.getString("userId", null));
		PendingIntent pendingGiftIntent = PendingIntent.getBroadcast(EventsScreen.this, 0, alarmGiftIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmGiftManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		alarmGiftManager.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(),RECEIVERCALLDURATION, pendingGiftIntent);
	
	}*/
	// For preventing app from crash applying patch
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Utility.mFacebook = new Facebook(HomeScreen.APP_ID);                                  // Create the Facebook Object using the app id.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		SessionStore.restore(Utility.mFacebook, EventsScreen.this);	
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
	public void onRestart(){
		super.onRestart();
		if(HomeScreen.fromBackPress){
			HomeScreen.fromBackPress=false;
			finish();
		}
	}

*/
	
	
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
			// TODO
			if(isInternetAvailable()){
				
				popup.setVisibility(View.GONE);
				new AboutUSAsync().execute("Start");
			}
			else{
				Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.txtLogout:
			// TODO
			if(isInternetAvailable()){
				
				new LogoutAsynTask().execute("Start");
			}
			else{
				Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;

		}
		return true;
	}


	private class LogoutAsynTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=ProgressDialog.show(EventsScreen.this,"","Logging Out...",true,false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				//GAUtility.trackEvent(EventsScreen.this, "Events Screen", "Logout Button Clicked", "Menu", (long)0);
				//Log.i("Giftology.Debug",getApplicationContext().toString());
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
				startActivity(new Intent(EventsScreen.this,HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
		//Log.i("Giftology.Debug","Access Exprire "+String.valueOf(facebook.getAccessExpires()));
		return editor.commit();
	}

	/*
	 * fetching friends list
	 */
	public void populate_array_list(){
		try {
			if(!(apiResponse.equals(""))){
				jsonArray = new JSONArray(apiResponse);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String s=""+jsonArray.length();
		//		Toast.makeText(EventsScreen.this,s, Toast.LENGTH_LONG).show();//523
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy");
		String dateNow = formatter.format(currentDate.getTime());
		//		dateNow="09/01/2013";
		String today_date=dateNow.substring(0,2);   int today_date_int=Integer.parseInt(today_date); 
		String today_month=dateNow.substring(3,5); int today_month_int=Integer.parseInt(today_month);
		int today_year_int=Integer.parseInt(dateNow.substring(6,10));

		//getting today and tomorrow date to show in calender label image
		date_today=today_date;
		currentDate.add(Calendar.DATE, 1);
		String dateTomorrow = formatter.format(currentDate.getTime());
		date_tomorrow=dateTomorrow.substring(0,2);

		for(int i=0;i<jsonArray.length();i++){
			try {
				if((today_month_int>=1)&&(today_month_int<=10)){                               //i.e today_month is from JAN to OCT
					String birthdate=jsonArray.getJSONObject(i).getString("birthday_date");
					String month=birthdate.substring(0, 2); int month_int=Integer.parseInt(month);
					String date=birthdate.substring(3,5);   int date_int=Integer.parseInt(date);

					if((month_int>=today_month_int) && (month_int<=(today_month_int+1))){     // i.e for current month and a month ahead
						if(month_int==today_month_int){				                          //adding dates for current month

							Calendar c1 = Calendar.getInstance();
							c1.set(today_year_int, month_int-1, date_int);
							c1.setFirstDayOfWeek(Calendar.MONDAY);
							int weekNo = c1.get(Calendar.WEEK_OF_MONTH);
							Calendar c2 = Calendar.getInstance();
							c2.set(today_year_int, today_month_int-1, today_date_int);
							c2.setFirstDayOfWeek(Calendar.MONDAY);
							int crnt_week_number = c2.get(Calendar.WEEK_OF_MONTH);

							if(date_int>=today_date_int){                                     //i.e now we are looking for birthday ahead from today
								//								add_in_array_list(i);
								if(date_int==today_date_int){								  //selecting today birthdays
									status="today";
									add_in_array_list(i);
								}
								else if(date_int==(today_date_int+1)){							  //selecting tomorrow birthdays
									status="tomorrow";
									add_in_array_list(i);
								}
								else if(crnt_week_number==weekNo){							     //selecting birthdays of this week
									status="this_week";
									add_in_array_list(i);
								}
								else{														//selecting remaining birthdays of this month
									status="this_month";
									add_in_array_list(i);
								}
							}
						}																		
						else{																 //adding birthdays of the next month
							status="next_month";
							add_in_array_list(i);
						}
					}
					//Log.i("ABC","DEF");
				}
				else{																		//i.e now we are looking for when today_month is either NOV or DEC
					String birthdate=jsonArray.getJSONObject(i).getString("birthday_date");
					String month=birthdate.substring(0, 2); int month_int=Integer.parseInt(month);
					String date=birthdate.substring(3,5); int date_int=Integer.parseInt(date);

					if(today_month.equals("11")){											//i.e now we are looking for when today_month is NOV
						if((month.equals("11"))||(month.equals("12"))){
							if(month.equals("11")){

								Calendar c1 = Calendar.getInstance();
								c1.set(today_year_int, month_int-1, date_int);
								int weekNo = c1.get(Calendar.WEEK_OF_MONTH);
								Calendar c2 = Calendar.getInstance();
								c2.set(today_year_int, today_month_int-1, today_date_int);
								int crnt_week_number = c2.get(Calendar.WEEK_OF_MONTH);

								if(date_int>=today_date_int){	                            //i.e now we are looking for birthday ahead from today
									//									add_in_array_list(i);
									if(date_int==today_date_int){								  //selecting today birthdays
										status="today";
										add_in_array_list(i);
									}
									else if(date_int==(today_date_int+1)){							  //selecting tomorrow birthdays
										status="tomorrow";
										add_in_array_list(i);
									}
									else if(crnt_week_number==weekNo){							     //selecting birthdays of this week
										status="this_week";
										add_in_array_list(i);
									}
									else{														//selecting remaining birthdays of this month
										status="this_month";
										add_in_array_list(i);
									}
								}
							}
							else{																 //adding birthdays of the next month
								status="next_month";
								add_in_array_list(i);
							}
						}
					}
					else{																	//i.e now we are looking for when today_month is DEC
						if((month.equals("12"))||(month.equals("01"))){
							if(month.equals("12")){											// adding birthdays of December

								Calendar c1 = Calendar.getInstance();
								c1.set(today_year_int, month_int-1, date_int);
								int weekNo = c1.get(Calendar.WEEK_OF_MONTH);
								Calendar c2 = Calendar.getInstance();
								c2.set(today_year_int, today_month_int-1, today_date_int);
								int crnt_week_number = c2.get(Calendar.WEEK_OF_MONTH);

								if(date_int>=today_date_int){                                     //i.e now we are looking for birthday ahead from today
									//								add_in_array_list(i);
									if(date_int==today_date_int){								  //selecting today birthdays
										status="today";
										dec_or_jan="dec";
										add_in_array_list(i);
									}
									else if(date_int==(today_date_int+1)){							  //selecting tomorrow birthdays
										status="tomorrow";
										dec_or_jan="dec";
										add_in_array_list(i);
									}
									else if(crnt_week_number==weekNo){							     //selecting birthdays of this week
										status="this_week";
										dec_or_jan="dec";
										add_in_array_list(i);
									}
									else{														//selecting remaining birthdays of this month
										status="this_month";
										dec_or_jan="dec";
										add_in_array_list(i);
									}
								}
							}
							if(month.equals("01")){											// adding birthdays of January

								status="next_month";
								dec_or_jan="jan";
								add_in_array_list(i);
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if((dec_al_today.size()!=0)||(jan_al_today.size()!=0)){
			al_today.addAll(dec_al_today);
			al_today.addAll(jan_al_today);
		}
		if((dec_al_tomorrow.size()!=0)||(jan_al_tomorrow.size()!=0)){
			al_tomorrow.addAll(dec_al_tomorrow);
			al_tomorrow.addAll(jan_al_tomorrow);
		}
		if((dec_al_this_week.size()!=0)||(jan_al_this_week.size()!=0)){
			al_this_week.addAll(dec_al_this_week);
			al_this_week.addAll(jan_al_this_week);
		}
		if((dec_al_this_month.size()!=0)||(jan_al_this_month.size()!=0)){
			al_this_month.addAll(dec_al_this_month);
			al_this_month.addAll(jan_al_this_month);
		}
		if((dec_al_next_month.size()!=0)||(jan_al_next_month.size()!=0)){
			al_next_month.addAll(dec_al_next_month);
			al_next_month.addAll(jan_al_next_month);
		}
	}
	public void add_in_array_list(int i){
		FriendDetail fd=new FriendDetail();
		try {
			fd.setName(jsonArray.getJSONObject(i).getString("name"));name_al.add(jsonArray.getJSONObject(i).getString("name"));
			fd.setDOB(jsonArray.getJSONObject(i).getString("birthday_date"));dob_al.add(jsonArray.getJSONObject(i).getString("birthday_date"));
			fd.setPic_URL(jsonArray.getJSONObject(i).getString("pic_big"));
			fd.setID(""+jsonArray.getJSONObject(i).getLong("uid"));
			al.add(fd);
			if(status.equals("today")){
				if(dec_or_jan.equals("")){
					al_today.add(fd);
					dob_al_today.add(jsonArray.getJSONObject(i).getString("birthday_date"));
					status="";
				}
				else{
					if(dec_or_jan.equals("dec")){
						dec_al_today.add(fd);
						dec_or_jan="";
						dob_al_today.add(jsonArray.getJSONObject(i).getString("birthday_date"));
						status="";
					}
					else{
						jan_al_today.add(fd);
						dec_or_jan="";
						dob_al_today.add(jsonArray.getJSONObject(i).getString("birthday_date"));
						status="";
					}
				}
			}
			if(status.equals("tomorrow")){
				if(dec_or_jan.equals("")){
					al_tomorrow.add(fd);
					dob_al_tomorrow.add(jsonArray.getJSONObject(i).getString("birthday_date"));
					status="";
				}
				else{
					if(dec_or_jan.equals("dec")){
						dec_al_tomorrow.add(fd);
						dec_or_jan="";
						dob_al_tomorrow.add(jsonArray.getJSONObject(i).getString("birthday_date"));
						status="";
					}
					else{
						jan_al_tomorrow.add(fd);
						dec_or_jan="";
						status="";
						dob_al_tomorrow.add(jsonArray.getJSONObject(i).getString("birthday_date"));
					}
				}
			}
			if(status.equals("this_week")){
				if(dec_or_jan.equals("")){
					al_this_week.add(fd);
					dob_al_this_week.add(jsonArray.getJSONObject(i).getString("birthday_date"));
					status="";
				}
				else{
					if(dec_or_jan.equals("dec")){
						dec_al_this_week.add(fd);
						dec_or_jan="";
						dob_al_this_week.add(jsonArray.getJSONObject(i).getString("birthday_date"));
						status="";
					}
					else{
						jan_al_this_week.add(fd);
						dec_or_jan="";
						dob_al_this_week.add(jsonArray.getJSONObject(i).getString("birthday_date"));
						status="";
					}
				}
			}
			if(status.equals("this_month")){
				if(dec_or_jan.equals("")){
					al_this_month.add(fd);
					dob_al_this_month.add(jsonArray.getJSONObject(i).getString("birthday_date"));
					status="";
				}
				else{
					if(dec_or_jan.equals("dec")){
						dec_al_this_month.add(fd);
						dec_or_jan="";
						dob_al_this_month.add(jsonArray.getJSONObject(i).getString("birthday_date"));
						status="";
					}
					else{
						jan_al_this_month.add(fd);
						dec_or_jan="";
						dob_al_this_month.add(jsonArray.getJSONObject(i).getString("birthday_date"));
						status="";
					}
				}
			}
			if(status.equals("next_month")){
				if(dec_or_jan.equals("")){
					al_next_month.add(fd);
					dob_al_next_month.add(jsonArray.getJSONObject(i).getString("birthday_date"));
					status="";
				}
				else{
					if(dec_or_jan.equals("dec")){
						dec_al_next_month.add(fd);
						dec_or_jan="";
						dob_al_next_month.add(jsonArray.getJSONObject(i).getString("birthday_date"));
						status="";
					}
					else{
						jan_al_next_month.add(fd);
						dec_or_jan="";
						dob_al_next_month.add(jsonArray.getJSONObject(i).getString("birthday_date"));
						status="";
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void post_populate_array_list(){
		int number=0,reminder=0;
		if(!(al_today.size()==0)){
			image_cal_today.setText(date_today);
			reminder=al_today.size()%3;
			number=al_today.size()/3;
			if(!(reminder==0)){number=number+1;}
			lin_grid1.getLayoutParams().height =number*rowSize;
			fla1=new FriendListAdapter1(this, al_today); 
			today.setAdapter(fla1);
		}
		else{
			today_lin.setVisibility(View.GONE);
		}

		if(!(al_tomorrow.size()==0)){
			image_cal_tomorrow.setText(date_tomorrow);
			reminder=al_tomorrow.size()%3;
			number=al_tomorrow.size()/3;
			if(!(reminder==0)){number=number+1;}
			lin_grid2.getLayoutParams().height =number*rowSize;
			fla2=new FriendListAdapter2(this, al_tomorrow);
			tomorrow.setAdapter(fla2);
		}
		else{
			tomorrow_lin.setVisibility(View.GONE);
		}

		if(!(al_this_week.size()==0)){
			reminder=al_this_week.size()%3;
			number=al_this_week.size()/3;
			if(!(reminder==0)){number=number+1;}
			lin_grid3.getLayoutParams().height =number*rowSize;
			fla3=new FriendListAdapter3(this, al_this_week);
			this_week.setAdapter(fla3);
		}
		else{
			this_week_lin.setVisibility(View.GONE);
		}

		if(!(al_this_month.size()==0)){
			reminder=al_this_month.size()%3;
			number=al_this_month.size()/3;
			if(!(reminder==0)){number=number+1;}
			lin_grid4.getLayoutParams().height =number*rowSize;
			fla4=new FriendListAdapter4(this, al_this_month);
			this_month.setAdapter(fla4);
			//			this_month.setAdapter(new FriendListAdapter(this, al_next_month));
		}
		else{
			this_month_lin.setVisibility(View.GONE);
		}

		if(!(al_next_month.size()==0)){
			reminder=al_next_month.size()%3;
			number=al_next_month.size()/3;
			if(!(reminder==0)){number=number+1;}
			lin_grid5.getLayoutParams().height =number*rowSize;
			fla5=new FriendListAdapter5(this, al_next_month);
			next_month.setAdapter(fla5);
			//			next_month.setAdapter(new FriendListAdapter(this, al_this_month));
		}
		else{
			next_month_lin.setVisibility(View.GONE);
		}
	}

	/*
	 * Clicking on a friend should popup a dialog for user to post on friend's
	 * wall.
	 */
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		try {
			final long friendId;
			friendId = jsonArray.getJSONObject(position).getLong("uid");
			String name = jsonArray.getJSONObject(position).getString("name");
			new AlertDialog.Builder(this).setTitle("Post on Wall?")
			.setMessage(String.format("Would you like to post on %1$s\'s wall", name))
			.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					Bundle params = new Bundle();
					/*
					 * Source Tag: friend_wall_tag To write on a friend's wall, 
					 * provide friend's UID in the 'to' parameter.
					 * More info on feed dialog:
					 * https://developers.facebook.com/docs/reference/dialogs/feed/
					 */
					params.putString("to", String.valueOf(friendId));
					params.putString("caption", getString(R.string.app_name));
					params.putString("description", "Checkout out Hackbook for Android to learn how you can make your android apps social using Facebook Platform.");
					params.putString("picture", Utility.HACK_ICON_URL);
					params.putString("name", "I am using the Hackbook for Android");
					Utility.mFacebook.dialog(EventsScreen.this, "feed", params,
							new PostDialogListener());
				}
			}).setNegativeButton("NO", null).show();
		} catch (JSONException e) {
			Toast.makeText(EventsScreen.this,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	/*
	 * Callback after the message has been posted on friend's wall.
	 */
	public class PostDialogListener extends BaseDialogListener {

		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				Toast.makeText(EventsScreen.this,"Messase posted on the wall", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(EventsScreen.this,"Messase not posted on the wall", Toast.LENGTH_SHORT).show();
			}
		}
	}
	/*
	 * fetching today friends list
	 */
	public class FriendListAdapter1 extends BaseAdapter {
		private Context context;
		private /*final*/ ArrayList<FriendDetail> al_adap;
		EventsScreen eventsscreen;
		private LayoutInflater mInflater;

		public FriendListAdapter1(EventsScreen id,ArrayList<FriendDetail> al_fda) {
			this.eventsscreen = id;
			this.al_adap = al_fda;
			if (Utility.model == null) {
				Utility.model = new FriendsGetProfilePics();
			}
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(eventsscreen.getBaseContext());
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final FriendDetail fd=al_adap.get(position);
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.gridelement, null);
				ViewHolder holder = new ViewHolder();
				holder.profile_pic = (ImageView) hView.findViewById(R.id.grid_item_image);
				holder.name = (TextView) hView.findViewById(R.id.grid_item_label);
				holder.info = (TextView) hView.findViewById(R.id.grid_item_info);
				holder.cal_date=(TextView)hView.findViewById(R.id.cal);
				holder.image_container=(RelativeLayout)hView.findViewById(R.id.image);
				holder.desc_container=(LinearLayout)hView.findViewById(R.id.desc);
				holder.cakeImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCake);
				holder.calImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCal);
				hView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) hView.getTag();
			holder.profile_pic.getLayoutParams().height=imageSize;//
			holder.profile_pic.getLayoutParams().width=imageSize;//
			/*holder.image_container.getLayoutParams().height=200;
			holder.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			holder.info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			holder.desc_container.getLayoutParams().height=30;*/
			holder.profile_pic.setImageBitmap(Utility.model.getImage(fd.getID(),fd.getPic_URL()));
			//	profile_pic.se
			String[] name_arr=fd.getName().split(" ");
			String[] date_arr=fd.getDOB().split("/");
			String name="";
			String age="";
			if(name_arr[0].length()<=2){
				name=name_arr[0]+" "+name_arr[1];
			}
			else{
				name=name_arr[0];
			}
			if(date_arr.length==3){
				String crnt_date_arr[]=crnt_date.split("/");
				String crnt_year=crnt_date_arr[2];
				int diff=Integer.parseInt(crnt_year)-Integer.parseInt(date_arr[2]);
				age="Turns "+diff;
			}
			else{
				age="Birthday";
			}
			holder.name.setText(name);
			holder.cal_date.setText(date_arr[1]);
			holder.info.setText(age);
			holder.calImage.setVisibility(View.GONE);
			hView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(isInternetAvailable()){
						int position=0;
						personClickedName=fd.getName();
						personClickedPicURL=fd.getPic_URL();
						personClickedID=fd.getID();
						personClickedDOB=fd.getDOB();
						personClickedTime="Today";
						
						FriendsScreen.fromFriendScreen=false;
						GAUtility.trackEvent(EventsScreen.this, "Events Screen", "Todays friend selected for gifting", "Friend Selected", (long)0);
						
						startActivity(new Intent(EventsScreen.this,GiftsToSend.class));
						overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
					}
					else{
						Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return hView;
		}

		public int getCount() {
			return al_adap.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}
	}
	/*
	 * fetching tomorrow friends list
	 */
	public class FriendListAdapter2 extends BaseAdapter {
		private Context context;
		private /*final*/ ArrayList<FriendDetail> al_adap;
		EventsScreen eventsscreen;
		private LayoutInflater mInflater;

		public FriendListAdapter2(EventsScreen id,ArrayList<FriendDetail> al_fda) {
			this.eventsscreen = id;
			this.al_adap = al_fda;
			if (Utility.model == null) {
				Utility.model = new FriendsGetProfilePics();
			}
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(eventsscreen.getBaseContext());
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final FriendDetail fd=al_adap.get(position);
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.gridelement, null);
				ViewHolder holder = new ViewHolder();
				holder.profile_pic = (ImageView) hView.findViewById(R.id.grid_item_image);
				holder.name = (TextView) hView.findViewById(R.id.grid_item_label);
				holder.info = (TextView) hView.findViewById(R.id.grid_item_info);
				holder.cal_date=(TextView)hView.findViewById(R.id.cal);
				holder.image_container=(RelativeLayout)hView.findViewById(R.id.image);
				holder.desc_container=(LinearLayout)hView.findViewById(R.id.desc);
				holder.cakeImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCake);
				holder.calImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCal);
				hView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) hView.getTag();
			holder.profile_pic.getLayoutParams().height=imageSize;//
			holder.profile_pic.getLayoutParams().width=imageSize;//
			/*holder.image_container.getLayoutParams().height=200;
			holder.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			holder.info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			holder.desc_container.getLayoutParams().height=30;*/
			//			holder.image_container.getLayoutParams().height=280;
			holder.profile_pic.setImageBitmap(Utility.model.getImage(fd.getID(),fd.getPic_URL()));
			//	profile_pic.se
			String[] name_arr=fd.getName().split(" ");
			String[] date_arr=fd.getDOB().split("/");
			String name="";
			String age="";
			if(name_arr[0].length()<=2){
				name=name_arr[0]+" "+name_arr[1];
			}
			else{
				name=name_arr[0];
			}
			if(date_arr.length==3){
				String crnt_date_arr[]=crnt_date.split("/");
				String crnt_year=crnt_date_arr[2];
				int diff=Integer.parseInt(crnt_year)-Integer.parseInt(date_arr[2]);
				age="Turns "+diff;
			}
			else{
				age="Birthday";
			}
			holder.name.setText(name);
			holder.cal_date.setText(date_arr[1]);
			holder.info.setText(age);
			holder.cakeImage.setVisibility(View.GONE);
			holder.calImage.setVisibility(View.GONE);
			hView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(isInternetAvailable()){
						int position=0;
						personClickedName=fd.getName();
						personClickedPicURL=fd.getPic_URL();
						personClickedID=fd.getID();
						personClickedDOB=fd.getDOB();
						personClickedTime="Tomorrow";
						FriendsScreen.fromFriendScreen=false;
						GAUtility.trackEvent(EventsScreen.this, "Events Screen", "Tomorrows friend selected for gifting", "Friend Selected", (long)0);
						
						startActivity(new Intent(EventsScreen.this,GiftsToSend.class));
						overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
					}
					else{
						Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return hView;
		}

		public int getCount() {
			return al_adap.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}
	}
	/*
	 * fetching this week friends list
	 */
	public class FriendListAdapter3 extends BaseAdapter {
		private Context context;
		private /*final*/ ArrayList<FriendDetail> al_adap;
		EventsScreen eventsscreen;
		private LayoutInflater mInflater;

		public FriendListAdapter3(EventsScreen id,ArrayList<FriendDetail> al_fda) {
			this.eventsscreen = id;
			this.al_adap = al_fda;
			if (Utility.model == null) {
				Utility.model = new FriendsGetProfilePics();
			}
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(eventsscreen.getBaseContext());
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final FriendDetail fd=al_adap.get(position);
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.gridelement, null);
				ViewHolder holder = new ViewHolder();
				holder.profile_pic = (ImageView) hView.findViewById(R.id.grid_item_image);
				holder.name = (TextView) hView.findViewById(R.id.grid_item_label);
				holder.info = (TextView) hView.findViewById(R.id.grid_item_info);
				holder.cal_date=(TextView)hView.findViewById(R.id.cal);
				holder.image_container=(RelativeLayout)hView.findViewById(R.id.image);
				holder.desc_container=(LinearLayout)hView.findViewById(R.id.desc);
				holder.cakeImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCake);
				holder.calImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCal);
				hView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) hView.getTag();
			holder.profile_pic.getLayoutParams().height=imageSize;//
			holder.profile_pic.getLayoutParams().width=imageSize;//
			/*holder.image_container.getLayoutParams().height=200;
			holder.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			holder.info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			holder.desc_container.getLayoutParams().height=30;*/
			//			holder.image_container.getLayoutParams().height=280;
			holder.profile_pic.setImageBitmap(Utility.model.getImage(fd.getID(),fd.getPic_URL()));
			//	profile_pic.se
			String[] name_arr=fd.getName().split(" ");
			String[] date_arr=fd.getDOB().split("/");
			String name="";
			String age="";
			if(name_arr[0].length()<=2){
				name=name_arr[0]+" "+name_arr[1];
			}
			else{
				name=name_arr[0];
			}
			if(date_arr.length==3){
				String crnt_date_arr[]=crnt_date.split("/");
				String crnt_year=crnt_date_arr[2];
				int diff=Integer.parseInt(crnt_year)-Integer.parseInt(date_arr[2]);
				age="Turns "+diff;
			}
			else{
				age="Birthday";
			}
			holder.name.setText(name);
			holder.cal_date.setText(date_arr[1]);
			holder.info.setText(age);
			holder.cakeImage.setVisibility(View.GONE);
			hView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(isInternetAvailable()){
						int position=0;
						personClickedName=fd.getName();
						personClickedPicURL=fd.getPic_URL();
						personClickedID=fd.getID();
						personClickedDOB=fd.getDOB();
						personClickedTime="";
						FriendsScreen.fromFriendScreen=false;
						GAUtility.trackEvent(EventsScreen.this, "Events Screen", "This weeks friend selected for gifting", "Friend Selected", (long)0);
						
						startActivity(new Intent(EventsScreen.this,GiftsToSend.class));
						overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
					}
					else{
						Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return hView;
		}

		public int getCount() {
			return al_adap.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}
	}
	/*
	 * fetching this month friends list
	 */
	public class FriendListAdapter4 extends BaseAdapter {
		private Context context;
		private /*final*/ ArrayList<FriendDetail> al_adap;
		EventsScreen eventsscreen;
		private LayoutInflater mInflater;

		public FriendListAdapter4(EventsScreen id,ArrayList<FriendDetail> al_fda) {
			this.eventsscreen = id;
			this.al_adap = al_fda;
			if (Utility.model == null) {
				Utility.model = new FriendsGetProfilePics();
			}
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(eventsscreen.getBaseContext());
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final FriendDetail fd=al_adap.get(position);
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.gridelement, null);
				ViewHolder holder = new ViewHolder();
				holder.profile_pic = (ImageView) hView.findViewById(R.id.grid_item_image);
				holder.name = (TextView) hView.findViewById(R.id.grid_item_label);
				holder.info = (TextView) hView.findViewById(R.id.grid_item_info);
				holder.cal_date=(TextView)hView.findViewById(R.id.cal);
				holder.image_container=(RelativeLayout)hView.findViewById(R.id.image);
				holder.desc_container=(LinearLayout)hView.findViewById(R.id.desc);
				holder.cakeImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCake);
				holder.calImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCal);
				hView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) hView.getTag();
			holder.profile_pic.getLayoutParams().height=imageSize;//
			holder.profile_pic.getLayoutParams().width=imageSize;//
			/*holder.image_container.getLayoutParams().height=200;
			holder.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			holder.info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			holder.desc_container.getLayoutParams().height=30;*/
			//			holder.image_container.getLayoutParams().height=280;
			holder.profile_pic.setImageBitmap(Utility.model.getImage(fd.getID(),fd.getPic_URL()));
			//	profile_pic.se
			String[] name_arr=fd.getName().split(" ");
			String[] date_arr=fd.getDOB().split("/");
			String name="";
			String age="";
			if(name_arr[0].length()<=2){
				name=name_arr[0]+" "+name_arr[1];
			}
			else{
				name=name_arr[0];
			}
			if(date_arr.length==3){
				String crnt_date_arr[]=crnt_date.split("/");
				String crnt_year=crnt_date_arr[2];
				int diff=Integer.parseInt(crnt_year)-Integer.parseInt(date_arr[2]);
				age="Turns "+diff;
			}
			else{
				age="Birthday";
			}
			holder.name.setText(name);
			holder.cal_date.setText(date_arr[1]);
			holder.info.setText(age);
			holder.cakeImage.setVisibility(View.GONE);
			hView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(isInternetAvailable()){
						int position=0;
						personClickedName=fd.getName();
						personClickedPicURL=fd.getPic_URL();
						personClickedID=fd.getID();
						personClickedDOB=fd.getDOB();
						personClickedTime="";
						
						FriendsScreen.fromFriendScreen=false;
						GAUtility.trackEvent(EventsScreen.this, "Events Screen", "This month friend selected for gifting", "Friend Selected", (long)0);
						
						startActivity(new Intent(EventsScreen.this,GiftsToSend.class));
						overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
					}
					else{
						Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return hView;
		}

		public int getCount() {
			return al_adap.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}
	}
	/*
	 * fetching next month friends list
	 */
	public class FriendListAdapter5 extends BaseAdapter {
		private Context context;
		private /*final*/ ArrayList<FriendDetail> al_adap;
		EventsScreen eventsscreen;
		private LayoutInflater mInflater;

		public FriendListAdapter5(EventsScreen id,ArrayList<FriendDetail> al_fda) {
			this.eventsscreen = id;
			this.al_adap = al_fda;
			if (Utility.model == null) {
				Utility.model = new FriendsGetProfilePics();
			}
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(eventsscreen.getBaseContext());
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final FriendDetail fd=al_adap.get(position);
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.gridelement, null);
				ViewHolder holder = new ViewHolder();
				holder.profile_pic = (ImageView) hView.findViewById(R.id.grid_item_image);
				holder.name = (TextView) hView.findViewById(R.id.grid_item_label);
				holder.info = (TextView) hView.findViewById(R.id.grid_item_info);
				holder.cal_date=(TextView)hView.findViewById(R.id.cal);
				holder.image_container=(RelativeLayout)hView.findViewById(R.id.image);
				holder.desc_container=(LinearLayout)hView.findViewById(R.id.desc);
				holder.cakeImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCake);
				holder.calImage=(RelativeLayout)hView.findViewById(R.id.overlapImageCal);
				hView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) hView.getTag();
			holder.profile_pic.getLayoutParams().height=imageSize;//
			holder.profile_pic.getLayoutParams().width=imageSize;//
			/*holder.image_container.getLayoutParams().height=200;
			holder.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			holder.info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			holder.desc_container.getLayoutParams().height=30;*/
			//			holder.image_container.getLayoutParams().height=280;
			holder.profile_pic.setImageBitmap(Utility.model.getImage(fd.getID(),fd.getPic_URL()));
			//	profile_pic.se
			String[] name_arr=fd.getName().split(" ");
			String[] date_arr=fd.getDOB().split("/");
			String name="";
			String age="";
			if(name_arr[0].length()<=2){
				name=name_arr[0]+" "+name_arr[1];
			}
			else{
				name=name_arr[0];
			}
			if(date_arr.length==3){
				String crnt_date_arr[]=crnt_date.split("/");
				String crnt_year=crnt_date_arr[2];
				int diff=Integer.parseInt(crnt_year)-Integer.parseInt(date_arr[2]);
				age="Turns "+diff;
			}
			else{
				age="Birthday";
			}
			holder.name.setText(name);
			holder.cal_date.setText(date_arr[1]);
			holder.info.setText(age);
			holder.cakeImage.setVisibility(View.GONE);
			hView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(isInternetAvailable()){
						int position=0;
						personClickedName=fd.getName();
						personClickedPicURL=fd.getPic_URL();
						personClickedID=fd.getID();
						personClickedDOB=fd.getDOB();
						personClickedTime="";
						FriendsScreen.fromFriendScreen=false;
						GAUtility.trackEvent(EventsScreen.this, "Events Screen", "Next Month friend selected for gifting", "Friend Selected", (long)0);
						
						startActivity(new Intent(EventsScreen.this,GiftsToSend.class));
						
						overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
					}
					else{
						Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return hView;
		}

		public int getCount() {
			return al_adap.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}
	}
	class ViewHolder {
		ImageView profile_pic;
		TextView name;
		TextView info;
		TextView cal_date;
		RelativeLayout image_container;
		LinearLayout desc_container;
		RelativeLayout cakeImage,calImage;
	}
	/*
	 *  callback after friends are fetched via fql query.
	 */
	public class FriendsRequestListener extends BaseRequestListener {
		public void onComplete(final String response, final Object state) {
			apiResponse=response;
			friendListFetched=true;
			
			handler.sendEmptyMessage(0);
		}
		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),Toast.LENGTH_SHORT).show();
		}
	}
	public void run() {
		
		String query="SELECT uid, name,current_location, birthday_date,pic_big FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) AND strlen(birthday_date) != 0 ORDER BY birthday_date";
		Bundle param = new Bundle();
		param.putString("method", "fql.query");
		param.putString("query", query);
		//Log.i("Giftology.Debug",param.toString());
		Utility.mAsyncRunner.request(null, param,new FriendsRequestListener());
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			screen=1;
			//			setContentView(R.layout.event_screen);
			if(!timeOut){
				progressRunning=false;
				
		
				
				initializeui();
				populate_array_list();
				post_populate_array_list();
				pd.dismiss();
				if(HomeScreen.prefs==null){
					HomeScreen.prefs= getApplicationContext().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
				}
				
				if(!(HomeScreen.prefs.getBoolean("isUserRegistered", true))){
					new RegisterUserTask().execute("Start");
				}
			}
			else{
				pd.dismiss();
				setContentView(R.layout.nointernetscreen);
			}
			timeOut=false;

		}
	};
	public void initializeui(){

		setContentView(R.layout.event_screen);
		tf=Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		today    = (GridView)findViewById(R.id.gridView1);
		tomorrow   = (GridView)findViewById(R.id.gridView2);
		this_week   = (GridView)findViewById(R.id.gridView3);
		this_month   = (GridView)findViewById(R.id.gridView4);
		next_month   = (GridView)findViewById(R.id.gridView5);
		today_lin   = (RelativeLayout)findViewById(R.id.linearLayout1);
		tomorrow_lin  = (RelativeLayout)findViewById(R.id.linearLayout2);
		this_week_lin  = (RelativeLayout)findViewById(R.id.linearLayout3);
		this_month_lin  = (RelativeLayout)findViewById(R.id.linearLayout4);
		next_month_lin  = (RelativeLayout)findViewById(R.id.linearLayout5);
		lin_grid1   = (LinearLayout)findViewById(R.id.lin_grid1);
		lin_grid2   = (LinearLayout)findViewById(R.id.lin_grid2);
		lin_grid3   = (LinearLayout)findViewById(R.id.lin_grid3);
		lin_grid4   = (LinearLayout)findViewById(R.id.lin_grid4);
		lin_grid5   = (LinearLayout)findViewById(R.id.lin_grid5);
		image_cal_today  = (TextView)findViewById(R.id.cal_today);
		image_cal_tomorrow = (TextView)findViewById(R.id.cal_tomorrow);
		next_month_label = (TextView)findViewById(R.id.headingText5);
		//more_options  = (ImageView)findViewById(R.id.more_option);
		popup    = (RelativeLayout)findViewById(R.id.menu_layout);
		heading1=(TextView)findViewById(R.id.headingText1);heading1.setTypeface(tf);
		heading2=(TextView)findViewById(R.id.headingText2);heading2.setTypeface(tf);
		heading3=(TextView)findViewById(R.id.headingText3);heading3.setTypeface(tf);
		heading4=(TextView)findViewById(R.id.headingText4);heading4.setTypeface(tf);
		heading5=(TextView)findViewById(R.id.headingText5);heading5.setTypeface(tf);
		heading=(TextView)findViewById(R.id.heading);heading.setTypeface(tf);

		refresh=(TextView)findViewById(R.id.text01);
		about=(TextView)findViewById(R.id.text02);
		settings=(TextView)findViewById(R.id.text03);

		/*more_options.setOnClickListener(new ImageView.OnClickListener(){
			public void onClick(View v){
				if(popup.getVisibility()==View.GONE){
					popup.setVisibility(View.VISIBLE);
				}
				else{
					popup.setVisibility(View.GONE);
				}
			}
		});*/
		today.setOnTouchListener(new GridView.OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(popup.getVisibility()==View.VISIBLE){popup.setVisibility(View.GONE);}
				if(arg1.getAction() == MotionEvent.ACTION_MOVE){
					return true;
				}
				return false;
			}
		});
		tomorrow.setOnTouchListener(new GridView.OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(popup.getVisibility()==View.VISIBLE){popup.setVisibility(View.GONE);}
				if(arg1.getAction() == MotionEvent.ACTION_MOVE){
					return true;
				}
				return false;
			}
		});
		this_week.setOnTouchListener(new GridView.OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(popup.getVisibility()==View.VISIBLE){popup.setVisibility(View.GONE);}
				if(arg1.getAction() == MotionEvent.ACTION_MOVE){
					return true;
				}
				return false;
			}
		});
		this_month.setOnTouchListener(new GridView.OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(popup.getVisibility()==View.VISIBLE){popup.setVisibility(View.GONE);}
				if(arg1.getAction() == MotionEvent.ACTION_MOVE){
					return true;
				}
				return false;
			}
		});
		next_month.setOnTouchListener(new GridView.OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(popup.getVisibility()==View.VISIBLE){popup.setVisibility(View.GONE);}
				if(arg1.getAction() == MotionEvent.ACTION_MOVE){
					return true;
				}
				return false;
			}
		});

		refresh.setOnClickListener(new TextView.OnClickListener(){
			@Override
			public void onClick(View v) {
				popup.setVisibility(View.GONE);
				if(isInternetAvailable()){
					SharedPreferences.Editor editor = HomeScreen.prefs.edit();
					editor.putBoolean("isUserRegistered", false);
					editor.commit();
					startActivity(new Intent(EventsScreen.this,EventsScreen.class));
					overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
				}
				else{
					Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});
		about.setOnClickListener(new TextView.OnClickListener(){

			String str="";
			@Override
			public void onClick(View v) {
				popup.setVisibility(View.GONE);
				if(isInternetAvailable()){
					new AboutUSAsync().execute("Start");
				}
				else{
					Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});
		settings.setOnClickListener(new TextView.OnClickListener(){
			@Override
			public void onClick(View v) {
				popup.setVisibility(View.GONE);
				if(isInternetAvailable()){
					startActivity(new Intent(EventsScreen.this,Settings.class));
					overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
				}
				else{
					Toast.makeText(EventsScreen.this, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	
	private class AboutUSAsync extends AsyncTask<String, Integer, String> {
		TextView txt;
		ProgressBar pb;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		
			
			final Dialog dialog = new Dialog(EventsScreen.this);
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
			GAUtility.trackEvent(EventsScreen.this, "Event Screen", "About Us", "Menu", (long)0);
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
							Toast.makeText(EventsScreen.this, "Network error.Please try again later", Toast.LENGTH_LONG).show();
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
	public static void callnotify(){
		if(!(fla1==null)){
			fla1.notifyDataSetChanged();
		}
		if(!(fla2==null)){
			fla2.notifyDataSetChanged();
		}
		if(!(fla3==null)){
			fla3.notifyDataSetChanged();
		}
		if(!(fla4==null)){
			fla4.notifyDataSetChanged();
		}
		if(!(fla5==null)){
			fla5.notifyDataSetChanged();
		}
	}
	/*
	 * registering user on server
	 */
	private class RegisterUserTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//						pd = ProgressDialog.show(EventsScreen.this, "", "Registering User...");
		}
		@Override
		protected String doInBackground(String... params) {
			String str="";
			try {

				String userdobReverse="",userbirthYear="";
				jObject = new JSONObject(Utility.mFacebook.request("me"));

				String userDOB=jObject.getString("birthday");     //MM/DD/YYYY format
		
				String userarrDOB[]=userDOB.split("/");
				if(userarrDOB.length==3){
					userdobReverse=userarrDOB[2]+"-"+userarrDOB[1]+"-"+userarrDOB[0];
					userbirthYear=userarrDOB[2];
				}
				if(userarrDOB.length==2){
					userdobReverse=userarrDOB[1]+"-"+userarrDOB[0];
					userbirthYear="";
				}
				
				JSONObject json = new JSONObject();

				JSONObject jsonUser=new JSONObject();
				userNamePref = getSharedPreferences("USERNAME",MODE_PRIVATE);
				//Toast.makeText(EventsScreen.this,"USERID = "+userNamePref,Toast.LENGTH_LONG).show();
				Editor pref_edit=userNamePref.edit();
				pref_edit.putString("username",jObject.getString("name"));
				pref_edit.commit();
				
				
				jsonUser.put("username", jObject.getString("name"));//Vaibhav Rastogi
				jsonUser.put("facebook_id",jObject.getString("id"));//100002950256522
				json.put("User",jsonUser); ////{"User":{"username":"Vaibhav Rastogi","facebook_id":"100002950256522"}}
				
				JSONObject jsonUserProfile=new JSONObject();
				jsonUserProfile.put("first_name", jObject.getString("first_name"));//Vaibhav 
				jsonUserProfile.put("last_name",jObject.getString("last_name"));//Rastogi
				jsonUserProfile.put("email", "");//null
				jsonUserProfile.put("mobile","");//null
				try{
					jsonUserProfile.put("city",getCityName(jObject.getString("location")));
				}
				catch (Exception e) {
					jsonUserProfile.put("city","");
				}
				//				jsonUserProfile.put("city",getCityName(jObject.getString("location")));//"id":"112369278780092","name":"Ghaziabad, India"
				jsonUserProfile.put("birthday",userdobReverse);//1989/04/03
				json.put("UserProfile", jsonUserProfile);  /////{"User":{"username":"Vaibhav Rastogi","facebook_id":"100002950256522"},"UserProfile":{"last_name":"Rastogi","birthday":"1989-03-04","first_name":"Vaibhav","email":"","city":"Ghaziabad","mobile":""}}

				JSONObject jsonUserUtm=new JSONObject();
				jsonUserUtm.put("utm_source", "mobileapp");
				json.put("UserUtm", jsonUserUtm);  /////{"User":{"username":"Vaibhav Rastogi","facebook_id":"100002950256522"},"UserUtm":{"utm_source":"mobileapp"},"UserProfile":{"last_name":"Rastogi","birthday":"1989-03-04","first_name":"Vaibhav","email":"","city":"Ghaziabad","mobile":""}}

				if(!(apiResponse.equals(""))){
					jsonArray = new JSONArray(apiResponse);
				}

				JSONArray jsonArrayReminder=new JSONArray();

				//				for(int i=0;i<jsonArray.length();i++){
				for(int i=0;i<jsonArray.length();i++){
					String userbdayReverse="",userbdayYear="";
					String bday=jsonArray.getJSONObject(i).getString("birthday_date");
					String userarrbday[]=bday.split("/");
					if(userarrbday.length==3){
						userbdayReverse=userarrbday[2]+"-"+userarrbday[1]+"-"+userarrbday[0];
						userbdayYear=userarrbday[2];
					}
					if(userarrbday.length==2){
						userbdayReverse=userarrbday[1]+"-"+userarrbday[0];
						userbdayYear="";
					}

					JSONObject jsonFriend=new JSONObject();
					jsonFriend.put("friend_fb_id", jsonArray.getJSONObject(i).getLong("uid"));//100001483700756
					jsonFriend.put("friend_name", jsonArray.getJSONObject(i).getString("name")); //Sarvesh Kumar
					jsonFriend.put("friend_birthday", userbdayReverse);//1989/11/02
					jsonFriend.put("friend_birthyear", userbdayYear);//1989

					jsonArrayReminder.put(jsonFriend);
					jsonFriend=null;
					//					System.gc();
				}

				json.put("Reminders",jsonArrayReminder);

				Object jresonse1=json.get("User");
				Object jresonse2=json.get("UserProfile");
				Object jresonse3=json.get("UserUtm");
				Object jresonse4=json.get("Reminders");


				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 300000);
				HttpConnectionParams.setSoTimeout(httpParams, 300000);
				HttpClient client = new DefaultHttpClient(httpParams);

				HttpPost request = new HttpPost(KeyGenerator.encodedURL("http://www.giftology.com/users/ws_add.json?"));
				request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
				HttpResponse response = client.execute(request);
		
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
					str=sb.toString();
					if(str.contains("Status\":\"OK")){
						String arr[]=str.split("\"");

						outer:for(int i=0;i<arr.length;i++){
							if(arr[i].equals("user_id")){
								final String uID=arr[i+2];

								SharedPreferences.Editor editor = HomeScreen.prefs.edit();
								editor.putString("userID", uID);
								editor.putBoolean("isUserRegistered", true);
								editor.commit();
								/*runOnUiThread(new Runnable() {
									public void run() { 	
										Toast.makeText(EventsScreen.this,"USERID = "+uID,Toast.LENGTH_LONG).show();
									}
								});*/
								break outer;
							}
						}
					}
					else{
		
						SharedPreferences.Editor editor = HomeScreen.prefs.edit();
						editor.putBoolean("isUserRegistered", false);
						editor.commit();
					}
					Log.i("","response  xml file:" +sb.toString()); 
				}
			}catch (Exception e) {
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
			/*pd.dismiss();
			if(HomeScreen.prefs.getBoolean("isUserRegistered", true)){
				Toast.makeText(EventsScreen.this,"User Registered...", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(EventsScreen.this,"User Not Registered.Try next time...", Toast.LENGTH_SHORT).show();
			}*/
		}
	}
	public String getCityName(String location){//"id":"112369278780092","name":"Ghaziabad, India"
		String cityName="";
		if(location!=null){
			location=location.replace("\"", "");   //id:112369278780092,name:Ghaziabad, India
			String arr1[]=location.split(",");
			String arr2[]=arr1[1].split(":");

			cityName=arr2[1];
		}

		return cityName;
	}

	public boolean isInternetAvailable(){
		/*try {
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
		}*/
		
		return true;
	}
	public void startTimer(){
		progressRunning=true;
		timeOut=false;
		
		if(Utility.mFacebook==null)
		{
			Utility.mFacebook = new Facebook(HomeScreen.APP_ID);                                  // Create the Facebook Object using the app id.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		SessionStore.restore(Utility.mFacebook, EventsScreen.this);	
		restoreCredentials(Utility.mFacebook);
		}
		
		TimerTask task = new TimerTask() {
			public void run() {
				ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
				List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
				String currentactivity=taskInfo.get(0).topActivity.getClassName();
				if(currentactivity.equals("com.unikove.giftology.EventsScreen")){
					if(progressRunning){
						if(pd!=null){
							pd.dismiss();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									timeOut=true;
									/*
										Utility.mFacebook = new Facebook(HomeScreen.APP_ID);                 
										
										Log.i("FACEBOOK.DEBUG","Facebook: "+ Utility.mFacebook.toString());
										
										restoreCredentials(Utility.mFacebook) ;
									*/
									/*Utility.mFacebook = new Facebook(HomeScreen.APP_ID);                                  // Create the Facebook Object using the app id.
									Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
									SessionStore.restore(Utility.mFacebook, EventsScreen.this);	
									restoreCredentials(Utility.mFacebook);*/
									
									Toast.makeText(EventsScreen.this, "Network error.Please try again later", Toast.LENGTH_LONG).show();
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

