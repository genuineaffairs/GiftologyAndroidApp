package com.unikove.giftology;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GiftsToSendAdapter extends ArrayAdapter<GiftsToSendDetail>{
	private final Activity context;
	ArrayList<GiftsToSendDetail> al;
	Typeface tf;
	public static GiftsToSendDetail giftdet;
	int height,width;
	int imageSize;

	public GiftsToSendAdapter(Activity context, ArrayList<GiftsToSendDetail> description) {
		super(context, R.layout.giftstosend_rowitem, description);
		this.context = context;
		this.al = description;
		tf = Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf");

		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;
		width = dm.widthPixels;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.giftstosend_rowitem, null, true);
		final TextView name = (TextView) rowView.findViewById(R.id.name);name.setTypeface(tf);
		final TextView price = (TextView) rowView.findViewById(R.id.price);price.setTypeface(tf);
		final RelativeLayout image = (RelativeLayout) rowView.findViewById(R.id.rel_inner);
		final Button free = (Button) rowView.findViewById(R.id.cost);free.setTypeface(tf);

		/*if(Settings.returnScreen(width, height)==2){
			imageSize=63;
			image.getLayoutParams().height=imageSize;//
			image.getLayoutParams().width=imageSize;//
		}*/

		final GiftsToSendDetail gd=al.get(position);
		name.setText(gd.getName());
		price.setText(gd.getMINVALUE());
		if(gd.getMINPRICE().equals("0")&&gd.getMAXPRICE().equals("0")){
			free.setVisibility(View.VISIBLE);
		}
		else{
			free.setVisibility(View.INVISIBLE);
		}

		if(!(gd.getBITMAP()==null)){
			//			image.setImageBitmap(gd.getBITMAP());
			Drawable d = new BitmapDrawable(context.getResources(),gd.getBITMAP());
			image.setBackgroundDrawable(d);
		}
		else{
			image.setBackgroundResource(R.drawable.thumbnail);
		}
		free.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					if(HomeScreen.prefs==null){
						HomeScreen.prefs = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
					}
					SharedPreferences.Editor editor = HomeScreen.prefs.edit();
					editor.putString("productID", gd.getID());
					editor.commit();

					giftdet=gd;
					GiftsToSendFinal.termsconditionString=gd.getTERMSCONDITION();
					//EasyTracker.getTracker().sendEvent("GiftToSend", "Gift with id:"+gd.getID()+ "to send selected for friend", "", (long) 0);
					//				Toast.makeText(context, "Free Clicked for "+gd.getName(), Toast.LENGTH_LONG).show();
					context.startActivity(new Intent(context,GiftsToSendFinal.class));
					context.overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
				}
				else{
					Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});
		rowView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					if(HomeScreen.prefs==null){
						HomeScreen.prefs = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
					}
					SharedPreferences.Editor editor = HomeScreen.prefs.edit();
					editor.putString("productID", gd.getID());
					editor.commit();

					giftdet=gd;
					GiftsToSendFinal.termsconditionString=gd.getTERMSCONDITION();
					//				Toast.makeText(context, "Free Clicked for "+gd.getName(), Toast.LENGTH_LONG).show();
					context.startActivity(new Intent(context,GiftsToSendFinal.class));
					context.overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
				}
				else{
					Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
				}
			}
		});

		return rowView;
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
