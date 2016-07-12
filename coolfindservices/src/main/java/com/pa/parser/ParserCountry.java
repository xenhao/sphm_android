package com.pa.parser;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.AdaptiveSearch;
import com.pa.pojo.Country;

public class ParserCountry {
	String content;
	AdaptiveSearch data;
	Country country;
	
	public ParserCountry(String content) {
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Wrapper data = objectMapper.readValue(
					content,
					Wrapper.class);
			country=data.result.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Country getData() {
		return country;
	}
	
	
	public static class Wrapper{
		public Wrapper(){};
		public String status;
		public ArrayList<Country> result;
	}
}
