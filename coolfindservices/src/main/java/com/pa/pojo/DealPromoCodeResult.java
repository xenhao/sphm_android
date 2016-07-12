package com.pa.pojo;


public class DealPromoCodeResult {
	public DealPromoCodeResult(){}
	
	public String status, status_code, message;
	public Result result;
	
	public static class Result{
		public String discounted_amount,original_amount,promotion_serial,promo_code;
	}
	
	
}
