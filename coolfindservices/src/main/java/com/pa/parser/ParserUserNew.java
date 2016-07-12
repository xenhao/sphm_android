package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.pa.pojo.ServiceCategory;
import com.pa.pojo.User;

public class ParserUserNew extends ParserBaseClass{
	
	String status;
	String result;
	String type;
	User item;
	ArrayList<User> arr;
	
	public String getType() {
		return type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public User getItem() {
		return item;
	}

	public void setItem(User item) {
		this.item = item;
	}

	public ArrayList<User> getArr() {
		return arr;
	}

	public void setArr(ArrayList<User> arr) {
		this.arr = arr;
	}

	public ParserUserNew(String result) {
		
		try {
			JSONObject root=new JSONObject(result);
			this.status=root.getString("status");
			
			JSONObject resultObj=root.getJSONObject("result");
			
			if("success".equals(status)){
				item=new User();
				item.activation_token=getString(resultObj,"activation_token");
				item.active_session_token=getString(resultObj,"active_session_token");
				item.agreement_token=getString(resultObj,"agreement_token");
				item.am_email=getString(resultObj,"am_email");
				item.co_about=getString(resultObj,"co_about");
				item.co_address_1=getString(resultObj,"co_address_1");
				item.co_address_2=getString(resultObj,"co_address_2");
				item.co_all_address_cache=getString(resultObj,"co_all_address_cache");
				item.co_business_day=getString(resultObj,"co_business_day");
				item.co_business_description=getString(resultObj,"co_business_description");
				item.co_business_hour_end=getString(resultObj,"co_business_hour_end");
				item.co_business_hour_start=getString(resultObj,"co_business_hour_start");
				item.co_city=getString(resultObj,"co_city");
				item.co_contact_person_email=getString(resultObj,"co_contact_person_email");
				item.co_contact_person_name=getString(resultObj,"co_contact_person_name");
				item.co_contact_person_number=getString(resultObj,"co_contact_person_number");
				item.co_country=getString(resultObj,"co_country");
				item.co_facebook_url=getString(resultObj,"co_facebook_url");
				item.co_instagram_url=getString(resultObj,"co_instagram_url");
				item.co_linkedin_url=getString(resultObj,"co_linkedin_url");
				item.co_main_business_number=getString(resultObj,"co_main_business_number");
				item.co_main_contact_email=getString(resultObj,"co_main_contact_email");
				item.co_main_contact_number=getString(resultObj,"co_main_contact_number");
				item.co_main_email=getString(resultObj,"co_main_email");
				item.co_name=getString(resultObj,"co_name");
				item.co_postal_code=getString(resultObj,"co_postal_code");
				item.co_profile_pic=getString(resultObj,"co_profile_pic");
				item.co_registration_number=getString(resultObj,"co_registration_number");
				item.co_service_id_cache=getString(resultObj,"co_service_id_cache");
				item.co_service_name_cache=getString(resultObj,"co_service_name_cache");
				item.co_state=getString(resultObj,"co_state");
				item.co_twitter_url=getString(resultObj,"co_twitter_url");
				item.co_website_url=getString(resultObj,"co_website_url");
				item.created_at=getString(resultObj,"created_at");
				item.cs_country=getString(resultObj,"cs_country");
				item.cs_address=getString(resultObj,"cs_address");
				item.cs_city=getString(resultObj,"cs_city");
				item.cs_email=getString(resultObj,"cs_email");
				item.cs_home_number=getString(resultObj,"cs_home_number");
				item.cs_mobile_number=getString(resultObj,"cs_mobile_number");
				item.cs_name=getString(resultObj,"cs_name");
				item.cs_postal_code=getString(resultObj,"cs_postal_code");
				item.cs_state=getString(resultObj,"cs_state");
				item.deleted_at=getString(resultObj,"deleted_at");
				item.forgot_password_token=getString(resultObj,"forgot_password_token");
				item.id=getString(resultObj,"id");
				item.is_active=getString(resultObj,"is_active");
				item.language=getString(resultObj,"language");
				item.name=getString(resultObj,"name");
				item.paypal_credit_card_id=getString(resultObj,"paypal_credit_card_id");
				item.remember_token=getString(resultObj,"remember_token");
				item.type=getString(resultObj,"type");
				item.updated_at=getString(resultObj,"updated_at");
				item.username=getString(resultObj,"username");
				item.usertype=getString(resultObj,"usertype");
				
				
			}else{
				item=null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
}
