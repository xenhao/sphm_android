package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.pa.pojo.BidDetail;
import com.pa.pojo.BidItem;

public class ParserBidList {

	String status;
	String result;
	BidItem item;
	boolean next_page;
	public int total_unread;
	
	
	public boolean isNext_page() {
		return next_page;
	}

	ArrayList<BidItem> arr;
	private BidDetail itemDetail;

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

	public BidItem getItem() {
		return item;
	}

	public void setItem(BidItem item) {
		this.item = item;
	}

	public ArrayList<BidItem> getArr() {
		return arr;
	}

	public void setArr(ArrayList<BidItem> arr) {
		this.arr = arr;
	}
	
	public ParserBidList(String result){
		arr=new ArrayList<BidItem>();
		try {
			JSONObject root = new JSONObject(result);
			status=root.getString("status");
			if ("success".equals(root.getString("status"))) {
				// success

				
				
						
		        
		        JSONArray arrJSON=root.getJSONArray("result");
		        for(int i=0;i<arrJSON.length();i++){
		        	JSONObject header=arrJSON.getJSONObject(i);
		        	
					item=new BidItem();
					
					item.username=header.getString("username");
			        item.title=header.getString("title");
			        item.currency=header.getString("currency");
			        item.serial=header.getString("serial");
			        item.service_icon=header.getString("service_icon");

			        try{
			        	item.description=header.getString("description");
			        	item.service_order_serial=header.getString("service_order_serial");
			        	item.merchant=header.getString("merchant");
			        	item.merchant_company_name=header.getString("merchant_company_name");
			        	item.merchant_contact=header.getString("merchant_contact");
			        	item.merchant_email=header.getString("merchant_email");
			        	item.price=header.getString("price");

			        }catch(Exception e){
			        	
			        }
			        
			        try{
			        	item.service_category_name=header.getString("service_category_name");
			        	item.id=header.getString("service_category_id");
				        item.service_description=header.getString("service_description");
				        item.service_request_type=header.getString("service_request_type");
				        item.service_request_status=header.getString("service_request_status");
				        item.service_budget_lowest=header.getString("service_budget_lowest");
				        item.service_budget_highest=header.getString("service_budget_highest");
				        item.service_request_start=header.getString("service_request_start");
				        item.service_request_stop=header.getString("service_request_stop");
				        item.service_request_currency=header.getString("service_request_currency");
				        item.service_request_address=header.getString("service_request_address");
				        item.service_request_state=header.getString("service_request_state");
				        item.service_request_city=header.getString("service_request_city");
				        item.service_request_postal_code=header.getString("service_request_postal_code");
				        item.voucher_serial=header.getString("voucher_serial");
				        item.other_remarks=header.getString("other_remarks");
				        //bid.attachment_image": "[\"SR-BRX6IRLKOC_0.png\",\"SR-BRX6IRLKOC_1.png\",\"SR-BRX6IRLKOC_2.png\"]",
				        item.preferred_time1_start=header.getString("preferred_time1_start");
				        item.preferred_time1_stop=header.getString("preferred_time1_stop");
				        item.preferred_time2_start=header.getString("preferred_time2_start");
				        item.preferred_time2_stop=header.getString("preferred_time2_stop");
				        item.service_request_country=header.getString("service_request_country");
				        //item.has_submitted_proposal=header.getString("has_submitted_proposal");
				        item.bid_count=header.getString("bid_count");
						item.updated_at=header.getString("updated_at");

			        }catch(Exception e){
			        	
			        }
			        try{
			        JSONArray arrAttachment=header.getJSONArray("attachment_image");
			        item.arrAttachment=new ArrayList<String>();

			        for(int j=0;j<arrAttachment.length();j++){
			        	item.arrAttachment.add(arrAttachment.getString(j));
			        }
			        }catch(Exception e){
				        item.arrAttachment=new ArrayList<String>();
			        	
			        }

			        arr.add(item);
		        }
		        
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

//	public ParserBidList(String result) {
//		Log.v("debug","start");
//		try {
//			JsonFactory f = new JsonFactory();
//			@SuppressWarnings("deprecation")
//			JsonParser jp = f.createJsonParser(result);
//			JsonToken token;
//			token = jp.nextToken();
//
//			String fieldname = jp.getCurrentName();
//			{
//				// get Question
//
//				while (token != JsonToken.END_OBJECT) {
//					fieldname = jp.getCurrentName();
//					if ("next_page".equals(fieldname)) {
//						jp.nextToken();
//						next_page = Boolean.parseBoolean(jp.getText());
//					}
//					else
//					if ("status".equals(fieldname)) {
//						jp.nextToken();
//						status = jp.getText();
//					} else if ("result".equals(fieldname)) {
//
//						// get Category
//						boolean isArray = false, isLoop = true;
//						arr = new ArrayList<BidItem>();
//						while (isLoop && token != JsonToken.END_ARRAY) {
//							fieldname = jp.getCurrentName();
//							
//							if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
//								isArray = true;
//
//							} 
//							
//							else if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
//								item = new BidItem();
//							} 
//							else if ("id".equals(fieldname)) {
//								
//								jp.nextToken();
//								item.id = (jp.getText());
//
//							} else if ("title".equals(fieldname)) {
//								jp.nextToken();
//								item.title = (jp.getText());
//							} 
//							else if ("user_id".equals(fieldname)) {
//								jp.nextToken();
//								item.user_id = (jp.getText());
//							} 
//							else if ("username".equals(fieldname)) {
//								jp.nextToken();
//								item.username = (jp.getText());
//							} 
//							else if ("serial".equals(fieldname)) {
//								jp.nextToken();
//								item.serial = (jp.getText());
//
//							} else if ("service_description".equals(fieldname)) {
//								jp.nextToken();
//								item.service_description = (jp.getText());
//								Log.v("debug",item.service_description);
//
//							} else if ("service_request_type".equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_type = (jp.getText());
//							} else if ("service_request_status"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_status = (jp.getText());
//							} else if ("service_budget_lowest"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.service_budget_lowest = (jp.getText());
//							} else if ("service_budget_highest"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.service_budget_highest = (jp.getText());
//							} else if ("service_request_start"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_start = (jp.getText());
//							} else if ("service_request_stop".equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_stop = (jp.getText());
//							} else if ("service_request_currency"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_currency = (jp.getText());
//							} else if ("service_request_address"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_address = (jp.getText());
//							} else if ("service_request_state"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_state = (jp.getText());
//							} else if ("service_request_city".equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_city = (jp.getText());
//							} else if ("service_request_postal_code"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_postal_code = (jp
//										.getText());
//							} else if ("service_request_preferred_time"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.service_request_preferred_time = (jp
//										.getText());
//							} 
//							else if ("preferred_time2_start"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.preferred_time2_start = (jp
//										.getText());
//							} 
//							else if ("preferred_time1_start"
//									.equals(fieldname)) {
//								jp.nextToken();
//								item.preferred_time1_start = (jp
//										.getText());
//							}
//							else if ("created_at".equals(fieldname)) {
//								jp.nextToken();
//								item.created_at = (jp.getText());
//							}
//
//							else if ("updated_at".equals(fieldname)) {
//								jp.nextToken();
//								item.updated_at = (jp.getText());
//							}
//
//							else if ("deleted_at".equals(fieldname)) {
//								jp.nextToken();
//								item.deleted_at = (jp.getText());
//							} else if ("detail".equals(fieldname)) {
//								boolean isArray2 = false, isLoop2 = true;
//
//								while (isLoop2 && token != JsonToken.END_ARRAY) {
//									fieldname = jp.getCurrentName();
//									if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
//										isArray2 = true;
//
//									} else if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
//										itemDetail = new BidDetail();
//									} else if ("id".equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.id = (jp.getText());
//									} else if ("service_id".equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.service_id = (jp.getText());
//									} else if ("service_request_id"
//											.equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.service_request_id = (jp.getText());
//									} else if ("service_detail_name"
//											.equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.service_detail_name = (jp.getText());
//									} else if ("service_detail_description"
//											.equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.service_detail_description = (jp.getText());
//									} else if ("quantity".equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.quantity = (jp.getText());
//									} else if ("unit_price".equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.unit_price = (jp.getText());
//									} else if ("subtotal".equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.subtotal = (jp.getText());
//									} else if ("param_cache".equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.param_cache = (jp.getText());
//									} else if ("created_at".equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.created_at = (jp.getText());
//									} else if ("updated_at".equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.updated_at = (jp.getText());
//									} else if ("deleted_at".equals(fieldname)) {
//										jp.nextToken();
//										itemDetail.deleted_at = (jp.getText());
//									} else if (token == JsonToken.END_OBJECT) {
//										if (itemDetail.id != null
//												&& itemDetail.id.length() > 0)
//											item.arrBidDetail.add(itemDetail);
//
//										if (!isArray2)
//											isLoop2 = false;
//									}
//									if (isLoop2)
//										token = jp.nextToken();
//
//								}
//							}
//
//							else if (token == JsonToken.END_OBJECT) {
//								if (item.serial != null && item.serial.length() > 0)
//									arr.add(item);
//
//								if (!isArray)
//									isLoop = false;
//							}
//							
//							if (isLoop)
//								token = jp.nextToken();
//
//						}
//						// System.out.println("LOOP DATA FINISH");
//
//					}
//					token = jp.nextToken();
//
//				}
//
//			}
//
//			jp.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

}
