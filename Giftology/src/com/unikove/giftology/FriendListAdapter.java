package com.unikove.giftology;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unikove.fb.FriendsGetProfilePics;
import com.unikove.fb.Utility;

public class FriendListAdapter extends ArrayAdapter<FriendDetail>{
	private final Activity context;
	ArrayList<FriendDetail> al;
	static String namevalue;
	Button phonebook,edit,delete,cancel,editcancel,editsave;
	DatePicker editpicker;
	EditText editnumber,editname;
	String tempname,tempdob,tempnumber;
	static final int PICK_NUMBER = 2;
	public static String namecl,numbercl,dobcl;
	Typeface tf;

	public FriendListAdapter(Activity context, ArrayList<FriendDetail> description) {
		super(context, R.layout.friendscreen_rowitem, description);
		this.context = context;
		this.al = description;
		if (Utility.model == null) {
			Utility.model = new FriendsGetProfilePics();
		}
		tf = Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf");
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.friendscreen_rowitem, null, true);
		final TextView name = (TextView) rowView.findViewById(R.id.name);
		final ImageView image = (ImageView) rowView.findViewById(R.id.image);

		final FriendDetail fd = al.get(position);
		name.setText(fd.getName());name.setTypeface(tf);
		image.setImageBitmap(Utility.model.getImage(fd.getID(),fd.getPic_URL()));

		rowView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isInternetAvailable()){
					EventsScreen.personClickedName=fd.getName();
					EventsScreen.personClickedID=fd.getID();
					EventsScreen.personClickedPicURL=fd.getPic_URL();
					EventsScreen.personClickedDOB=fd.getDOB();
					FriendsScreen.fromFriendScreen=true;
										context.startActivity(new Intent(context,GiftsToSend.class));
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

	/*private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}*/
}
