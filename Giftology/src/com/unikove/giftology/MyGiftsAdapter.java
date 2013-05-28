package com.unikove.giftology;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ResponseCache;
import java.util.ArrayList;

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
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unikove.fb.Utility;

public class MyGiftsAdapter extends ArrayAdapter<MyGiftsDetail>{
	private final Activity context;
	ArrayList<MyGiftsDetail> al;
	public static ArrayList<MyGiftsRedeemDetail> al_giftRedeem=new ArrayList<MyGiftsRedeemDetail>();
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
		final TextView name = (TextView) rowView.findViewById(R.id.name);name.setTypeface(tf);
		final TextView price = (TextView) rowView.findViewById(R.id.price);price.setTypeface(tf);
		final RelativeLayout image = (RelativeLayout) rowView.findViewById(R.id.rel_inner);
		final TextView expiry = (TextView) rowView.findViewById(R.id.expiry);expiry.setTypeface(tf);


		final MyGiftsDetail gd=al.get(position);
		name.setText(gd.getName());
		price.setText(gd.getPRICE());
		expiry.setText("Expiry "+gd.getEXPIRY());
		if(!(gd.getBITMAP()==null)){
			Drawable d = new BitmapDrawable(context.getResources(),gd.getBITMAP());
			image.setBackgroundDrawable(d);
		}
		else{
			image.setBackgroundResource(R.drawable.thumbnail);
		}

		rowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					giftID=gd.getID();
					new LoadGiftsTask().execute("Start");
				}
				else{
					Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});

		return rowView;
	}
	private class LoadGiftsTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(context, "", "Loading...", true,false);
		}

		@Override
		protected String doInBackground(String... params) {
			String result="";
			try {
				JSONObject jObject = new JSONObject(Utility.mFacebook.request("me"));
				callWebservice(jObject.getString("id"));
				result="done";
			}
			catch(java.net.SocketTimeoutException e){
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "Network error.Please try again later", Toast.LENGTH_LONG).show();
					}
				});
			}
			catch (Exception e) {
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
			if(!result.equals("")){
				//EasyTracker.getTracker().sendEvent("MyGifts", "Gifts Selected to Redeem", "", (long) 0);
				context.startActivity(new Intent(context,MyGiftsRedeem.class));
				context.overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
			}
		}
	}
	public void callWebservice(String receiverID) throws Exception{//100002950256522
		String str="";
		//		try {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,30000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		HttpClient client = new DefaultHttpClient(httpParams);
		 //ResponseCache.setDefault(new GiftologyFBCache());
//		HttpPost httpPost = new HttpPost("http://www.giftology.com/gifts/ws_redeem.json?rand=sumit&key=8c744f645901c282a5ef6aa3451e672c&receiver_fb_id="+receiverID+"&gift_id="+giftID);
		HttpPost httpPost = new HttpPost(KeyGenerator.encodedURL("http://www.giftology.com/gifts/ws_redeem.json?")+"&receiver_fb_id="+receiverID+"&gift_id="+giftID);
		
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
			str=sb.toString();
			fillArrayList(str);
		}
		/*} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	public void fillArrayList(String s){
		al_giftRedeem.clear();
		try {
			JSONObject jsonObject=new JSONObject(s);
			JSONObject jsonObjectElement=jsonObject.getJSONObject("gift");

			MyGiftsRedeemDetail mgd=new MyGiftsRedeemDetail();

			JSONObject jsonObjectGifts=jsonObjectElement.getJSONObject("Gift");
			mgd.setCOUPONCODE(jsonObjectGifts.getString("code"));
			mgd.setMESSAGE(jsonObjectGifts.getString("gift_message"));

			JSONObject jsonProduct=jsonObjectElement.getJSONObject("Product");
			mgd.setTANDC(jsonProduct.getString("terms"));
			mgd.setREDEEMINST(jsonProduct.getString("redeem_instr"));

			JSONObject jsonVendor=jsonProduct.getJSONObject("Vendor");
			mgd.setSmall_Pic_URL("http://www.giftology.com/"+jsonVendor.getString("thumb_image"));
			mgd.setLarge_Pic_URL("http://www.giftology.com/"+jsonVendor.getString("wide_image"));


			JSONObject jsonSender=jsonObjectElement.getJSONObject("Sender");
			mgd.setFROM(jsonSender.getString("username"));

			al_giftRedeem.add(mgd);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}
	public boolean isInternetAvailable(){
		ConnectivityManager connec = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
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
}
