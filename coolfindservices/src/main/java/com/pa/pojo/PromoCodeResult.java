package com.pa.pojo;

import java.util.ArrayList;

public class PromoCodeResult {
	public PromoCodeResult(){}
	
	public String status;
	public Result result;
	public String reason;
	public String message;
	
	public static class Result{
		public String service_proposal_serial,current_amt,new_amt,discounted_amt;
	}
	
	
}
