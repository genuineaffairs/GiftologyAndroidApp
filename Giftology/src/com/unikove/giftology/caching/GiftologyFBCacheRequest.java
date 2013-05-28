package com.unikove.giftology.caching;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.util.List;
import java.util.Map;

class GiftologyFBCacheRequest extends CacheRequest {
FileOutputStream fos;
File file;
public GiftologyFBCacheRequest(String filename,
   Map<String, List<String>> rspHeaders) {
   try {
        File file = new File(filename);
        fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(rspHeaders);
   } catch (Exception ex) {
        throw new RuntimeException(ex.getMessage());
   }
}
public OutputStream getBody() throws IOException {
   return fos;
}

public void abort() {
   // we abandon the cache by close the stream,
   // and delete the file
   try {
	fos.close();
} catch (IOException e) {
	// TODO Auto-generated catch block
	
}
   file.delete();
 }
}