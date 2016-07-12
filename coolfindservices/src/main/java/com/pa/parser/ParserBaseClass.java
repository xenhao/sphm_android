package com.pa.parser;

import org.json.JSONException;
import org.json.JSONObject;

public class ParserBaseClass {

	
	public String getString(JSONObject obj,String TAG ){
		try {
			return obj.getString(TAG);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
