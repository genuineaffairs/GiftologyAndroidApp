package com.unikove.giftology.caching;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.util.Log;

public class CachingUtility {

	private static int CACHINGTIME = 2 * 60 * 60 * 1000;

	private static boolean isInternetAvailable(Context context) {
		try {
			ConnectivityManager connec = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);
			android.net.NetworkInfo wifi = connec
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			android.net.NetworkInfo mobile = connec
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (wifi.isConnected()) {
				return true;
			} else if (mobile.isConnected()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			//Log.i("Giftology.Debug", "In caching utility exception");
			return false;
		}
	}

	public static synchronized StringBuilder getHttpEntityStringBuilder(
			Context context) {

		if (isInternetAvailable(context)) {
			return null;
		}

		File cacheFile = new File(context.getFilesDir(), "cachefile");

		if (cacheFile.exists()
				&& cacheFile.canRead()
				&& (cacheFile.lastModified() >= (Calendar.getInstance()
						.getTimeInMillis() - CACHINGTIME))) {
			try {
				// Log.i("Giftology.Debug","Reading cache file");
				FileInputStream fileInputStream = new FileInputStream(cacheFile);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						fileInputStream);
				ObjectInputStream objectInputStream = new ObjectInputStream(
						bufferedInputStream);
				StringBuilder sb = (StringBuilder) objectInputStream
						.readObject();
				objectInputStream.close();
				// Log.i("Giftology.Debug","Reading cache file, SB: "+
				// sb.toString());
				return sb;

			} catch (FileNotFoundException e) {
//				Log.i("Giftology.Debug",
//						"Reading cache file Failed File httpEntityString");
				return null;
			} catch (IOException e) {
//				Log.i("Giftology.Debug",
//						"Reading cache file Failed IO httpEntityString " + e);
				return null;
			} catch (ClassNotFoundException e) {
//				Log.i("Giftology.Debug",
//						"Reading cache file Failed Class httpEntityString");
				return null;
			}
		} else {
			return null;
		}

	}

	public static synchronized void setHttpEntityStringBuilder(Context context,
			StringBuilder httpEntityString) {
		File cacheFile = new File(context.getFilesDir(), "cachefile");
		// Log.i("Giftology.Debug",httpEntityString.toString());
		// Log.i("Giftology.Debug","Cache file location:" +
		// cacheFile.getAbsolutePath());

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					fileOutputStream);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					bufferedOutputStream);
			objectOutputStream.writeObject(httpEntityString);
			// Log.i("Giftology.Debug","Writing cache file");
			objectOutputStream.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
//			Log.i("Giftology.Debug",
//					"Writing cache file fail IO httpEntityString" + e + "\n"
//							+ httpEntityString.getClass().getCanonicalName());
		}
	}

	public static synchronized void deleteCacheFile(Context context) {
		File cacheFile = new File(context.getFilesDir(), "cachefile");
		cacheFile.delete();
		cacheFile = new File(context.getFilesDir(), "usernamecachefile");
		cacheFile.delete();
		cacheFile = new File(context.getFilesDir(), "Bitmapcachefile");
		cacheFile.delete();
	}

	public static synchronized void setUserName(String username, Context context) {
		File cacheFile = new File(context.getFilesDir(), "usernamecachefile");

//		Log.i("Giftology.Debug",
//				"Cache file location:" + cacheFile.getAbsolutePath());

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					fileOutputStream);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					bufferedOutputStream);
			objectOutputStream.writeObject(username);
			//Log.i("Giftology.Debug", "Writing cache file for username");
			objectOutputStream.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
//			Log.i("Giftology.Debug", "Writing cache file fail IO username" + e
//					+ "\n" + username.getClass().getCanonicalName());
		}
	}

	public static synchronized String getUsername(Context context) {

		File cacheFile = new File(context.getFilesDir(), "usernamecachefile");

		if (cacheFile.exists()
				&& cacheFile.canRead()
				&& (cacheFile.lastModified() >= (Calendar.getInstance()
						.getTimeInMillis() - CACHINGTIME))) {
			try {

				//Log.i("Giftology.Debug", "Reading cache file for username");
				FileInputStream fileInputStream = new FileInputStream(cacheFile);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						fileInputStream);
				ObjectInputStream objectInputStream = new ObjectInputStream(
						bufferedInputStream);
				String username = (String) objectInputStream.readObject();
				objectInputStream.close();
				// Log.i("Giftology.Debug","Reading cache file, SB: "+
				// username);
				return username;

			} catch (FileNotFoundException e) {
				/*Log.i("Giftology.Debug",
						"Reading cache file Failed File username");*/
				return null;
			} catch (IOException e) {
				/*Log.i("Giftology.Debug",
						"Reading cache file Failed IO username" + e);*/
				return null;
			} catch (ClassNotFoundException e) {
				/*Log.i("Giftology.Debug",
						"Reading cache file Failed Class username");*/
				return null;
			}
		} else {
			return null;
		}

	}

	public static synchronized void setBitmap(Bitmap bitmap, Context context) {
		File cacheFile = new File(context.getFilesDir(), "Bitmapcachefile");

		/*Log.i("Giftology.Debug",
				"Cache file location:" + cacheFile.getAbsolutePath());*/

		try {

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
			byte bitmapBytes[] = byteStream.toByteArray();

			FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					fileOutputStream);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					bufferedOutputStream);
			objectOutputStream.write(bitmapBytes, 0, bitmapBytes.length);
			Log.i("Giftology.Debug", "Writing cache file for Bitmap");
			objectOutputStream.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
		/*	Log.i("Giftology.Debug", "Writing cache file fail IO bitmap " + e
					+ "\n" + bitmap.getClass().getCanonicalName());*/
		}
	}

	public static synchronized Bitmap getBitmap(Context context) {

		File cacheFile = new File(context.getFilesDir(), "Bitmapcachefile");

		if (cacheFile.exists()
				&& cacheFile.canRead()
				&& (cacheFile.lastModified() >= (Calendar.getInstance()
						.getTimeInMillis() - CACHINGTIME))) {
			try {

			//	Log.i("Giftology.Debug", "Reading cache file for Bitmap");
				FileInputStream fileInputStream = new FileInputStream(cacheFile);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						fileInputStream);
				ObjectInputStream objectInputStream = new ObjectInputStream(
						bufferedInputStream);

				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				int b;
				while ((b = objectInputStream.read()) != -1)
					byteStream.write(b);
				byte bitmapBytes[] = byteStream.toByteArray();
				Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0,
						bitmapBytes.length);

				objectInputStream.close();
				// Log.i("Giftology.Debug","Reading cache file bitmap , SB: "+
				// Bitmap);
				return bitmap;

			} catch (FileNotFoundException e) {
			/*	Log.i("Giftology.Debug",
						"Reading cache file Failed File bitmap ");*/
				return null;
			} catch (IOException e) {
				//Log.i("Giftology.Debug", "Reading cache file Failed IO bitmap "
					//	+ e);
				return null;
			}
		} else {
			return null;
		}

	}

}
