package com.pa.pojo;

public class DealBraintreeClientToken {
	public DealBraintreeClientToken(){};
	
	public String status,status_code;
    public Result result;

    public class Result {
        public Result(){}
        public String customerId, token;
    }
	
}
