package com.pa.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.PackageListDetailItem;

import java.util.ArrayList;

/**
 * Created by Steven on 03/11/2015.
 */
public class ParseDealGetPackageListDetail {

    private Wrapper data;

    public ParseDealGetPackageListDetail(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            data = objectMapper.readValue(
                    content,
                    Wrapper.class);
        } catch (Exception e) {
            e.printStackTrace();

//            Log.i("asd",e.printStackTrace());
        }
    }

    public String getStatus() {
        return data.status;
    }

    public ArrayList<PackageListDetailItem> getResult() {
        return data.result;
    }

    public int getTotal() {
        return data.total;
    }

    public static class Wrapper {
        public Wrapper() {}

        public String status;
        public ArrayList<PackageListDetailItem> result;
        public int total;
    }
}
