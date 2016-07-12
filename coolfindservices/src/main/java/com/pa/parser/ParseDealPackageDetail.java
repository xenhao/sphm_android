package com.pa.parser;

import com.pa.pojo.PackageJobItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Steven on 05/11/2015.
 */
public class ParseDealPackageDetail {

    public boolean status;
    public PackageJobItem mJobItem;
    public JSONObject mJson;

    public ParseDealPackageDetail(String content) {
        try {
            mJson = new JSONObject(content);

            if (mJson.has("status") && mJson.getString("status").equalsIgnoreCase("success")) {
                status = true;
            } else {
                status = false;
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public PackageJobItem parsePromoJobItem() {

        mJobItem = new PackageJobItem();

        if (!status) {
            return null;
        }

        try {
            mJson = mJson.getJSONObject("result");
            mJobItem.package_serial 			= mJson.getString("package_serial");
            mJobItem.serial 				    = mJson.getString("serial");
            mJobItem.title 				        = mJson.getString("title");
            mJobItem.price 				        = mJson.getString("price");
            mJobItem.ori_price 			        = mJson.getString("ori_price");
            mJobItem.currency 			        = mJson.getString("currency");
            mJobItem.detail 			        = mJson.getString("detail");
            mJobItem.short_description          = mJson.getString("short_description");
            mJobItem.tnc 					    = mJson.getString("tnc");
            mJobItem.start_date                 = mJson.getString("start_date");
            mJobItem.end_date                   = mJson.getString("end_date");
            mJobItem.lead_time                  = mJson.getString("lead_time");
            mJobItem.travel_time 				= mJson.getString("travel_time");
            mJobItem.total_slot 				= mJson.getString("total_slot");
            mJobItem.used_slot 				    = mJson.getString("used_slot");
            mJobItem.has_service_time 		    = mJson.getString("has_service_time");
            mJobItem.has_service_address 		= mJson.getString("has_service_address");
            mJobItem.is_promotion 		        = mJson.getString("is_promotion");
            mJobItem.deep_link 		            = mJson.getString("deep_link");
            mJobItem.merchant_username          = mJson.getString("merchant_username");
            mJobItem.merchant_name              = mJson.getString("merchant_name");
            mJobItem.rating                     = (float)mJson.getDouble("rating");
            mJobItem.no_of_review               = mJson.getString("no_of_review");
            mJobItem.cancellation_policy        = mJson.getString("cancellation_policy");
//            mJobItem.attachment_photo           = mJson.g("attachment_photo");
            ArrayList<String> attachment = new ArrayList<>();
            JSONArray jAttachment = mJson.getJSONArray("attachment_photo");
            for(int i = 0, ilen = jAttachment.length(); i < ilen; i++)
            {
                attachment.add(jAttachment.getString(i));
            }
            mJobItem.attachment_photo = attachment;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return mJobItem;
    }

}
