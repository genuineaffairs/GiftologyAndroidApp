package com.unikove.giftology.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.ConnectivityManager;

import com.unikove.giftology.KeyGenerator;

public class ConnectionUtility {

/*	public static  HttpEntity getHttpEntity(String url) {
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					GiftologyUtility.SERVERTIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams,
					GiftologyUtility.SERVERTIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpContext localContext = new BasicHttpContext();
			// Log.i("Giftology.Debug", client.toString());
			HttpPost httpPost = new HttpPost(KeyGenerator.encodedURL(url));

			HttpResponse response = client.execute(httpPost, localContext);

			HttpEntity entity = response.getEntity();
			return entity;
		} catch (Exception e) {
			return null;
		}
	}
*/
	public   HttpEntity getHttpEntityForPost(String url,
			org.apache.http.HttpEntity inputEntity) {
		try {
			// Log.i("Giftology.Debug","In getHttpEntityForPost" +
			// inputEntity.toString());
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					GiftologyUtility.SERVERTIMEOUT);
			
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpContext localContext = new BasicHttpContext();
			// Log.i("Giftology.Debug", client.toString());
			HttpPost httpPost = new HttpPost(KeyGenerator.encodedURL(url));
			if (inputEntity != null) {
				httpPost.setEntity((HttpEntity) inputEntity);
			}
			HttpResponse response = client.execute(httpPost, localContext);

			HttpEntity entity = response.getEntity();
			// Log.i("Giftology.Debug","In getHttpEntityForPost" +
			// entity.toString());
			return entity;
		} catch (Exception e) {
			return null;
		}
	}

	public   StringBuilder parseEntity(HttpEntity httpEntity)
			throws IllegalStateException, IOException {
		if (httpEntity != null) {
			InputStream instream = httpEntity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					instream));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				// Log.i("Giftology.Debug",line);
			}
			return sb;
		} else {
			return null;
		}
	}

	public   StringBuilder getGiftologyDataGet(String urlstring) throws IOException
	{
		HttpURLConnection urlConnection = null ;
		URL url;
		InputStream in = null;
		try {
			
			// Log.i(GiftologyUtility.TAG,"1");

			url=new URL(KeyGenerator.encodedURL(urlstring));
			 				
				 //  Log.i(GiftologyUtility.TAG,"2");
				urlConnection= (HttpURLConnection) url.openConnection();
				  // Log.i(GiftologyUtility.TAG,"3");
				urlConnection.setDefaultUseCaches(true);
				// Log.i(GiftologyUtility.TAG,"4");
				urlConnection.addRequestProperty("Cache-Control", "max-stale=" + 24 * 60 * 60);
				 //Log.i(GiftologyUtility.TAG,"5");
				urlConnection.setConnectTimeout(GiftologyUtility.SERVERTIMEOUT);
	      in = new BufferedInputStream(urlConnection.getInputStream());
		//	Log.i(GiftologyUtility.TAG,"URL CONNECTION CONTENT TYPE"+urlConnection.getHeaderFields().toString());
			
			//Log.i(GiftologyUtility.TAG,"6");
	    
	     return readStream(in);
	   }
		finally {
			in.close();
	     urlConnection.disconnect();
	   }
	}
	
	public   StringBuilder getGiftologyDataGetKeyLess(String urlstring) throws IOException
	{
		HttpURLConnection urlConnection = null ;
		URL url;
		InputStream in=null;
		try {
			
			  // Log.i(GiftologyUtility.TAG,"1");
			url=new URL(urlstring);
			 //  Log.i(GiftologyUtility.TAG,"2");
			urlConnection= (HttpURLConnection) url.openConnection();
			 //  Log.i(GiftologyUtility.TAG,"3");
			
			// Log.i(GiftologyUtility.TAG,"4");
			
			urlConnection.addRequestProperty("Cache-Control", "min-fresh="+2*60*60);
			urlConnection.setConnectTimeout(GiftologyUtility.SERVERTIMEOUT);
			urlConnection.setDefaultUseCaches(true);
			// Log.i(GiftologyUtility.TAG,"5");
			//Log.i(GiftologyUtility.TAG,"URL CONNECTION CONTENT TYPE"+urlConnection.getHeaderFields().toString());
			//urlConnection.setConnectTimeout(GiftologyUtility.SERVERTIMEOUT);
	     in = new BufferedInputStream(urlConnection.getInputStream());
		// Log.i(GiftologyUtility.TAG,"6");
	     return readStream(in);
	   }catch(Exception e) 
	   {
	//	   Log.i(GiftologyUtility.TAG,e.toString());
		 
		   return null;
	   }
		finally {
			in.close();
	     urlConnection.disconnect();
	   }
	}
	
	private   StringBuilder readStream(InputStream in) {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);	
			}
		} catch (IOException e) {
	
			// Log.i(GiftologyUtility.TAG,"CRASH!!");
			return null;
		}
		 //Log.i(GiftologyUtility.TAG,sb.toString());
		return sb;
		
		//return null;
	}

	
	public   StringBuilder getGiftologyDataGetKeyLessforNotification(String urlstring) throws IOException
	{
		HttpURLConnection urlConnection = null ;
		URL url;
		InputStream in=null;
		try {
			
			  // Log.i(GiftologyUtility.TAG,"1");
			url=new URL(urlstring);
			 //  Log.i(GiftologyUtility.TAG,"2");
			urlConnection= (HttpURLConnection) url.openConnection();
			 //  Log.i(GiftologyUtility.TAG,"3");
			
			// Log.i(GiftologyUtility.TAG,"4");
			urlConnection.setConnectTimeout(GiftologyUtility.SERVERTIMEOUT);
			urlConnection.addRequestProperty("Cache-Control", "min-fresh="+2*60*60);
			
			urlConnection.setDefaultUseCaches(true);
			// Log.i(GiftologyUtility.TAG,"5");
			//Log.i(GiftologyUtility.TAG,"URL CONNECTION CONTENT TYPE"+urlConnection.getHeaderFields().toString());
			//urlConnection.setConnectTimeout(GiftologyUtility.SERVERTIMEOUT);
	      in = new BufferedInputStream(urlConnection.getInputStream());
		// Log.i(GiftologyUtility.TAG,"6");
	     return readStream(in);
	   }catch(Exception e) 
	   {
		  // Log.i(GiftologyUtility.TAG,e.toString());
		 
		   return null;
	   }
		finally {
			in.close();
	     urlConnection.disconnect();
	   }	
	
	}
	
	/*public static String getGiftologyDataPost(URL url)
	{
		   HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		   try {
		     urlConnection.setDoOutput(true);
		     urlConnection.setChunkedStreamingMode(0);

		     OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
		     writeStream(out);

		     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
		     readStream(in);
		   }
		    finally {
		     urlConnection.disconnect();
		   }
		 
	}*/

