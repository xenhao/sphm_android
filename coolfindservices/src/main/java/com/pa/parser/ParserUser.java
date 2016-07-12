package com.pa.parser;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.User;

public class ParserUser {
	
	String status;
	String result;
	User item;
	String type;
	public String getType() {
		return type;
	}

	ArrayList<User> arr;
	
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

	public User getItem() {
		return item;
	}

	public void setItem(User item) {
		this.item = item;
	}

	public ArrayList<User> getArr() {
		return arr;
	}

	public void setArr(ArrayList<User> arr) {
		this.arr = arr;
	}

	public User getUser(){
		return item;
	}
	
	public ParserUser(String result){
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Wrapper data = objectMapper.readValue(
					result,
					Wrapper.class);
			
			item=data.result;
			status=data.status;
			type=data.type;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
//	public ParserUser(String result) {
//		
//		try {
//			JsonFactory f = new JsonFactory();
//			@SuppressWarnings("deprecation")
//			JsonParser jp = f.createJsonParser(result);
//			JsonToken token;
//			token = jp.nextToken();
//
//		
//				String fieldname = jp.getCurrentName();
//				{
//					// get Question
//
//					while (token != JsonToken.END_OBJECT) {
//						fieldname = jp.getCurrentName();
//						if ("status".equals(fieldname)) {
//							jp.nextToken();
//							status=jp.getText();
//						}
//						else if("type".equals(fieldname)){
//							jp.nextToken();
//							type=jp.getText();
//						}
//						else if ("result".equals(fieldname)) {
//
//							// get Category
//							boolean isArray=false,isLoop=true;
//							arr=new ArrayList<User>();
//							while (isLoop && token != JsonToken.END_ARRAY) {
//								fieldname = jp.getCurrentName();
//								if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
//									isArray=true;
//									
//								}else						
//								if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
//									item = new User();
//								} else if ("active_session_token".equals(fieldname)) {
//									jp.nextToken();
//									item.activeToken=(jp.getText());
//								}  
//								else if ("username".equals(fieldname)) {
//									jp.nextToken();
//									item.name=(jp.getText());
//								}  
//								
//																
//								else if (token == JsonToken.END_OBJECT) {
//									if(item.name!=null && item.name.length()>0)
//									arr.add(item);
//									
//									if(!isArray)isLoop=false;
//								}
//								if(isLoop)
//								token = jp.nextToken();
//
//							}
//							//System.out.println("LOOP DATA FINISH");
//
//						
//						}
//						token = jp.nextToken();
//
//					}
//
//				}
//				
//			jp.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	public static class Wrapper{
		public Wrapper(){};
		public String status,type;
		public User result;
		public ArrayList<String> reason;
	}
}
