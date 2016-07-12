package com.pa.pojo;

import java.util.ArrayList;

public class MerchantProfile {
	public String status,about,cover_photo,merchant_name,overall_rating,rating_count;
	
	public ArrayList<String> service_list;
	public ArrayList<String> gallery_image;
	public ArrayList<MerchantReview> reviews;
	
	
	public MerchantProfile(){};
}
