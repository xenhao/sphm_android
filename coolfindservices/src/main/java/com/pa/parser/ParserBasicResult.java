package com.pa.parser;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.pa.pojo.ServiceCategory;

public class ParserBasicResult {

	String status;
	String resultt = "";
	String reason="";

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getResult() {
		return resultt;
	}

	public void setResult(String result) {
		this.resultt = result;
	}

	public ParserBasicResult(String result) {

		try {
			JsonFactory f = new JsonFactory();
			@SuppressWarnings("deprecation")
			JsonParser jp = f.createJsonParser(result);
			JsonToken token;
			token = jp.nextToken();

			String fieldname = jp.getCurrentName();
			{
				// get Question

				while (token != JsonToken.END_OBJECT) {
					fieldname = jp.getCurrentName();
					if ("status".equals(fieldname)) {
						jp.nextToken();
						status = jp.getText();
					}
					// else if ("result".equals(fieldname)) {
					// jp.nextToken();
					// result = jp.getText();
					//
					// }
					else if ("reason".equals(fieldname)) {

						// get Category
						jp.nextToken();
						if (jp.getCurrentToken() == JsonToken.START_ARRAY) {

							boolean isArray = false, isLoop = true;
							while (token != JsonToken.END_ARRAY) {
								fieldname = jp.getCurrentName();
								if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
									isArray = true;
									jp.nextToken();
								}
								if (isArray) {
									if(jp.getText().length()>0)
									reason += jp.getText() + "  ";
									//System.out.println(reason);

								} 
								token = jp.nextToken();

							}
							// System.out.println("LOOP DATA FINISH");
						}else {
							reason = result + jp.getText() + "  ";

						}


					}

					token = jp.nextToken();

				}

			}

			jp.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		resultt = result;

	}

}
