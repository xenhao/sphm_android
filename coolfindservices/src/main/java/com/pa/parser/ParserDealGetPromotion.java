package com.pa.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.PromotionItem;

import java.util.ArrayList;

public class ParserDealGetPromotion {

    private Wrapper data;

    public ParserDealGetPromotion(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            data = objectMapper.readValue(
                    content,
                    Wrapper.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStatus() {
        return data.status;
    }

    public ArrayList<PromotionItem> getResult() {
        return data.result;
    }

    public int getTotal() {
        return data.total;
    }

    public static class Wrapper {
        public Wrapper() {}

        public String status;
        public ArrayList<PromotionItem> result;
        public int total;
    }

}
