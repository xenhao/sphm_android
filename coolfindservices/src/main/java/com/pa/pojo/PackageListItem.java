package com.pa.pojo;

import java.util.ArrayList;

/**
 * Created by Steven on 02/11/2015.
 */
public class PackageListItem {
    public PackageListItem() {}

    public String serial, title, min_price, service_name, currency, detail, cover_photo, status,
            start_date, end_date, has_service_address, has_service_time, created_at, updated_at,
            promo_code, merchant_username,merchant_name ,merchant_co_name, mgp;
    public float rating;

    public ArrayList<String> attachment_photo;
    public Merchant merchant;
}
