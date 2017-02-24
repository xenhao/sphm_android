package com.pa.common;

import com.pa.pojo.User;

import java.util.ArrayList;

public class GlobalVar {
	public static User USER;
	
	public static boolean animateLogo=true;
	public static boolean isRegister=false;
	public static String mobile_number="";
	public static String a,b;

	public static String username;
	public static String email,first_name,last_name,fbid;
	public static boolean isFB;
	public static boolean isGuest = false;
	public static boolean isResumeGuest = false;
	
	public static String country;
	public static String state;
	public static String state_short;

	//	these are created to cater for the extremely slow loading time of category filters in main packages page
	public static String[] package_category = { "" };
	public static ArrayList<String> package_category_id = new ArrayList<>();
	public static ArrayList<String> package_category_name = new ArrayList<>();
}
