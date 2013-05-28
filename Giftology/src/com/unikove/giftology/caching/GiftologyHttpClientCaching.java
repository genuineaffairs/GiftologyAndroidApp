package com.unikove.giftology.caching;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.unikove.giftology.KeyGenerator;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.cache.CacheResponseStatus;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.client.cache.CacheConfig;
import ch.boye.httpclientandroidlib.impl.client.cache.CachingHttpClient;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

public class GiftologyHttpClientCaching {

public  HttpEntity callCaching(URL url) {
 CacheConfig cacheConfig = new CacheConfig();
 cacheConfig.setMaxCacheEntries(1000);
 cacheConfig.setMaxObjectSizeBytes(1024 * 1024);
 
 HttpParams httpParams = new BasicHttpParams();
	HttpConnectionParams.setConnectionTimeout(httpParams,30000);
	HttpConnectionParams.setSoTimeout(httpParams, 30000);

 HttpClient cachingClient = new CachingHttpClient(new DefaultHttpClient(httpParams), cacheConfig);

 HttpContext localContext = new BasicHttpContext();

 sendRequest(cachingClient, localContext,url);
 CacheResponseStatus responseStatus = (CacheResponseStatus) localContext.getAttribute(
 CachingHttpClient.CACHE_RESPONSE_STATUS);
 //checkResponse(responseStatus);


 HttpEntity httpEntity= sendRequest(cachingClient, localContext,url);
 return httpEntity;
 /*responseStatus = (CacheResponseStatus) localContext.getAttribute(
 CachingHttpClient.CACHE_RESPONSE_STATUS);
 checkResponse(responseStatus);*/
}

 HttpEntity sendRequest(HttpClient cachingClient, HttpContext localContext,URL url) {
 HttpPost httpPost;
try {
	httpPost = new HttpPost(KeyGenerator.encodedURL(url.toURI().toString()));
} catch (URISyntaxException e) {
	// TODO Auto-generated catch block
	return null;
}
 HttpResponse response = null;
 try {
 response = cachingClient.execute(httpPost, localContext);
 } catch (ClientProtocolException e1) {

	 return null;
 } catch (IOException e1) {
 // TODO Auto-generated catch block
return null;
 }
 HttpEntity entity = response.getEntity();
 try {
	entity.consumeContent();
} catch (IOException e) {
	// TODO Auto-generated catch block
	//e.printStackTrace();
}
 return entity;

}

 void checkResponse(CacheResponseStatus responseStatus) {
 switch (responseStatus) {
 case CACHE_HIT:
 System.out.println("A response was generated from the cache with no requests "
 + "sent upstream");
 break;
 case CACHE_MODULE_RESPONSE:
 System.out.println("The response was generated directly by the caching module");
 break;
 case CACHE_MISS:
 System.out.println("The response came from an upstream server");
 break;
 case VALIDATED:
 System.out.println("The response was generated from the cache after validating "
 + "the entry with the origin server");
 break;
 }
}

}