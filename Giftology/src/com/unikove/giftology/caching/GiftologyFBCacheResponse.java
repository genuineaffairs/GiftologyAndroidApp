package com.unikove.giftology.caching;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.CacheResponse;
import java.util.List;
import java.util.Map;

class GiftologyFBCacheResponse extends CacheResponse {
FileInputStream fis;
Map<String, List<String>> headers;
public GiftologyFBCacheResponse(String filename) {
        try {
         fis = new FileInputStream(new File(filename));
         ObjectInputStream ois = new ObjectInputStream (fis);
         headers = (Map<String, List<String>>)  ois.readObject();
   } catch (IOException ex) {
        // handle exception
   } catch (ClassNotFoundException e) {
	
}
}

public InputStream getBody() throws IOException {
   return fis;
}

 public Map getHeaders() throws IOException {
   return headers;
 }
}