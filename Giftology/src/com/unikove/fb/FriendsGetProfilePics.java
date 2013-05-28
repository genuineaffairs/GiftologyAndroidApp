package com.unikove.fb;

import java.util.Hashtable;
import java.util.Stack;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;

import com.unikove.giftology.EventsScreen;
import com.unikove.giftology.FriendsScreen;

/*
 * Fetch friends profile pictures request via AsyncTask
 */
public class FriendsGetProfilePics {

	Hashtable<String, Bitmap> friendsImages;
	Hashtable<String, String> positionRequested;
	BaseAdapter listener;
	int runningCount = 0;
	Stack<ItemPair> queue;

	/*
	 * 15 max async tasks at any given time.
	 */
	final static int MAX_ALLOWED_TASKS = 15;

	public FriendsGetProfilePics() {
		friendsImages = new Hashtable<String, Bitmap>();
		positionRequested = new Hashtable<String, String>();
		queue = new Stack<ItemPair>();
	}

	/*
	 * Inform the listener when the image has been downloaded. listener is
	 * FriendsList here.
	 */
	public void setListener(BaseAdapter listener) {
		this.listener = listener;
		reset();
	}

	public void reset() {
		positionRequested.clear();
		runningCount = 0;
		queue.clear();
	}

	/*
	 * If the profile picture has already been downloaded and cached, return it
	 * else execute a new async task to fetch it - if total async tasks >15,
	 * queue the request.
	 */
	public Bitmap getImage(String uid, String url) {
		Bitmap image = friendsImages.get(uid);
		if (image != null) {
			return image;
		}
		if (!positionRequested.containsKey(uid)) {
			positionRequested.put(uid, "");
			if (runningCount >= MAX_ALLOWED_TASKS) {
				queue.push(new ItemPair(uid, url));
			} else {
				runningCount++;
				
				try {
					new GetProfilePicAsyncTask().execute(uid, url);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				
				}
			}
		}
		return null;
	}

	public void getNextImage() {
		if (!queue.isEmpty()) {
			ItemPair item = queue.pop();
			try {
				new GetProfilePicAsyncTask().execute(item.uid, item.url);
			} catch (Exception e) {
			
			}
		}
	}

	/*
	 * Start a AsyncTask to fetch the request
	 */
	private class GetProfilePicAsyncTask extends AsyncTask<Object, Void, Bitmap> {
		String uid;

		@Override
		protected Bitmap doInBackground(Object... params) {
			this.uid = (String) params[0];
			String url = (String) params[1];
			return Utility.getBitmap(url);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			runningCount--;
			if (result != null) {
				try {
					FriendsScreen.callnotify();
					friendsImages.put(uid, result);
					//                listener.notifyDataSetChanged();
					if(EventsScreen.flagScreen.equals("Events")){
						EventsScreen.callnotify();
					}
					if(EventsScreen.flagScreen.equals("SendGift")){
						if(listener!=null){
							listener.notifyDataSetChanged();
						}
					}
					getNextImage();
				} catch (Exception e) {
				//	Log.i("Giftology.Debug",e.toString());
					
				}
			}
		}
	}

	class ItemPair {
		String uid;
		String url;

		public ItemPair(String uid, String url) {
			this.uid = uid;
			this.url = url;
		}
	}

}
