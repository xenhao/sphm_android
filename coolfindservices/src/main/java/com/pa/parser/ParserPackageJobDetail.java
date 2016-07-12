package com.pa.parser;

import com.pa.pojo.PackageJobDetailItem;
import com.pa.pojo.PackageJobItem;

import org.json.JSONObject;

/**
 * Created by Steven on 11/11/2015.
 */
public class ParserPackageJobDetail {

    public boolean status;
    public PackageJobDetailItem mJobItem;
    public JSONObject mJson;

    public ParserPackageJobDetail(String content) {
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

    public PackageJobDetailItem parsePackageJobItem() {
        mJobItem = new PackageJobDetailItem();

        if (!status) {
            return null;
        }

        try {
            mJson = mJson.getJSONObject("result");
            mJobItem.customer_username 			= mJson.getString("customer_username");
            mJobItem.updated_at 				= mJson.getString("updated_at");
            mJobItem.created_at 				= mJson.getString("created_at");
            mJobItem.deleted_at 				= mJson.getString("deleted_at");
            mJobItem.discount_amount 			= mJson.getString("discount_amount");
            mJobItem.discount_percent 			= mJson.getString("discount_percent");
            mJobItem.merchant_username 			= mJson.getString("merchant_username");
            mJobItem.merchant_verification_code = mJson.getString("merchant_verification_code");
            mJobItem.serial 					= mJson.getString("serial");
            mJobItem.service_description 		= mJson.getString("service_description");
            mJobItem.service_order_status 		= mJson.getString("service_order_status");
            mJobItem.service_order_type 		= mJson.getString("service_order_type");
            mJobItem.service_proposal_serial 	= mJson.getString("service_proposal_serial");
            mJobItem.total 						= mJson.getString("total");
            mJobItem.ori_total 					= mJson.getString("ori_total");
            mJobItem.request_title 				= mJson.getString("request_title");
            mJobItem.service_order_address 		= mJson.getString("service_order_address");
            mJobItem.service_order_state 		= mJson.getString("service_order_state");
            mJobItem.service_order_city 		= mJson.getString("service_order_city");
            mJobItem.service_order_postal_code 	= mJson.getString("service_order_postal_code");
            mJobItem.merchant_verification_code = mJson.getString("merchant_verification_code");
            mJobItem.customer_approval_code 	= mJson.getString("customer_approval_code");
            mJobItem.preferred_time1_start 		= mJson.getString("preferred_time1_start");
            mJobItem.preferred_time1_stop 		= mJson.getString("preferred_time1_stop");
            mJobItem.preferred_time2_start 		= mJson.getString("preferred_time2_start");
            mJobItem.preferred_time2_stop 		= mJson.getString("preferred_time2_stop");
            mJobItem.tnc                        = mJson.getString("promo_tnc");
            mJobItem.detail                     = mJson.getString("promo_detail");
            mJobItem.company_name               = mJson.getString("merchant_company");
            mJobItem.co_main_contact_number     = mJson.getString("merchant_contact");
            mJobItem.currency                   = mJson.getString("currency");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return mJobItem;
    }
}
