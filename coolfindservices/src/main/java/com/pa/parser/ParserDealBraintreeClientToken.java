package com.pa.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.DealBraintreeClientToken;

public class ParserDealBraintreeClientToken {
	String content;
    DealBraintreeClientToken data;


	public ParserDealBraintreeClientToken(String content) {
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			data = objectMapper.readValue(
					content,
                    DealBraintreeClientToken.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DealBraintreeClientToken getData() {
		return data;
	}
}
