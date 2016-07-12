package com.pa.pojo;

import java.util.ArrayList;

public class PromoCode {
	public PromoCode(){};
	
	public String status;
	public Result result;
	public ArrayList<String> reason;
	
	public static class Result{
		public Result(){};
		public String currency,value,promo_value_type;
	}
	
	
}
