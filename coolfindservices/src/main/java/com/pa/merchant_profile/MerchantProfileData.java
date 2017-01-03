package com.pa.merchant_profile;

import com.pa.pojo.MerchantReview;

import java.util.ArrayList;

/**
 * Created by lenovo on 27-Dec-16.
 */

public class MerchantProfileData {

    private String headerTitle;
    private String viewType;

    public String status,about,cover_photo,merchant_name,overall_rating,rating_count;

	public ArrayList<String> service_list;
	public ArrayList<String> gallery_image;
	public ArrayList<MerchantReview> reviews;

    public MerchantProfileData() {

    }

    public void setViewType(String type){
        this.viewType = type;
    }

    public String getViewType(){
        return viewType;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

}
