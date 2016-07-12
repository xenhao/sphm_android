package com.pa.pojo;

public class UserFreeCreditsResult {
	public UserFreeCreditsResult(){}
	
	public String status;
	public Result result;
	
	public static class Result{
		public String cs_username, cs_credit;
	}
	
	
}
