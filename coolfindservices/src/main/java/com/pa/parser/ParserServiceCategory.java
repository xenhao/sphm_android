package com.pa.parser;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.pa.pojo.ServiceCategory;

public class ParserServiceCategory {
	
	String status;
	String result;
	ServiceCategory item;
	ArrayList<ServiceCategory> arr;
	
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

	public ServiceCategory getItem() {
		return item;
	}

	public void setItem(ServiceCategory item) {
		this.item = item;
	}

	public ArrayList<ServiceCategory> getArr() {
		return arr;
	}

	public void setArr(ArrayList<ServiceCategory> arr) {
		this.arr = arr;
	}

	public ParserServiceCategory(String result) {
		
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
						if ("status".equals(fieldname)) {
							jp.nextToken();
							status=jp.getText();
						}else if ("result".equals(fieldname)) {

							// get Category
							boolean isArray=false,isLoop=true;
							arr=new ArrayList<ServiceCategory>();
							while (isLoop && token != JsonToken.END_ARRAY) {
								fieldname = jp.getCurrentName();
								if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
									isArray=true;
									
								}else						
								if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
									item = new ServiceCategory();
								} else if ("id".equals(fieldname)) {
									jp.nextToken();
									item.id=(jp.getText());
								}  
								else if ("service_name".equals(fieldname)) {
									jp.nextToken();
									item.service_name=(jp.getText());
								}  
							
																
								else if (token == JsonToken.END_OBJECT) {
									if(item.id!=null && item.id.length()>0)
									arr.add(item);
									
									if(!isArray)isLoop=false;
								}
								if(isLoop)
								token = jp.nextToken();

							}
							//System.out.println("LOOP DATA FINISH");

						
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
