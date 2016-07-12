package com.pa.pojo;

/**
 * Created by Steven on 11/11/2015.
 */
public class PackageJobDetailItem extends JobDItem {

    public String tnc;
    public String detail;

    public PackageJobDetailItem() {
    }

    public PackageJobDetailItem(JobItem item) {
        this.customer_username = item.customer_username;
        this.merchant_username = item.merchant_username;
        this.serial = item.serial;
        this.service_proposal_serial = item.service_proposal_serial;
        this.service_description = item.service_description;
        this.service_order_type = item.service_order_type;
        this.service_order_status = item.service_order_status;
        this.total = item.total;
        this.ori_total = item.ori_total;
        this.promo_code = item.promo_code;
        this.discount_percent = item.discount_percent;
        this.discount_amount = item.discount_amount;
        this.merchant_verification_code = item.merchant_verification_code;
        this.customer_approval_code = item.customer_approval_code;
        this.created_at = item.created_at;
        this.updated_at = item.updated_at;
        this.deleted_at = item.deleted_at;
        this.read_status = item.read_status;
        this.company_name = item.company_name;
        this.request_title = item.request_title;
        this.service_order_address = item.service_order_address;
        this.service_order_state = item.service_order_state;
        this.service_order_city = item.service_order_city;
        this.service_order_postal_code = item.service_order_postal_code;
        this.cs_name = item.cs_name;
        this.cs_address = item.cs_address;
        this.cs_mobile_number = item.cs_mobile_number;
        this.cs_home_number = item.cs_home_number;
        this.cs_email = item.cs_email;
        this.preferred_time1_start = item.preferred_time1_start;
        this.preferred_time1_stop = item.preferred_time1_stop;
        this.preferred_time2_start = item.preferred_time2_start;
        this.preferred_time2_stop = item.preferred_time2_stop;
        this.is_verified = item.is_verified;
        this.service_icon = item.service_icon;
        this.other_remarks = item.other_remarks;
        this.related_job_id = item.related_job_id;
        this.currency = item.currency;
    }
}
