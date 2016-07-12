package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pa.pojo.JobItem;

public class ParserJob {
	String content;
	ArrayList<JobItem> arrItem = new ArrayList<JobItem>();

	public int total_unread;
	public String status;
	public boolean has_next;

	public ParserJob(String s) {
		content = s;
		try {
			JSONObject obj = new JSONObject(content);
			status = obj.getString("status");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<JobItem> getPromoData() {
		try {
			JSONObject object = new JSONObject(content);

			if (!object.getString("status").equalsIgnoreCase("success")) {
				return arrItem;
			}

			has_next 		= object.getBoolean("hasNext");
			JSONArray array = object.getJSONArray("result");

			for (int i = 0, ilen = array.length(); i < ilen; i++) {
				JSONObject iObject 	= array.getJSONObject(i);
				JobItem item 		= new JobItem();

				item.customer_username 			= iObject.getString("customer_username");
				item.updated_at 				= iObject.getString("updated_at");
				item.created_at 				= iObject.getString("created_at");
				item.deleted_at 				= iObject.getString("deleted_at");
				item.discount_amount 			= iObject.getString("discount_amount");
				item.discount_percent 			= iObject.getString("discount_percent");
				item.merchant_username 			= iObject.getString("merchant_username");
				item.merchant_verification_code = iObject.getString("merchant_verification_code");
				item.serial 					= iObject.getString("serial");
				item.service_description 		= iObject.getString("service_description");
				item.service_order_status 		= iObject.getString("service_order_status");
				item.service_order_type 		= iObject.getString("service_order_type");
				item.service_proposal_serial 	= iObject.getString("service_proposal_serial");
				item.total 						= iObject.getString("total");
				item.ori_total 					= iObject.getString("ori_total");
				item.promo_code 				= iObject.getString("promo_code");
				item.request_title 				= iObject.getString("request_title");
				item.service_order_address 		= iObject.getString("service_order_address");
				item.service_order_state 		= iObject.getString("service_order_state");
				item.service_order_city 		= iObject.getString("service_order_city");
				item.service_order_postal_code 	= iObject.getString("service_order_postal_code");
				item.merchant_verification_code = iObject.getString("merchant_verification_code");
				item.customer_approval_code 	= iObject.getString("customer_approval_code");
				item.preferred_time1_start 		= iObject.getString("preferred_time1_start");
				item.preferred_time1_stop 		= iObject.getString("preferred_time1_stop");
				item.preferred_time2_start 		= iObject.getString("preferred_time2_start");
				item.preferred_time2_stop 		= iObject.getString("preferred_time2_stop");

				arrItem.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrItem;
	}

	public ArrayList<JobItem> getPackageData() {

		try {
			JSONObject object = new JSONObject(content);

			if (!object.getString("status").equalsIgnoreCase("success")) {
				return arrItem;
			}

			has_next 		= object.getBoolean("hasNext");
			JSONArray array = object.getJSONArray("result");

			for (int i = 0, ilen = array.length(); i < ilen; i++) {
				JSONObject iObject 	= array.getJSONObject(i);
				JobItem item 		= new JobItem();

				item.customer_username 			= iObject.getString("customer_username");
				item.updated_at 				= iObject.getString("updated_at");
				item.created_at 				= iObject.getString("created_at");
				item.deleted_at 				= iObject.getString("deleted_at");
				item.discount_amount 			= iObject.getString("discount_amount");
				item.discount_percent 			= iObject.getString("discount_percent");
				item.merchant_username 			= iObject.getString("merchant_username");
				item.merchant_verification_code = iObject.getString("merchant_verification_code");
				item.serial 					= iObject.getString("serial");
				item.service_description 		= iObject.getString("service_description");
				item.service_order_status 		= iObject.getString("service_order_status");
				item.service_order_type 		= iObject.getString("service_order_type");
				item.service_proposal_serial 	= iObject.getString("service_proposal_serial");
				item.total 						= iObject.getString("total");
				item.ori_total 					= iObject.getString("ori_total");
				item.request_title 				= iObject.getString("request_title");
				item.service_order_address 		= iObject.getString("service_order_address");
				item.service_order_state 		= iObject.getString("service_order_state");
				item.service_order_city 		= iObject.getString("service_order_city");
				item.service_order_postal_code 	= iObject.getString("service_order_postal_code");
				item.merchant_verification_code = iObject.getString("merchant_verification_code");
				item.customer_approval_code 	= iObject.getString("customer_approval_code");
				item.preferred_time1_start 		= iObject.getString("preferred_time1_start");
				item.preferred_time1_stop 		= iObject.getString("preferred_time1_stop");

				arrItem.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrItem;

	}

	public ArrayList<JobItem> getData() {
		try {
			JSONObject obj = new JSONObject(content);
			if ("success".equals(obj.getString("status"))) {
				total_unread = obj.getInt("total_unread");
				has_next = Boolean.parseBoolean(obj.getString("next_page"));

				JSONArray arr = new JSONArray(obj.getString("result"));

				for (int i = 0; i < arr.length(); i++) {
					JSONObject curr = arr.getJSONObject(i);

					JobItem item = new JobItem();
					item.customer_username = curr
							.getString("customer_username");
					item.created_at = curr.getString("created_at");
					// item.customer_approval_code=curr.getString("customer_approval_code");
					item.deleted_at = curr.getString("deleted_at");
					item.discount_amount = curr.getString("discount_amount");
					item.discount_percent = curr.getString("discount_percent");
					item.merchant_username = curr
							.getString("merchant_username");
					item.merchant_verification_code = curr
							.getString("merchant_verification_code");
					item.serial = curr.getString("serial");
					item.service_description = curr
							.getString("service_description");
					item.service_order_status = curr
							.getString("service_order_status");
					item.service_order_type = curr
							.getString("service_order_type");
					item.service_proposal_serial = curr
							.getString("service_proposal_serial");
					item.total = curr.getString("total");
					item.updated_at = curr.getString("updated_at");

					item.company_name = curr.getString("company_name");
					item.request_title = curr.getString("request_title");
					item.service_order_address = curr
							.getString("service_order_address");
					try{
					item.service_order_country = curr
							.getString("service_order_country");
					}catch(Exception e){
						
					}
					item.service_order_state = curr
							.getString("service_order_state");
					item.service_order_city = curr
							.getString("service_order_city");
					item.service_order_postal_code = curr
							.getString("service_order_postal_code");
					item.read_status = curr.getString("read_status");

					item.merchant_verification_code = curr
							.getString("merchant_verification_code");
					item.customer_approval_code = curr
							.getString("customer_approval_code");
					item.preferred_time1_start = curr
							.getString("preferred_time1_start");
					item.preferred_time1_stop = curr
							.getString("preferred_time1_stop");
					item.preferred_time2_start = curr
							.getString("preferred_time2_start");
					item.preferred_time2_stop = curr
							.getString("preferred_time2_stop");
					item.is_verified = curr.getString("is_verified");
					item.service_icon=curr.getString("service_icon");
					try {
						JSONArray arrAttachment = curr
								.getJSONArray("attachment_image");
						item.arrAttachment = new ArrayList<String>();

						for (int j = 0; j < arrAttachment.length(); j++) {
							item.arrAttachment.add(arrAttachment.getString(j));
						}
					} catch (Exception e) {
						item.arrAttachment = new ArrayList<String>();

					}
					
					try{
						item.related_job_id=curr.getString("related_job_id");
					}catch(Exception e){
						
					}

					arrItem.add(item);

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrItem;
	}

}
