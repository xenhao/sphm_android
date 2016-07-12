package com.pa.pojo;

import java.util.ArrayList;

public class PromotionItem {
    public PromotionItem() {}
    public String serial, title, price, discount_price, currency, detail, tnc, cover_photo, status,
            start_date, end_date, has_service_address, has_service_time, created_at, updated_at,
            promo_code, merchant_username;
    public ArrayList<String> attachment_photo;
    public Merchant merchant;
}
