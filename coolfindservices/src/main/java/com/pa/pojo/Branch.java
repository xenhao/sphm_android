package com.pa.pojo;

public class Branch {
	public String address1,address2,state,city,postal,business_number,main_email_address,cp_email,cp_number;

	public Branch(){
		address1="";
		address2="";
		state="";
		city="";
		postal="";
		business_number="";
		main_email_address="";
		cp_email="";
		cp_number="";
	}
	
	public String toString(){
		return "{\"address1\":\""+address1+"\",\"address2\":\""+address2+"\",\"state\":\""+state+"\",\"city\":\""+city+"\",\"postal\":\""+postal+"\",\"business_number\":\""+business_number+"\",\"main_email_address\":\""+main_email_address+"\",\"cp_email\":\""+cp_email+"\",\"cp_number\":\""+cp_number+"\"}";
	}

}
