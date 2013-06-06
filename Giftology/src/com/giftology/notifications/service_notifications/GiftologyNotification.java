package com.giftology.notifications.service_notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.unikove.giftology.R;

public class GiftologyNotification extends Notification {

	String title = null;
	String text = null;
	int smallIcon = 0;
	Bitmap bigIcon = null;
	private final String NOTIFICATION_TOP_MESSAGE = "You have a reason to celebrate!";

	public Notification getNotification(String message, Context context,
			PendingIntent pIntent) {

		// check message and then extract
		createMessage(message, context);
		Notification noti = new NotificationCompat.Builder(context)
				.setContentTitle(title).setContentText(text)
				.setLargeIcon(bigIcon).setSmallIcon(smallIcon)
				.setContentIntent(pIntent).build();
		// Vibrate and show lights
		noti.defaults |= Notification.DEFAULT_VIBRATE;
		noti.flags |= Notification.FLAG_SHOW_LIGHTS;

		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		// do something to get
		return noti;

	}

	private void createMessage(String message, Context context) {

		// TODO Auto-generated method stub right code

		this.text = message;
		this.title = NOTIFICATION_TOP_MESSAGE;
		this.smallIcon = com.unikove.giftology.R.drawable.ic_launcher;
		this.bigIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);

	}

}
