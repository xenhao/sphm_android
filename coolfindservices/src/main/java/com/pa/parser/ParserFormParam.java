package com.pa.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.common.Tracer;
import com.pa.pojo.AdaptiveSearch;
import com.pa.pojo.FormByService;
import com.pa.pojo.FormParam;
import com.pa.pojo.QuestionUnit;

public class ParserFormParam {
	String content;
	public String status;

	ArrayList<FormByService> arrFormByService = new ArrayList<FormByService>();
	
	
	public ParserFormParam(String content) {
//		this.content = content;
//		JSONObject root;
//		try {
//			root = new JSONObject(content);
//			Tracer.d("test");
//			status = root.getString("status");
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Wrapper data = objectMapper.readValue(
					content,
					Wrapper.class);
			arrFormByService =data.result;
			status=data.status;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public ArrayList<FormByService> getData() {

//		try {
//			JSONObject root = new JSONObject(content);
//			if ("success".equals(status)) {
//				JSONArray result = root.getJSONArray("result");
//
//				for (int j = 0; j < result.length(); j++) {
//					JSONObject resItem = result.getJSONObject(j);
//					FormByService formByService = new FormByService();
//
//					formByService.service_id = resItem.getString("service_id");
//
//					JSONArray array = new JSONArray(
//							resItem.getString("form_data"));
//					for (int i = 0; i < array.length(); i++) {
//						// ArrayList<FormParam> arr = new
//						// ArrayList<FormParam>();
//
//						FormParam item = new FormParam();
//						JSONObject temp = array.getJSONObject(i);
//						item.param_name = temp.getString("param_name");
//						item.param_type = temp.getString("param_type");
//						item.param_default_value = temp
//								.getString("param_default_value");
//						// arr.add(item);
//
//						if ("option".equals(item.param_type)) {
//							String option = temp.getString("param_options");
//							JSONArray arrOption = new JSONArray(option);
//							for (int x = 0; x < arrOption.length(); x++) {
//								JSONObject optionObj = arrOption
//										.getJSONObject(x);
//								QuestionUnit qu = new QuestionUnit();
//								qu.setAnswer(optionObj.getString("label"));
//								qu.setValue(optionObj.getString("value"));
//								item.param_options.add(qu);
//							}
//						}
//
//						formByService.form_data.add(item);
//					}
//
//					arrFormByService.add(formByService);
//				}
//			}
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//		}
		return arrFormByService;
	}

	public static class Wrapper{
		public Wrapper(){};
		public String status;
		public ArrayList<FormByService> result;
	}
	
}
