package com.pa.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.User;
import com.pa.pojo.UserORM;

public class ParserUserORM {
	String content;
	UserORM data;
	
	
	public ParserUserORM(String content) {
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			data = objectMapper.readValue(
					content,
					UserORM.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public UserORM getData() {
		return data;
	}
}
