package com.pa.pojo;

import java.util.ArrayList;

public class BidItem {
	//public String category,id,date,time;
	

	public String title,id,username,user_id,serial,service_description,service_request_type,service_request_status,service_budget_lowest;

	public String service_budget_highest,service_request_start,service_request_stop,service_request_currency,service_request_address,service_request_city;
	public String service_request_state,service_request_postal_code,service_request_preferred_time,created_at,updated_at,deleted_at;
	public String preferred_time2_start;
	public String preferred_time1_start;
	
	public ArrayList<BidDetail>arrBidDetail=new ArrayList<BidDetail>();
	public String read_status;
	public String highest_bid,bid_count,lowest_bid;

	public String service_category_name;

	public String currency;

	public String service_category_id;

	public String voucher_serial;

	public String other_remarks;

	public String preferred_time1_stop;

	public String preferred_time2_stop;

	public String service_request_country;

    public String state_short;

	public ArrayList<String> arrAttachment;

	public String has_submitted_proposal;
	public String service_icon;
	
	//vo
	public String description,service_order_serial,merchant,merchant_company_name,merchant_contact,merchant_email,price;
	
	public BidItem(){
		
	}
	
	public BidItem(String category,String id,String date,String time){
//		this.category=category;
//		this.id=id;
//		this.date=date;
//		this.time=time;
	}
	
	
}
