package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pa.common.Tracer;
import com.pa.pojo.BidDetail;

public class ParserBidDetail {

	String status;
	String result;
	public String service_icon;
	boolean next_page;
	public String highest_bid, bid_count, lowest_bid, state_short;
	public String cancellation_policy;

	ArrayList<BidDetail> arrBid=new ArrayList<BidDetail>();
	private BidDetail itemDetail;
	public boolean isNext_page() {
		return next_page;
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

	public ArrayList<BidDetail> getArr() {
		return arrBid;
	}

	public void setArr(ArrayList<BidDetail> arr) {
		this.arrBid = arr;
	}

	public ParserBidDetail(String result) {

		try {
			

			JSONObject root = new JSONObject(result);
			status=root.getString("status");
			if ("success".equals(root.getString("status"))) {
				// success

				highest_bid=root.getString("highest_bid");;
				lowest_bid=root.getString("lowest_bid");
				bid_count=root.getString("bid_count");

                JSONObject header_info = root.getJSONObject("header_info");
                state_short = header_info.getString("state_short");
		        
		        JSONArray arr=root.getJSONArray("result");
		        for(int i=0;i<arr.length();i++){
		        	JSONObject detailItem=arr.getJSONObject(i);

		        	BidDetail detail=new BidDetail();
		        	detail.service_id=detailItem.getString("service_id");
		        	detail.service_detail_name=detailItem.getString("service_detail_name");
		            detail.service_detail_description=detailItem.getString("service_detail_description");
		            detail.quantity=detailItem.getString("quantity");
		            detail.unit_price=detailItem.getString("unit_price");
		            detail.subtotal=detailItem.getString("subtotal");
		            detail.param_cache=detailItem.getString("param_cache");
		            detail.created_at=detailItem.getString("created_at");
		            detail.updated_at=detailItem.getString("updated_at");
		            arrBid.add(detail);
		        	Tracer.d(""+i+detail.service_detail_name);
		        }
		        
		        cancellation_policy=root.getString("cancellation_policy");
		        service_icon=root.getString("service_icon");
		        
			}
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
