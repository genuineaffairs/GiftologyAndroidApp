package com.unikove.giftology.caching;

import java.io.IOException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import java.util.Map;

class GiftologyFBCache extends ResponseCache {
	 public CacheResponse
	 get(URI uri, String rqstMethod, Map rqstHeaders)
	   throws IOException {
	   // get the response from a cached file if available

	        return new GiftologyFBCacheResponse("file1.cache");

	 }
	public CacheRequest put(URI uri, URLConnection conn)
	   throws IOException {
	   // save cache to a file
	   // 1. serialize headers into file2.cache
	   // 2. write data to file2.cache

	       return new GiftologyFBCacheRequest("file2.cache",conn.getHeaderFields());
	  
	 }
}