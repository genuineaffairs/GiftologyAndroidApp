package com.unikove.giftology;

import com.facebook.android.Facebook;
import com.unikove.fb.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CustomLinearLayout extends LinearLayout{
	private LayoutInflater mInflater = null;
	private View view = null;
	public Context context = null;
	ViewHolder holder = null;

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	
	public CustomLinearLayout(Context contexts) {
		super(contexts);
		this.context = contexts;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = mInflater.inflate(R.layout.custom_linearlayout, null);
		init();
	}
	public CustomLinearLayout(Context contexts, AttributeSet attrs) {
		super(contexts, attrs);
		this.context = contexts;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = mInflater.inflate(R.layout.custom_linearlayout,null);
		init();
	}


	public void init(){
		removeAllViews();
		
		
		holder = new ViewHolder();

		ImageView events=(ImageView)view.findViewById(R.id.imageView1);
		ImageView friends=(ImageView)view.findViewById(R.id.imageView2);
		ImageView mygifts=(ImageView)view.findViewById(R.id.imageView3);

		if(EventsScreen.screen==1){
			events.setImageResource(R.drawable.events_sel);
			friends.setImageResource(R.drawable.friends);
			mygifts.setImageResource(R.drawable.gifts);
		}
		if(EventsScreen.screen==2){
			events.setImageResource(R.drawable.events);
			friends.setImageResource(R.drawable.friends_sel);
			mygifts.setImageResource(R.drawable.gifts);
		}
		if(EventsScreen.screen==3){
			events.setImageResource(R.drawable.events);
			friends.setImageResource(R.drawable.friends);
			mygifts.setImageResource(R.drawable.gifts_sel);
		}

		events.setOnClickListener(new ImageView.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					if(!(EventsScreen.screen==1)){
						EventsScreen.screen=1;
						context.startActivity(new Intent(context,EventsScreen.class));
						((Activity) context).overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
						((Activity) context).finish();
					}
				}
				else{
					Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});
		friends.setOnClickListener(new ImageView.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					if(!(EventsScreen.screen==2)){
						EventsScreen.screen=2;
						context.startActivity(new Intent(context,FriendsScreen.class));
						((Activity) context).overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
						((Activity) context).finish();
					}
				}
				else{
					Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mygifts.setOnClickListener(new ImageView.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					if(!(EventsScreen.screen==3)){
						EventsScreen.screen=3;
						context.startActivity(new Intent(context,MyGifts.class));
						((Activity) context).overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
						((Activity) context).finish();
					}
				}
				else{
					Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});
		view.setTag(holder);
		addView(view);
	}

	class ViewHolder{

	}
	public boolean isInternetAvailable(){
		/*ConnectivityManager connec = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifi.isConnected()) {
			return true;
		} 
		else if (mobile.isConnected()) {
			return true;
		}
		return false;*/
		
		return true;
	}
}
