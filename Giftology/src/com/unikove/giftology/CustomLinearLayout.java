package com.unikove.giftology;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.unikove.giftology.activityscreens.EventsScreen;
import com.unikove.giftology.activityscreens.FriendsScreen;
import com.unikove.giftology.activityscreens.MyGifts;

public class CustomLinearLayout extends LinearLayout {
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
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = mInflater.inflate(R.layout.custom_linearlayout, null);
		init();
	}

	public CustomLinearLayout(Context contexts, AttributeSet attrs) {
		super(contexts, attrs);
		this.context = contexts;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = mInflater.inflate(R.layout.custom_linearlayout, null);
		init();
	}

	public void init() {
		removeAllViews();

		holder = new ViewHolder();

		ImageView events = (ImageView) view.findViewById(R.id.imageView1);
		ImageView friends = (ImageView) view.findViewById(R.id.imageView2);
		ImageView mygifts = (ImageView) view.findViewById(R.id.imageView3);

		if (EventsScreen.screen == 1) {
			events.setImageResource(R.drawable.events_sel);
			friends.setImageResource(R.drawable.friends);
			mygifts.setImageResource(R.drawable.gifts);
		}
		if (EventsScreen.screen == 2) {
			events.setImageResource(R.drawable.events);
			friends.setImageResource(R.drawable.friends_sel);
			mygifts.setImageResource(R.drawable.gifts);
		}
		if (EventsScreen.screen == 3) {
			events.setImageResource(R.drawable.events);
			friends.setImageResource(R.drawable.friends);
			mygifts.setImageResource(R.drawable.gifts_sel);
		}

		events.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!(EventsScreen.screen == 1)) {
					EventsScreen.screen = 1;
					context.startActivity(new Intent(context,
							EventsScreen.class));
					((Activity) context).overridePendingTransition(
							R.anim.zoom_enter, R.anim.zoom_exit);
					((Activity) context).finish();
				}

			}
		});
		friends.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!(EventsScreen.screen == 2)) {
					EventsScreen.screen = 2;
					context.startActivity(new Intent(context,
							FriendsScreen.class));
					((Activity) context).overridePendingTransition(
							R.anim.zoom_enter, R.anim.zoom_exit);
					((Activity) context).finish();
				}

			}
		});
		mygifts.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!(EventsScreen.screen == 3)) {
					EventsScreen.screen = 3;
					context.startActivity(new Intent(context, MyGifts.class));
					((Activity) context).overridePendingTransition(
							R.anim.zoom_enter, R.anim.zoom_exit);
					((Activity) context).finish();
				}

			}
		});
		view.setTag(holder);
		addView(view);
	}

	class ViewHolder {

	}

}
