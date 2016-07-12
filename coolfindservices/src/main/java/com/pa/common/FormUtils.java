package com.pa.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class FormUtils {

	public static String printEmptyFormValue(String s){
		if(TextUtils.isEmpty(s)){
			return " NA ";
		}else{
			return s;
		}
	}
	public static String printTradeName(String a,String b){
		if(!TextUtils.isEmpty(a)){
			return a;
		}else{
			return b;
		}
	}
	
	public static String printPrice(String currency,String price){
		if(TextUtils.isEmpty(price)){
			return "NA";
		}
		else{
			return currency+" "+price;
		}
	}
	public static void dialNumber(Activity a,String url) {
		if(!url.contains("tel"))url="tel:"+url;
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse(url));
		a.startActivity(intent);
	}
	
}
