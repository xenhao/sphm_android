package com.pa.common;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

import io.intercom.android.sdk.Intercom;

public class PARestClient  {
	  private static final String BASE_URL =Config.MAIN_URI ;

	  private static AsyncHttpClient client = new AsyncHttpClient();

	  final static String TAG = "PARestClient";

	  public static void get(String SERVER,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		  Log.d(TAG, "========================================================================");
		  Log.d(TAG, "GET " + getAbsoluteUrl(SERVER, url));
          if (params != null){
              params.add("source_type", "sphm");
		    Log.d(TAG, "Params: " + params.toString());
          }
		  Log.d(TAG, "------------------------------------------------------------------------");
	      client.get(getAbsoluteUrl(SERVER, url), params, responseHandler);
	      //client.setMaxRetriesAndTimeout(3, 60000);
	      client.setTimeout(60000);

          Map userMap = new HashMap<>();
          userMap.put("update_last_request_at", true);
          userMap.put("new_session", true);

          Intercom.client().updateUser(userMap);
	  }

	  public static void post(String SERVER,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		  Log.d(TAG, "========================================================================");
		  Log.d(TAG, "POST "+getAbsoluteUrl(SERVER,url));
          if (params != null){
              params.add("source_type", "sphm");
              Log.d(TAG, "Params: " + params.toString());
          }
		  Log.d(TAG, "------------------------------------------------------------------------");
	      client.post(getAbsoluteUrl(SERVER, url), params, responseHandler);
	      //client.setMaxRetriesAndTimeout(3, 120000);

	      client.setTimeout(120000);

          Map userMap = new HashMap<>();
          userMap.put("update_last_request_at", true);
          userMap.put("new_session", true);

          Intercom.client().updateUser(userMap);
	  }

	  public static String getAbsoluteUrl(String SERVER,String relativeUrl) {
		  String base="";

		  // TODO Configure Server
//		  SERVER="0";

	      if("0".equals(SERVER)){
	    	 base= "https://" + Config.DOMAIN_DEV + "/";
	      }else
	      if("1".equals(SERVER))
	      {
	    	  
		    	 base= "https://" + Config.DOMAIN_STAGING + "/";
	      }
	      else
		      if("2".equals(SERVER))
		      {
		    	  
			    	 base= "https://" + Config.DOMAIN_LIVE + "/";
		      }

		  return base + relativeUrl.replace(BASE_URL, "");
	  }

    public static void dealGet(String SERVER,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.d(TAG, "========================================================================");
        Log.d(TAG, "GET " + getDealAbsoluteUrl(SERVER, url));
        if (params != null){
            params.add("source_type", "sphm");
            Log.d(TAG, "Params: " + params.toString());
        }
        Log.d(TAG, "------------------------------------------------------------------------");
        client.get(getDealAbsoluteUrl(SERVER, url), params, responseHandler);
        //client.setMaxRetriesAndTimeout(3, 60000);
        client.setTimeout(60000);

        Map userMap = new HashMap<>();
        userMap.put("update_last_request_at", true);
        userMap.put("new_session", true);

        Intercom.client().updateUser(userMap);
    }

    public static void dealPost(String SERVER,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.d(TAG, "========================================================================");
        Log.d(TAG, "POST "+getDealAbsoluteUrl(SERVER, url));
        if (params != null){
            params.add("source_type", "sphm");
            Log.d(TAG, "Params: " + params.toString());
        }
        Log.d(TAG, "------------------------------------------------------------------------");
        client.post(getDealAbsoluteUrl(SERVER, url), params, responseHandler);

        client.setTimeout(120000);

        Map userMap = new HashMap<>();
        userMap.put("update_last_request_at", true);
        userMap.put("new_session", true);

        Intercom.client().updateUser(userMap);
    }

    public static String getDealAbsoluteUrl(String SERVER, String relativeUrl) {

        String domain = Config.DOMAIN_DEAL_LIVE;

        if("0".equals(SERVER))
            domain = Config.DOMAIN_DEAL_DEV;
        else if("1".equals(SERVER))
            domain = Config.DOMAIN_DEAL_STAGING;
        else if("2".equals(SERVER))
            domain = Config.DOMAIN_DEAL_LIVE;

        return "https://" + domain + "/" + relativeUrl;
    }

	}