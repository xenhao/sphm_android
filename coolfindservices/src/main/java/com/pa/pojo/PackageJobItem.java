package com.pa.pojo;

/**
 * Created by Steven on 05/11/2015.
 */
public class PackageJobItem extends PJobItem {

    public String tnc;
    public String detail;
    public String cancellation_policy;
    public String selected_start_date;
    public String selected_end_date;
    public String selected_address;
    public String selected_country;
    public String selected_state;
    public String selected_city;
    public String selected_postal_code;

    public PackageJobItem() {
    }

    public PackageJobItem(PJobItem item) {

        this.package_serial = item.package_serial;
        this.serial = item.serial;
        this.title = item.title;
        this.price = item.price;
        this.ori_price = item.ori_price;
        this.currency = item.currency;
        this.detail = item.detail;
        this.short_description = item.short_description;
        this.tnc = item.tnc;
        this.start_date = item.start_date;
        this.end_date = item.end_date;
        this.lead_time = item.lead_time;
        this.travel_time = item.travel_time;
        this.total_slot = item.total_slot;
        this.used_slot = item.used_slot;
        this.has_service_time = item.has_service_time;
        this.has_service_address = item.has_service_address;
        this.is_promotion = item.is_promotion;
        this.deep_link = item.deep_link;
        this.merchant_name = item.merchant_name;
        this.merchant_username = item.merchant_username;
        this.country = item.country;
        this.country_short = item.country_short;
        this.rating = item.rating;
        this.no_of_review = item.no_of_review;
        this.attachment_photo = item.attachment_photo;
    }
}
