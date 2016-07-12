package com.pa.pojo;

import java.util.ArrayList;

public class Merchant {
	public String id, co_name,co_trade_name, co_about, username, bulk_children_array,
			co_address_1;
	public ArrayList<ParentServiceCategory> arrService = new ArrayList<ParentServiceCategory>();

	public String co_overall_rating,is_favourite,is_verified;
	
	public boolean isFavourite(){
		return "Y".equals(is_favourite);
	}
	
	public boolean isVerified(){
		return "Y".equals(is_verified);
		//return true;
	}
	    

}
