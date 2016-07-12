package com.pa.pojo;

import java.util.ArrayList;

/**
 * Created by Steven on 03/11/2015.
 */
public class PackageListDetailItem {

    public PackageListDetailItem() {}

    public String serial, title, package_serial, service_name, detail, tnc, status,
            currency, ori_price, has_service_address, has_service_time, created_at, updated_at,
            promo_code, merchant_username, merchant_name ,short_description, price;

    public ArrayList<String> attachment_photo;
    public String cover_photo;
    public Merchant merchant;
}
