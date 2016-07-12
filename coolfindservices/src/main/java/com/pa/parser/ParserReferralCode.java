package com.pa.parser;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.ReferralCode;

public class ParserReferralCode extends ParserBaseClass {
	String content;
	ReferralCode data;

	public ParserReferralCode(String content) {
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JSONObject json = new JSONObject(content);
			data = objectMapper.readValue(
					content,
					ReferralCode.class);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ReferralCode getData(){
		return data;
	}
	
}
