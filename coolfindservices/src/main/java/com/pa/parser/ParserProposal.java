package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pa.common.Tracer;
import com.pa.pojo.Merchant;
import com.pa.pojo.Proposal;

public class ParserProposal {
	String content;
	ArrayList<Proposal> arrItem = new ArrayList<Proposal>();
	
	public String status;
	
	
	public ParserProposal(String s) {
		content = s;
		try{
		JSONObject obj = new JSONObject(content);
		status=obj.getString("status");
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public ArrayList<Proposal> getData() {
		try {
			JSONObject root = new JSONObject(content);
			if ("success".equals(root.getString("status"))) {
				// success


				JSONArray arr = new JSONArray(root.getString("result"));

				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					Proposal proposal=new Proposal();
					proposal.merchant_username=obj.getString("merchant_username");
					proposal.customer_username=obj.getString("customer_username");
					proposal.service_request_serial=obj.getString("service_request_serial");
					proposal.serial=obj.getString("serial");
					proposal.service_description=obj.getString("service_description");
					proposal.service_proposal_type=obj.getString("service_proposal_type");
					proposal.service_proposal_status=obj.getString("service_proposal_type");
					proposal.total=obj.getString("total");
					proposal.discount_percent=obj.getString("discount_percent");
					proposal.discount_amount=obj.getString("discount_amount");
					proposal.created_at=obj.getString("created_at");
					proposal.updated_at=obj.getString("updated_at");
					proposal.deleted_at=obj.getString("deleted_at");
					proposal.request_title=obj.getString("request_title");
					proposal.merchant_company_name=obj.getString("merchant_company_name");
					proposal.merchant_address=obj.getString("merchant_address");
					proposal.merchant_company_service_cache=obj.getString("merchant_company_service_cache");
					proposal.co_overall_rating=obj.getString("co_overall_rating");
					proposal.other_remarks=obj.getString("other_remarks");
					proposal.preferred_time1_start=obj.getString("preferred_time1_start");
					proposal.preferred_time2_start=obj.getString("preferred_time2_start");
					proposal.merchant_city=obj.getString("merchant_city");
					proposal.merchant_state=obj.getString("merchant_state");
					proposal.merchant_country=obj.getString("merchant_country");
					proposal.co_rating_count=obj.getString("co_rating_count");
					try{
						proposal.is_verified=obj.getString("is_verified");

                        // Get merchant submitted images
                        JSONArray arrAttachment=obj.getJSONArray("attachment_image");
                        proposal.arrAttachment=new ArrayList<String>();

                        for(int j=0;j<arrAttachment.length();j++){
                            proposal.arrAttachment.add(arrAttachment.getString(j));
                        }
					}catch(Exception e){
                        proposal.arrAttachment=new ArrayList<String>();
					}
					
					arrItem.add(proposal);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrItem;
	}

}
