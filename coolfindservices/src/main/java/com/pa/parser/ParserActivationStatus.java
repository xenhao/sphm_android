package com.pa.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.ActivationStatus;
import com.pa.pojo.User;
import com.pa.pojo.UserORM;

public class ParserActivationStatus {
	String content;
	ActivationStatus data;
	
	
	public ParserActivationStatus(String content) {
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			data = objectMapper.readValue(
					content,
					ActivationStatus.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ActivationStatus getData() {
		return data;
	}
}
