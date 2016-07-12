package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pa.common.Tracer;
import com.pa.pojo.Merchant;

public class ParserMerchant {
	String content;
	ArrayList<Merchant> arrItem = new ArrayList<Merchant>();

	public String status;
	public boolean isNextPage;
	public String next;

	public boolean isNextPage() {
		return isNextPage;
	}

	public void setNextPage(boolean isNextPage) {
		this.isNextPage = isNextPage;
	}

	public ParserMerchant(String s) {
		content = s;
		try {
			JSONObject obj = new JSONObject(content);
			status = obj.getString("status");
			this.isNextPage = Boolean.parseBoolean(obj.getString("next_page"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayList<Merchant> getData() {
		try {
			JSONObject root = new JSONObject(content);
			if ("success".equals(root.getString("status"))) {
				// success

				JSONArray arr = new JSONArray(root.getString("result"));

				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);

					Merchant item = new Merchant();
					item.id = obj.getString("id");
					item.co_name = obj.getString("co_name");
					item.co_about = obj.getString("co_about");
					item.username = obj.getString("username");
					item.bulk_children_array = obj
							.getString("bulk_children_array");
					item.co_address_1=obj.getString("co_address_1");
					item.co_overall_rating=obj.getString("co_overall_rating");
					item.is_favourite=obj.getString("is_favourite");
					item.is_verified=obj.getString("is_verified");
					String param_cache = obj.getString("co_service_name_cache");
					Tracer.d("debug",""+ i + param_cache);
					item.arrService = new ParserParentServiceCategoryVMerchant(
							param_cache, 2).getArr();
					Tracer.d("debug",""+ i + item.arrService.size());
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
