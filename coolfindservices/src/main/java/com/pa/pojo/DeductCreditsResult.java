package com.pa.pojo;

public class DeductCreditsResult {
	public DeductCreditsResult(){}
	
	public String status, reason, message;
	public Result result;
	
	public static class Result{
		public String credit_serial, order_amount, use_credit, final_amount, balance_credit, condition;
	}
	
	
}
