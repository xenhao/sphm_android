package com.pa.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.AdaptiveSearch;
import com.pa.pojo.UserORM;

public class ParserAdaptiveSearch {
	String content;
	AdaptiveSearch data;
	
	
	public ParserAdaptiveSearch(String content) {
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			data = objectMapper.readValue(
					content,
					AdaptiveSearch.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public AdaptiveSearch getData() {
		return data;
	}
}
