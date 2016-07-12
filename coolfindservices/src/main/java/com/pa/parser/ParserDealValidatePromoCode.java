package com.pa.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.DealPromoCodeResult;

public class ParserDealValidatePromoCode {
	String content;
    DealPromoCodeResult data;


	public ParserDealValidatePromoCode(String content) {
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			data = objectMapper.readValue(
					content,
                    DealPromoCodeResult.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DealPromoCodeResult getData() {
		return data;
	}
}
