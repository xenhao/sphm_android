package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pa.common.Tracer;
import com.pa.pojo.BidDetail;
import com.pa.pojo.BidItem;
import com.pa.pojo.Merchant;

public class ParserServiceRequestDetail {
	String content;
	BidItem bid;
	public String status;
	public boolean isNextPage;
	public String next;

	public String service_icon;
	public String cancellation_policy;

	public boolean isNextPage() {
		return isNextPage;
	}

	public void setNextPage(boolean isNextPage) {
		this.isNextPage = isNextPage;
	}

	public ParserServiceRequestDetail(String s) {
		content = s;
		try {
			JSONObject obj = new JSONObject(content);
			status = obj.getString("status");
			this.cancellation_policy = obj.getString("cancellation_policy");
			Tracer.d("aaa" + this.cancellation_policy);
			try {
				this.isNextPage = Boolean.parseBoolean(obj
						.getString("next_page"));
			} catch (Exception e) {
				this.isNextPage = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public BidItem getData() {
		try {
			JSONObject root = new JSONObject(content);
			if ("success".equals(root.getString("status"))) {
				// success

				JSONObject header = root.getJSONObject("header_info");
				bid = new BidItem();

				bid.highest_bid = root.getString("highest_bid");
				;
				bid.lowest_bid = root.getString("lowest_bid");
				bid.bid_count = root.getString("bid_count");

				bid.username = header.getString("username");
				bid.service_category_name = header
						.getString("service_category_name");
				bid.title = header.getString("title");
				bid.currency = header.getString("currency");
				bid.id = header.getString("service_category_id");
				bid.serial = header.getString("serial");
				bid.service_description = header
						.getString("service_description");
				bid.service_request_type = header
						.getString("service_request_type");
				bid.service_request_status = header
						.getString("service_request_status");
				bid.service_budget_lowest = header
						.getString("service_budget_lowest");
				bid.service_budget_highest = header
						.getString("service_budget_highest");
				bid.service_request_start = header
						.getString("service_request_start");
				bid.service_request_stop = header
						.getString("service_request_stop");
				bid.service_request_currency = header
						.getString("service_request_currency");
				bid.service_request_address = header
						.getString("service_request_address");
				bid.service_request_state = header
						.getString("service_request_state");
				bid.service_request_city = header
						.getString("service_request_city");
				bid.service_request_postal_code = header
						.getString("service_request_postal_code");
				bid.voucher_serial = header.getString("voucher_serial");
				bid.other_remarks = header.getString("other_remarks");
				// bid.attachment_image": "[\"SR-BRX6IRLKOC_0.png\",\"SR-BRX6IRLKOC_1.png\",\"SR-BRX6IRLKOC_2.png\"]",
				bid.preferred_time1_start = header
						.getString("preferred_time1_start");
				bid.preferred_time1_stop = header
						.getString("preferred_time1_stop");
				bid.preferred_time2_start = header
						.getString("preferred_time2_start");
				bid.preferred_time2_stop = header
						.getString("preferred_time2_stop");
				bid.service_request_country = header
						.getString("service_request_country");
				bid.state_short = header
						.getString("state_short");

				try {
					JSONArray arrAttachment = header
							.getJSONArray("attachment_image");
					bid.arrAttachment = new ArrayList<String>();

					for (int j = 0; j < arrAttachment.length(); j++) {
						bid.arrAttachment.add(arrAttachment.getString(j));
					}
				} catch (Exception e) {
					bid.arrAttachment = new ArrayList<String>();

				}

				JSONArray arr = root.getJSONArray("result");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject detailItem = arr.getJSONObject(i);
					BidDetail detail = new BidDetail();

					detail.service_detail_name = detailItem
							.getString("service_detail_name");
					detail.service_detail_description = detailItem
							.getString("service_detail_description");
					detail.quantity = detailItem.getString("quantity");
					detail.unit_price = detailItem.getString("unit_price");
					detail.subtotal = detailItem.getString("subtotal");
					detail.param_cache = detailItem.getString("param_cache");
					detail.created_at = detailItem.getString("created_at");
					detail.updated_at = detailItem.getString("updated_at");
					bid.arrBidDetail.add(detail);
				}
				service_icon=root.getString("service_icon");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bid;
	}

}