/*	public static  HttpEntity getHttpEntityKeyless(String url) {
		// TODO Auto-generated method stub

		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					GiftologyUtility.SERVERTIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams,
					GiftologyUtility.SERVERTIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpContext localContext = new BasicHttpContext();
			// Log.i("Giftology.Debug", client.toString());
			HttpPost httpPost = new HttpPost(url);

			HttpResponse response = client.execute(httpPost, localContext);
			
			HttpEntity entity = response.getEntity();
			return entity;
		} catch (Exception e) {
			return null;
		}
		// return null;
	}

	public static  HttpEntity getHttpEntityGet(String url) {
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					GiftologyUtility.SERVERTIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams,
					GiftologyUtility.SERVERTIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpContext localContext = new BasicHttpContext();
			// Log.i("Giftology.Debug", client.toString());
			HttpGet httpGet = new HttpGet(url);

			HttpResponse response = client.execute(httpGet, localContext);

			HttpEntity entity = response.getEntity();
			return entity;
		} catch (Exception e) {
			return null;
		}
		// return null;
	}
*/
	public static  boolean isConnectedToServer(String url) {
		try {
			URL serverURL = new URL(url);
			URLConnection urlconn = serverURL.openConnection();
			urlconn.setConnectTimeout(GiftologyUtility.SERVERTIMEOUT);
			urlconn.connect();
			return true;
		} catch (IOException e) {

		} catch (IllegalStateException e) {

		}
		return false;

	}

	public static  boolean isInternetAvailable(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connec
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		android.net.NetworkInfo mobile = connec
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isConnected() || mobile.isConnected()) {
			return isConnectedToServer(GiftologyUtility.GIFTOLOGYSERVER);
		}

		return false;
	}
}
