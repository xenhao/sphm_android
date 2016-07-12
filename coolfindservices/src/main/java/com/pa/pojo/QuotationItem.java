package com.pa.pojo;

import java.util.ArrayList;

public class QuotationItem {
	//public String category,id,date,time;
	

	public String id,username,user_id,serial,service_description,service_request_type,service_request_status,service_budget_lowest;

	public String service_budget_highest,service_request_start,service_request_stop,service_request_currency,service_request_address,service_request_city;
	public String service_request_state,service_request_postal_code,service_request_preferred_time,created_at,updated_at,deleted_at;
	
	public ArrayList<BidDetail>arrBidDetail=new ArrayList<BidDetail>();
	
	public QuotationItem(){
		
	}
	
	public QuotationItem(String category,String id,String date,String time){
//		this.category=category;
//		this.id=id;
//		this.date=date;
//		this.time=time;
	}
	
	
}
