package com.pa.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.State;

import java.util.ArrayList;

public class ParserState {
	String content;
    String status;
    ArrayList<State> states;

	public ParserState(String content) {
		this.content = content;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Wrapper data = objectMapper.readValue(
					content,
					Wrapper.class);
            status = data.status;
            states = data.result;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public String getStatus() {
        return status;
    }

    public ArrayList<State> getStates() {
        return states;
    }
	
	public static class Wrapper{
		public String status;
		public ArrayList<State> result;
	}
}
