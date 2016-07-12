package com.pa.pojo;

import java.util.ArrayList;

public class QuestionUnit {
	public String value;
	public String label;
	public FormParam child;
	public ArrayList<FormParam> children_multi;
	

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setAnswer(String answer) {
		this.label = answer;
	}
}
