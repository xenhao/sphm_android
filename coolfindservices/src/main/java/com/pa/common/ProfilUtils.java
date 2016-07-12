package com.pa.common;

import android.app.Activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.parser.ParserUserORM;
import com.pa.pojo.UserORM;

public class ProfilUtils implements Config{
	protected static AppPreferences pref;
	Activity activity;
	
	public ProfilUtils(Activity activity) {
		this.activity = activity;
		pref = new AppPreferences(activity);

	}
	
	public static void resetUser(){
		pref.savePref(PREF_USER,"");
	}

	public static void saveUser(UserORM user) {
		try {
			pref.savePref(PREF_USER,
					new ObjectMapper().writeValueAsString(user));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static UserORM getUser() {
		try {
			return new ParserUserORM(pref.getPref(PREF_USER)).getData();
		} catch (Exception e) {
			return null;
		}
	}
	
}
