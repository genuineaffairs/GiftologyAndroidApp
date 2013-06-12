package com.unikove.giftology.activityscreens;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

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
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;
import com.google.analytics.tracking.android.EasyTracker;
import com.unikove.fb.Utility;
import com.unikove.giftology.R;
import com.unikove.giftology.data.ContactDetail;
import com.unikove.giftology.reciever.NotificationReciever;
import com.unikove.giftology.util.CachingUtility;
import com.unikove.giftology.util.ConnectionUtility;
import com.unikove.giftology.util.GAUtility;
import com.unikove.giftology.util.GiftologyUtility;

public class ShareTheNews extends Activity {
	TextView text;
	RelativeLayout sendMsg, sendMail;
	ProgressDialog pd;
	private static final int PICK_CONTACT = 1001;
	private static final int PICK_EMAIL = 1002;
	ArrayList<ContactDetail> al = new ArrayList<ContactDetail>();
	Dialog dialog;
	String emailorPhone;
	String predefinedString1, predefinedString2;
	String textToSend;
	Button done;
	String userFirstName;
	public boolean dataSavedLogout = false;
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	Typeface tf;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharethenews);
		GAUtility.trackView(ShareTheNews.this, "Share The News");
		EasyTracker.getInstance().activityStart(this);
		sendMsg = (RelativeLayout) findViewById(R.id.messagelinearLayout);
		sendMail = (RelativeLayout) findViewById(R.id.maillinearLayout);
		text = (TextView) findViewById(R.id.fbwallpostTextView);
		done = (Button) findViewById(R.id.done);
		text.setText(GiftsToSend.name + "'s Wall");
		tf = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
		enableHttpCaching();
		predefinedString1 = "Hey ";
		predefinedString2 = "\nI just sent you a Gift Voucher on Facebook via Giftology.com.\nGo check it out it's pretty awesome!";
		String[] name_arr = EventsScreen.personClickedName.split(" ");

		textToSend = predefinedString1 + name_arr[0] + predefinedString2/*
																		 * +"\n"+
																		 * GiftsToSendFinal
																		 * .
																		 * messageToShare
																		 */;

		if (EventsScreen.userNamePref == null) {
			EventsScreen.userNamePref = getSharedPreferences("USERNAME",
					MODE_PRIVATE);
		}
		String userName = EventsScreen.userNamePref.getString("username", "");
		String userNameArr[] = userName.split(" ");
		userFirstName = userNameArr[0];
		/*
		 * sending msg
		 */
		sendMsg.setOnClickListener(new RelativeLayout.OnClickListener() {
			@Override
			public void onClick(View v) {
				al.clear();
				emailorPhone = "phone";
				GAUtility.trackEvent(ShareTheNews.this, "Share The News",
						"Send Message Clicked", "Share the message", (long) 0);

				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, PICK_CONTACT);
			}
		});
		/*
		 * sending mail
		 */
		sendMail.setOnClickListener(new RelativeLayout.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ConnectionUtility
						.isInternetAvailable(getApplicationContext())) {
					al.clear();
					emailorPhone = "email";
					GAUtility.trackEvent(ShareTheNews.this, "Share The News",
							"Email Clicked", "Share the message", (long) 0);

					Intent intent = new Intent(Intent.ACTION_PICK,
							ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(intent, PICK_EMAIL);
				} else {
					Toast.makeText(ShareTheNews.this, "Internet not available",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		/*
		 * going back for another transaction
		 */
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ConnectionUtility
						.isInternetAvailable(getApplicationContext())) {
					GAUtility.trackEvent(ShareTheNews.this, "Share The News",
							"Done Clicked", "Share the message", (long) 0);

					Intent intent = null;
					if (FriendsScreen.fromFriendScreen) {
						intent = new Intent(ShareTheNews.this,
								FriendsScreen.class);
						FriendsScreen.fromFriendScreen = false;
					} else {
						intent = new Intent(ShareTheNews.this,
								EventsScreen.class);
					}
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(ShareTheNews.this, "Internet not available",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/*
	 * getting email and phone number of selected user
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_CONTACT) {
			if (resultCode == RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					String id = c
							.getString(c
									.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
					String hasPhone = c
							.getString(c
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					if (hasPhone.equalsIgnoreCase("1")) {
						Cursor phones = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = " + id, null, null);
						phones.moveToFirst();
						if (phones.getCount() > 0) {
							for (int i = 0; i < phones.getCount(); i++) {
								ContactDetail cd = new ContactDetail();
								String cNumber = phones
										.getString(phones
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								String cLabel = phones
										.getString(phones
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
								cd.setNUMBER(cNumber);
								cd.setLABEL(cLabel);
								al.add(cd);
								phones.moveToNext();
							}
						}
						if (al.size() != 0) {
							if (al.size() > 1) {
								showAlertDialog_Language("Select Number");
							} else {
								emailorPhone = " ";
								Intent intent = new Intent(Intent.ACTION_VIEW,
										Uri.parse("sms:"
												+ al.get(0).getNUMBER()));

								intent.putExtra("sms_body", textToSend + "\n"
										+ "- " + userFirstName);
								startActivity(intent);
							}
						} else {
							Toast.makeText(
									ShareTheNews.this,
									"number not present for the selected contact",
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(ShareTheNews.this,
								"number not present for the selected contact",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		}
		if (requestCode == PICK_EMAIL) {
			if (resultCode == RESULT_OK) {
				Uri contactData = data.getData();
				Cursor cursor = getContentResolver().query(contactData, null,
						null, null, null);
				if (cursor.moveToFirst()) {
					String contactId = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));

					Cursor emails = getContentResolver().query(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID
									+ " = " + contactId, null, null);

					emails.moveToFirst();
					if (emails.getCount() > 0) {
						for (int i = 0; i < emails.getCount(); i++) {
							ContactDetail cd = new ContactDetail();
							String emailAddress = emails
									.getString(emails
											.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
							String label = emails
									.getString(emails
											.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
							cd.setNUMBER(emailAddress);
							cd.setLABEL(label);
							al.add(cd);
							emails.moveToNext();
						}
					}
					if (al.size() != 0) {
						if (al.size() > 1) {
							showAlertDialog_Language("Select email");
						} else {
							// txt.setText(al.get(0).getNUMBER());
							if (EventsScreen.userNamePref == null) {
								EventsScreen.userNamePref = getSharedPreferences(
										"USERNAME", MODE_PRIVATE);
							}
							String userName = EventsScreen.userNamePref
									.getString("username", "");
							// String userNameArr[]=userName.split(" ");

							emailorPhone = " ";
							Intent emailIntent = new Intent(
									android.content.Intent.ACTION_SEND);
							String aEmailList[] = { al.get(0).getNUMBER() };
							emailIntent.putExtra(
									android.content.Intent.EXTRA_EMAIL,
									aEmailList);
							emailIntent.putExtra(
									android.content.Intent.EXTRA_TEXT,
									textToSend + "\n" + "- " + userFirstName);
							emailIntent
									.putExtra(
											android.content.Intent.EXTRA_SUBJECT,
											"Surprise! I've sent you a gift on Giftology");
							// emailIntent.putExtra(Intent.EXTRA_STREAM,
							// outputFileUri);
							emailIntent.setType("plain/text");
							startActivity(Intent.createChooser(emailIntent,
									"Send your email in:"));
						}
					} else {
						Toast.makeText(ShareTheNews.this,
								"email not present for the selected contact",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	}

	public void showAlertDialog_Language(String str) {
		dialog = new Dialog(ShareTheNews.this);
		dialog.setContentView(R.layout.alert_dialog_contact);
		dialog.setTitle(str);
		ListView listView = (ListView) dialog.findViewById(R.id.listView1);
		NumberAdapter dialogadapter = new NumberAdapter(ShareTheNews.this, al);
		listView.setAdapter(dialogadapter);

		dialog.show();
	}

	class NumberAdapter extends ArrayAdapter<ContactDetail> {
		private final Context context;
		private final ArrayList<ContactDetail> al_val;

		public NumberAdapter(Context context, ArrayList<ContactDetail> al_val) {
			super(context, R.layout.contact_row_element, al_val);
			this.context = context;
			this.al_val = al_val;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.contact_row_element,
					parent, false);
			TextView label = (TextView) rowView.findViewById(R.id.textView1);
			TextView number = (TextView) rowView.findViewById(R.id.textView2);
			ContactDetail cd = al_val.get(position);
			label.setText(cd.getLABEL());
			number.setText(cd.getNUMBER());

			rowView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.cancel();
					if (emailorPhone.equals("phone")) {
						emailorPhone = " ";
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri
								.parse("sms:"
										+ al_val.get(position).getNUMBER()));
						intent.putExtra("sms_body", textToSend + "\n" + "- "
								+ userFirstName);
						startActivity(intent);
					} else if (emailorPhone.equals("email")) {
						if (ConnectionUtility
								.isInternetAvailable(getApplicationContext())) {
							emailorPhone = " ";
							Intent emailIntent = new Intent(
									android.content.Intent.ACTION_SEND);
							String aEmailList[] = { al_val.get(position)
									.getNUMBER() };
							emailIntent.putExtra(
									android.content.Intent.EXTRA_EMAIL,
									aEmailList);
							emailIntent.putExtra(
									android.content.Intent.EXTRA_TEXT,
									textToSend + "\n" + "- " + userFirstName);
							emailIntent.putExtra(
									android.content.Intent.EXTRA_SUBJECT,
									"Giftology");
							emailIntent.setType("plain/text");
							startActivity(Intent.createChooser(emailIntent,
									"Send your email in:"));
						} else {
							Toast.makeText(ShareTheNews.this,
									"Internet not available",
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
			return rowView;
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
				Toast.makeText(ShareTheNews.this, "Internet not available",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.txtLogout:
			if (ConnectionUtility.isInternetAvailable(getApplicationContext())) {
				new LogoutAsynTask().execute("Start");
			} else {
				Toast.makeText(ShareTheNews.this, "Internet not available",
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
				Toast.makeText(ShareTheNews.this, "Internet not available", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return true;
	}

	private class LogoutAsynTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(ShareTheNews.this, "", "Logging Out...",
					true, false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				GAUtility.trackEvent(ShareTheNews.this, "Share The News",
						"Logout Clicked", "Menu", (long) 0);
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
				startActivity(new Intent(ShareTheNews.this, HomeScreen.class)
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

			final Dialog dialog = new Dialog(ShareTheNews.this);
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
			GAUtility.trackEvent(ShareTheNews.this, "Share The News",
					"About us Clicked", "Menu", (long) 0);

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
							Toast.makeText(ShareTheNews.this,
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
			//	Log.i(GiftologyUtility.TAG,e.toString());
			}
		}
	}

}
