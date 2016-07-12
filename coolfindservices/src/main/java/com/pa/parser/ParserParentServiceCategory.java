package com.pa.parser;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.pa.pojo.ParentServiceCategory;
import com.pa.pojo.ServiceCategory;

public class ParserParentServiceCategory {

	public String status;
	public String result;
	public ParentServiceCategory item;
	public ServiceCategory item2;
	ArrayList<ParentServiceCategory> arr;

	public String TAG_ID="id";
	public String TAG_SERVICE_NAME="service_name";
	public String TAG_CHILDREN="children";
	public String TAG_CHILDREN_ID="id";
	public String TAG_CHILDREN_SERVICE_NAME="service_name";
	public String TAG_ICON_IMAGE="icon_image";
	
	int TYPE=1;
	
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

	public ParentServiceCategory getItem() {
		return item;
	}

	public void setItem(ParentServiceCategory item) {
		this.item = item;
	}

	public ArrayList<ParentServiceCategory> getArr() {
		return arr;
	}

	public void setArr(ArrayList<ParentServiceCategory> arr) {
		this.arr = arr;
	}

	public ParserParentServiceCategory(String result,int type){
		this.result=result;
		this.TYPE=type;
		TAG_ID="parent_service_id";
		TAG_SERVICE_NAME="parent_service_name";
		TAG_CHILDREN="children_data";
		TAG_CHILDREN_ID="children_service_id";
		TAG_CHILDREN_SERVICE_NAME="children_service_name";

		processData();
	}
	
	public ParserParentServiceCategory(String result) {
		this.result=result;

		TYPE=1;
		TAG_ID="id";
		TAG_SERVICE_NAME="service_name";
		TAG_CHILDREN="children";
		TAG_CHILDREN_ID="id";
		TAG_CHILDREN_SERVICE_NAME="service_name";

		processData();

	}

	
	void processData(){
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
						status = jp.getText();
					} else if ("result".equals(fieldname)) {

						// get Category
						boolean isArray = false, isLoop = true;
						arr = new ArrayList<ParentServiceCategory>();
						while (isLoop && token != JsonToken.END_ARRAY) {
							fieldname = jp.getCurrentName();
							if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
								isArray = true;

							} else if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
								item = new ParentServiceCategory();
							} else if (TAG_ID.equals(fieldname)) {
								jp.nextToken();
								item.id = (jp.getText());
							} else if (TAG_SERVICE_NAME.equals(fieldname)) {
								jp.nextToken();
								item.service_name = (jp.getText());
							} else if (TAG_CHILDREN.equals(fieldname)) {
								boolean isArray2 = false, isLoop2 = true;
								item.children = new ArrayList<ServiceCategory>();

								while (isLoop2 && token != JsonToken.END_ARRAY) {
									fieldname = jp.getCurrentName();
									if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
										isArray2 = true;

									} else if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
										item2 = new ServiceCategory();
									} else if (TAG_CHILDREN_ID.equals(fieldname)) {
										jp.nextToken();
										item2.id = (jp.getText());
									} else if (TAG_CHILDREN_SERVICE_NAME.equals(fieldname)) {
										jp.nextToken();
										item2.service_name = (jp.getText());
									} 
									else if ("has_children".equals(fieldname)){
										jp.nextToken();
										item2.has_children=Integer.parseInt(jp.getText());
									}
									else if ("icon_image".equals(fieldname)){
										jp.nextToken();
										item2.icon_image=(jp.getText());
									}

									else if (token == JsonToken.END_OBJECT) {
										if (item2.id != null
												&& item2.id.length() > 0) {
											item.children.add(item2);
										}
										if (!isArray2)
											isLoop2 = false;
									}
									//System.out.println("Looping 2");
									if (isLoop2)
										token = jp.nextToken();
								}

								

							} 
							
							
							else if (token == JsonToken.END_OBJECT) {
								if (item.id != null && item.id.length() > 0)
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
