package com.pa.parser;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ParserParamCache {
	String content;
	public HashMap<String, Object> data;
	public ArrayList<HashMap<String, JSONObject>> arrData;
	
	public ParserParamCache(String content) {
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//			data = objectMapper.readValue(
//					content,
//					new TypeReference<HashMap<String,JSONObject>>(){});
//			
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);
			
			JavaType javaType= typeFactory.constructType(Object.class,mapType);
			
			
			arrData=objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(
					ArrayList.class, javaType));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static class WrapperParam{
		public WrapperParam(){};
		public HashMap<String, Object> data;

	}

}
