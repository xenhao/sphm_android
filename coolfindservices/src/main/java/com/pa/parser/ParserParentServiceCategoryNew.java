package com.pa.parser;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.AdaptiveSearch;
import com.pa.pojo.ParentServiceCategory;
import com.pa.pojo.ServiceCategory;

public class ParserParentServiceCategoryNew {
	public String content;
	public String status;
	public String getStatus() {
		return status;
	}

	public ArrayList<ParentServiceCategory> getArr() {
		return arr;
	}

	public String result;
	public ParentServiceCategory item;
	public ServiceCategory item2;
	public ArrayList<ParentServiceCategory> arr;

	public ParserParentServiceCategoryNew(String content){
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Wrapper wrapper = objectMapper.readValue(
					content,
					Wrapper.class);
			arr=wrapper.result;
			status=wrapper.status;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static class Wrapper{
		public Wrapper(){};
		public String status;
		public ArrayList<ParentServiceCategory> result;
	}
}
