package com.unikove.giftology.caching;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.util.Log;


public class CachingUtility {

	public static final String TAG="Giftology.Debug";
	private static long httpCacheSize=5 * 1024 * 1024; 
	public static void createCacheDir(Context context)
	{
		 try {
	           File httpCacheDir = new File(context.getCacheDir(), "http");
	           
	           HttpResponseCache.install(httpCacheDir, httpCacheSize);
	          
		 }catch (IOException e) {
	          // Log.i(TAG, "HTTP response cache installation failed:" + e);
	       }
	
		 try {
	           File httpCacheDir = new File(context.getCacheDir(), "http");
	         
	           Class.forName("android.net.http.HttpResponseCache")
	                   .getMethod("install", File.class, long.class)
	                   .invoke(null, httpCacheDir, httpCacheSize);
		   }
	        catch (Exception httpResponseCacheNotAvailable) {
	        	
	        	// Log.i(TAG,"HTTP CACHE UNAVAILABLE "+httpResponseCacheNotAvailable);
	       }

	}
	
	public HttpURLConnection getCachedConnection(URL url)
	{
		return null;
	}
	public static void deleteCache(){
		HttpResponseCache cache = HttpResponseCache.getInstalled();
	       if (cache != null) {
	           cache.flush();
	       }
	}
	
}
