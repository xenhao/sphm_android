package com.pa.pojo;

import java.util.ArrayList;

public class FormParam {
	public FormParam(){};
	public String param_name,param_type,param_default_value,param_description;
	public ArrayList<QuestionUnit> param_options=new ArrayList<QuestionUnit>();
	
	//param_name	String	Duration
//	param_type	String	option
//	param_default_value	String	30 Mins
//	param_description	String	
}
