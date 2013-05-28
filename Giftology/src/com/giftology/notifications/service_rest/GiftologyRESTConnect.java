package com.giftology.notifications.service_rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.util.Log;

import com.unikove.giftology.KeyGenerator;

public class GiftologyRESTConnect implements RESTConnect {

	private  final String WSWEBURL="http://giftology.com/";
	private  final String RAND="sumit";
	private  final String KEY="8c744f645901c282a5ef6aa3451e672c";
	private  final String URLRAND="&rand="+RAND+"&";
	private  final String URLKEY="key="+KEY;
	private  final String URLRANDKEY=URLRAND+URLKEY;
	private  final String NEWCELEBRATIONCONNECTOR="reminders/ws_reminder_today.json?user_fb_id=";
	private  final String NEWFRIENDCONNECTOR="users/ws_latest_friend_joined.json?user_fb_id=";
	private  final String NEWGIFTCONNECTOR="gifts/ws_latest_gift.json?user_fb_id=";
	private  final String FRIENDNAME="friend_name";
	private  final String FIRSTNAME="first_name";
	private  final String LASTNAME="last_name";
	private  final String PRODUCTID="product_id";
	private  final String SENDERFIRSTNAME="sender_first_name";
	private  final String SENDERLASTNAME="sender_last_name";
	private  final String EMPTYSTRING="";
	private final String GIFT="gift";
	private final String BIRTHDAY="today_birthday";
	private final String LATESTFRIEND="latest_friend";
	private final String REMINDER="Reminder";
	private final String YOURFRIENDS="Your Friends";
	
	
	@Override
	public String new_celebration(String userId) {
		
		String connectURL=WSWEBURL+NEWCELEBRATIONCONNECTOR+userId+URLRANDKEY;
		
		String json=connectRest(connectURL);
		
		return processCelebrationJson(json);
	}

	private String processCelebrationJson(String json) {
		// TODO Auto-generated method stub
		
		try{
			
		Object object= JSONValue.parse(json);
		
		JSONObject jsonObject=(JSONObject)object;
		
		JSONArray subjsonArray=(JSONArray)jsonObject.get(BIRTHDAY);
		//picking the first birthday only.
		
		if(subjsonArray.size()>1)
		{
			return YOURFRIENDS;
		}
		JSONObject subsubjsonObject=(JSONObject)subjsonArray.get(0);
	
		JSONObject reminder=(JSONObject)subsubjsonObject.get(REMINDER);
		if(subsubjsonObject.toJSONString().contains(FRIENDNAME))
		{
			
			//return jsonObject.get("gift").ge;
			
			return reminder.get(FRIENDNAME).toString();
		}
		}catch(Exception e)
		{
			
			return EMPTYSTRING;
		}
		
		return EMPTYSTRING;
	}

	
	
	
	@Override
	public String new_friend(String userId) {
		// TODO Auto-generated method stub
		String connectURL=WSWEBURL+NEWFRIENDCONNECTOR+userId+URLRANDKEY;
		String json=connectRest(connectURL);
		return processFriendJson( json);
	}

	private String processFriendJson(String json) {
		// TODO Auto-generated method stub
		
			try{
			
			Object object= JSONValue.parse(json);
			
			JSONObject jsonObject=(JSONObject)object;
			JSONObject subjsonObject=(JSONObject)jsonObject.get(LATESTFRIEND);
			
			if(jsonObject.toJSONString().contains(FIRSTNAME))
			{
				
				//return jsonObject.get("gift").ge;
				return subjsonObject.get(FIRSTNAME)+" "+subjsonObject.get(LASTNAME);
			}
			}catch(Exception e)
			{
				
				return EMPTYSTRING;
			}
			
			return EMPTYSTRING;
	}

	@Override
	public String new_gift(String userId) {
		
		String connectURL=WSWEBURL+NEWGIFTCONNECTOR+userId+URLRANDKEY;
		
		String json=connectRest(connectURL);
		
		return processGiftJson(json);
	}

	private String processGiftJson(String json) {
		try{
			
			Object object= JSONValue.parse(json);
			//Log.i("Giftology.Notification",json);
			JSONObject jsonObject=(JSONObject)object;
			JSONObject subjsonObject=(JSONObject)jsonObject.get(GIFT);
			
			if(jsonObject.toJSONString().contains(SENDERFIRSTNAME))
			{
				
				
				//return jsonObject.get("gift").ge;
				return subjsonObject.get(PRODUCTID)+";"+subjsonObject.get(SENDERFIRSTNAME)+" "+subjsonObject.get(SENDERLASTNAME);
			}
			}catch(Exception e)
			{
				
				return EMPTYSTRING;
			}
			
			return EMPTYSTRING;
	}

	
	
	
	private String connectRest(String connectURL)
	{
		HttpResponse response=null;
		try {
			response=connectWS(connectURL);
		} catch (ClientProtocolException e) {
			
			return EMPTYSTRING;
		} catch (IOException e) {
			
			return EMPTYSTRING;
		}
		
		
		return decodeResponse(response);
	}
	
	private HttpResponse connectWS(String connectURL) throws ClientProtocolException, IOException
	{
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,30000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpGet httpGet = new HttpGet(connectURL);
		HttpResponse response = client.execute(httpGet);
		
		return response;
	}
	
	private String decodeResponse(HttpResponse response)
	{
		StringBuilder stringbuffer;	
		try{
		HttpEntity entity=response.getEntity();
		InputStream instream = entity.getContent();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream));
		stringbuffer = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			stringbuffer.append(line);
		} 
		}catch(Exception e)
		{
			return EMPTYSTRING;
		}
		
		return stringbuffer.toString();
	}
	
	
}
