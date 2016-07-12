package com.pa.pojo;

public class MolData{
	public MolData(){};

	public MolData(String[] config) {
		// TODO Auto-generated constructor stub
		merchantId=config[0];
		appName=config[1];
		verifyKey=config[2];
		apiKey=config[3];
		apiPass=config[4];
	}

	public String merchantId,appName,verifyKey,apiKey,apiPass;
}