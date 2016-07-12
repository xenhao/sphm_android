package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.pa.pojo.BidDetail;
import com.pa.pojo.JobDetail;
import com.pa.pojo.JobItem;

public class ParserJobDetail {

	String status;
	String result;
	boolean next_page;
	public JobItem item;
	
	public String cancellation_policy;

	public boolean isNext_page() {
		return next_page;
	}

	ArrayList<JobDetail> arr=new ArrayList<JobDetail>();
	private JobDetail itemDetail;

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

	public ArrayList<JobDetail> getArr() {
		return arr;
	}

	public void setArr(ArrayList<JobDetail> arr) {
		this.arr = arr;
	}

	public ParserJobDetail(String content) {
		try {
			JSONObject obj = new JSONObject(content);
			status=obj.getString("status");
	        
			if ("success".equals(obj.getString("status"))) {

				JSONObject curr = obj.getJSONObject("header_info");

				item = new JobItem();
				
				item.customer_username = curr.getString("customer_username");
				item.created_at = curr.getString("created_at");
				// item.customer_approval_code=curr.getString("customer_approval_code");
				item.deleted_at = curr.getString("deleted_at");
				item.discount_amount = curr.getString("discount_amount");
				item.discount_percent = curr.getString("discount_percent");
				item.merchant_username = curr.getString("merchant_username");
				item.merchant_verification_code = curr
						.getString("merchant_verification_code");
				item.serial = curr.getString("serial");
				item.service_description = curr
						.getString("service_description");
				item.service_order_status = curr
						.getString("service_order_status");
				item.service_order_type = curr.getString("service_order_type");
				item.service_proposal_serial = curr
						.getString("service_proposal_serial");
				item.total = curr.getString("total");
				item.updated_at = curr.getString("updated_at");

				//item.company_name = curr.getString("company_name");
				item.request_title = curr.getString("request_title");
				item.service_order_address = curr
						.getString("service_order_address");
				item.service_order_state = curr
						.getString("service_order_state");
				item.service_order_city = curr.getString("service_order_city");
				item.service_order_postal_code = curr
						.getString("service_order_postal_code");
				item.currency=curr.getString("currency");
				item.cs_address = curr
						.getString("cs_address");
				item.cs_home_number = curr
						.getString("cs_home_number");
				item.cs_mobile_number = curr.getString("cs_mobile_number");
				item.cs_name = curr
						.getString("cs_name");
				item.co_main_contact_number=curr.getString("co_main_contact_number");
				
		        item.preferred_time1_start=curr.getString("preferred_time1_start");
		        item.preferred_time1_stop=curr.getString("preferred_time1_stop");
		        item.preferred_time2_start=curr.getString("preferred_time2_start");
		        item.preferred_time2_stop=curr.getString("preferred_time2_stop");
				item.cs_email=curr.getString("cs_email");
				item.other_remarks=curr.getString("other_remarks");
				JSONArray jsonArr=obj.getJSONArray("result");
				for(int i=0;i<jsonArr.length();i++){

		        	JSONObject detailItem=jsonArr.getJSONObject(i);

		        	JobDetail detail=new JobDetail();
		        	detail.service_id=detailItem.getString("service_id");
		        	detail.service_detail_name=detailItem.getString("service_detail_name");
		            detail.service_detail_description=detailItem.getString("service_detail_description");
		            detail.quantity=detailItem.getString("quantity");
		            detail.unit_price=detailItem.getString("unit_price");
		            detail.subtotal=detailItem.getString("subtotal");
		            detail.param_cache=detailItem.getString("param_cache");
		            detail.created_at=detailItem.getString("created_at");
		            detail.updated_at=detailItem.getString("updated_at");
		            arr.add(detail);
		        
				}

			}
			
			this.cancellation_policy=obj.getString("cancellation_policy");


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
