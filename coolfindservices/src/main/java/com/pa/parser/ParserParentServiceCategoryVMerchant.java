package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pa.common.Tracer;
import com.pa.pojo.ParentServiceCategory;
import com.pa.pojo.ServiceCategory;

public class ParserParentServiceCategoryVMerchant extends ParserBaseClass{

	public String status;
	public String result;
	public ParentServiceCategory item;
	public ServiceCategory item2;
	ArrayList<ParentServiceCategory> arr;

	public String TAG_ID = "id";
	public String TAG_SERVICE_NAME = "service_name";
	public String TAG_CHILDREN = "children";
	public String TAG_CHILDREN_ID = "id";
	public String TAG_CHILDREN_SERVICE_NAME = "service_name";
	public String TAG_ICON_IMAGE="icon_image";

	int TYPE = 1;

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

	public ParserParentServiceCategoryVMerchant(String result, int type) {
		this.result = result;
		this.TYPE = type;
		TAG_ID = "parent_service_id";
		TAG_SERVICE_NAME = "parent_service_name";
		TAG_CHILDREN = "children_data";
		TAG_CHILDREN_ID = "children_service_id";
		TAG_CHILDREN_SERVICE_NAME = "children_service_name";

		processData();
	}

	public ParserParentServiceCategoryVMerchant(String result) {
		this.result = result;

		TYPE = 1;
		TAG_ID = "id";
		TAG_SERVICE_NAME = "service_name";
		TAG_CHILDREN = "children";
		TAG_CHILDREN_ID = "id";
		TAG_CHILDREN_SERVICE_NAME = "service_name";

		processData();

	}

	void processData() {
		try {
			arr=new ArrayList<ParentServiceCategory>();
			JSONArray jsonArr=new JSONArray(result);
			for(int i=0;i<jsonArr.length();i++){
				Tracer.d("debug",""+i);
				JSONObject obj=jsonArr.getJSONObject(i);
				ParentServiceCategory parent=new ParentServiceCategory();
				parent.id=obj.getString(TAG_ID);
				parent.service_name=obj.getString(TAG_SERVICE_NAME);
				parent.children=new ArrayList<ServiceCategory>();
				JSONArray child_arr=new JSONArray(obj.getString(TAG_CHILDREN));
				for(int j=0;j<child_arr.length();j++){
					JSONObject obj2=child_arr.getJSONObject(j);
					ServiceCategory child=new ServiceCategory();
					child.id=obj2.getString(TAG_CHILDREN_ID);
					child.service_name=obj2.getString(TAG_CHILDREN_SERVICE_NAME);
					
					child.icon_image=getString(obj2, TAG_ICON_IMAGE) ;//obj2.getString(TAG_ICON_IMAGE);
					
					parent.children.add(child);
				}
				
				arr.add(parent);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
