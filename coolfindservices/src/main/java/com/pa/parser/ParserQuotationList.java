package com.pa.parser;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.pa.pojo.BidDetail;
import com.pa.pojo.QuotationItem;
import com.pa.pojo.ServiceCategory;

public class ParserQuotationList {

	String status;
	String result;
	QuotationItem item;
	boolean next_page;
	public boolean isNext_page() {
		return next_page;
	}

	ArrayList<QuotationItem> arr;
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

	public QuotationItem getItem() {
		return item;
	}

	public void setItem(QuotationItem item) {
		this.item = item;
	}

	public ArrayList<QuotationItem> getArr() {
		return arr;
	}

	public void setArr(ArrayList<QuotationItem> arr) {
		this.arr = arr;
	}

	public ParserQuotationList(String result) {

		try {
			JsonFactory f = new JsonFactory();
			@SuppressWarnings("deprecation")
			JsonParser jp = f.createJsonParser(result);
			JsonToken token;
			token = jp.nextToken();

			String fieldname = jp.getCurrentName();
			{
				// get Question

				while (token != JsonToken.END_OBJECT) {
					fieldname = jp.getCurrentName();
					if ("next_page".equals(fieldname)) {
						jp.nextToken();
						next_page = Boolean.parseBoolean(jp.getText());
					}
					else
					if ("status".equals(fieldname)) {
						jp.nextToken();
						status = jp.getText();
					} else if ("result".equals(fieldname)) {

						// get Category
						boolean isArray = false, isLoop = true;
						arr = new ArrayList<QuotationItem>();
						while (isLoop && token != JsonToken.END_ARRAY) {
							fieldname = jp.getCurrentName();
							
							if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
								isArray = true;

							} 
							
							else if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
								item = new QuotationItem();
							} 
							
							else if ("id".equals(fieldname)) {
								jp.nextToken();
								item.id = (jp.getText());
							} else if ("user_id".equals(fieldname)) {
								jp.nextToken();
								item.user_id = (jp.getText());
							} 
							else if ("username".equals(fieldname)) {
								jp.nextToken();
								item.username = (jp.getText());
							}
							else if ("serial".equals(fieldname)) {
								jp.nextToken();
								item.serial = (jp.getText());
							} else if ("service_description".equals(fieldname)) {
								jp.nextToken();
								item.service_description = (jp.getText());
							} else if ("service_request_type".equals(fieldname)) {
								jp.nextToken();
								item.service_request_type = (jp.getText());
							} else if ("service_reqeust_status"
									.equals(fieldname)) {
								jp.nextToken();
								item.service_request_status = (jp.getText());
							} else if ("service_budget_lowest"
									.equals(fieldname)) {
								jp.nextToken();
								item.service_budget_lowest = (jp.getText());
							} else if ("service_budget_highest"
									.equals(fieldname)) {
								jp.nextToken();
								item.service_budget_highest = (jp.getText());
							} else if ("service_request_start"
									.equals(fieldname)) {
								jp.nextToken();
								item.service_request_start = (jp.getText());
							} else if ("service_request_stop".equals(fieldname)) {
								jp.nextToken();
								item.service_request_stop = (jp.getText());
							} else if ("service_request_currency"
									.equals(fieldname)) {
								jp.nextToken();
								item.service_request_currency = (jp.getText());
							} else if ("service_request_address"
									.equals(fieldname)) {
								jp.nextToken();
								item.service_request_address = (jp.getText());
							} else if ("service_request_state"
									.equals(fieldname)) {
								jp.nextToken();
								item.service_request_state = (jp.getText());
							} else if ("service_request_city".equals(fieldname)) {
								jp.nextToken();
								item.service_request_city = (jp.getText());
							} else if ("service_request_postal_code"
									.equals(fieldname)) {
								jp.nextToken();
								item.service_request_postal_code = (jp
										.getText());
							} else if ("service_request_preferred_time"
									.equals(fieldname)) {
								jp.nextToken();
								item.service_request_preferred_time = (jp
										.getText());
							} else if ("created_at".equals(fieldname)) {
								jp.nextToken();
								item.created_at = (jp.getText());
							}

							else if ("updated_at".equals(fieldname)) {
								jp.nextToken();
								item.updated_at = (jp.getText());
							}

							else if ("deleted_at".equals(fieldname)) {
								jp.nextToken();
								item.deleted_at = (jp.getText());
							} else if ("detail".equals(fieldname)) {
								boolean isArray2 = false, isLoop2 = true;

								while (isLoop2 && token != JsonToken.END_ARRAY) {
									fieldname = jp.getCurrentName();
									if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
										isArray2 = true;

									} else if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
										itemDetail = new BidDetail();
									} else if ("id".equals(fieldname)) {
										jp.nextToken();
										itemDetail.id = (jp.getText());
									} else if ("service_id".equals(fieldname)) {
										jp.nextToken();
										itemDetail.service_id = (jp.getText());
									} else if ("service_request_id"
											.equals(fieldname)) {
										jp.nextToken();
										itemDetail.service_request_id = (jp.getText());
									} else if ("service_detail_name"
											.equals(fieldname)) {
										jp.nextToken();
										itemDetail.service_detail_name = (jp.getText());
									} else if ("service_detail_description"
											.equals(fieldname)) {
										jp.nextToken();
										itemDetail.service_detail_description = (jp.getText());
									} else if ("quantity".equals(fieldname)) {
										jp.nextToken();
										itemDetail.quantity = (jp.getText());
									} else if ("unit_price".equals(fieldname)) {
										jp.nextToken();
										itemDetail.unit_price = (jp.getText());
									} else if ("subtotal".equals(fieldname)) {
										jp.nextToken();
										itemDetail.subtotal = (jp.getText());
									} else if ("param_cache".equals(fieldname)) {
										jp.nextToken();
										itemDetail.param_cache = (jp.getText());
									} else if ("created_at".equals(fieldname)) {
										jp.nextToken();
										itemDetail.created_at = (jp.getText());
									} else if ("updated_at".equals(fieldname)) {
										jp.nextToken();
										itemDetail.updated_at = (jp.getText());
									} else if ("deleted_at".equals(fieldname)) {
										jp.nextToken();
										itemDetail.deleted_at = (jp.getText());
									} else if (token == JsonToken.END_OBJECT) {
										if (itemDetail.id != null
												&& itemDetail.id.length() > 0)
											item.arrBidDetail.add(itemDetail);

										if (!isArray2)
											isLoop2 = false;
									}
									if (isLoop2)
										token = jp.nextToken();

								}
							}

							else if (token == JsonToken.END_OBJECT) {
								if (item.serial != null && item.serial.length() > 0)
									arr.add(item);

								if (!isArray)
									isLoop = false;
							}
							if (isLoop)
								token = jp.nextToken();

						}
						// System.out.println("LOOP DATA FINISH");

					}
					token = jp.nextToken();

				}

			}

			jp.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
