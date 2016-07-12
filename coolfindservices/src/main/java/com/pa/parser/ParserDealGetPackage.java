package com.pa.parser;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.pojo.PackageListItem;

import java.util.ArrayList;

/**
 * Created by Steven on 02/11/2015.
 */
public class ParserDealGetPackage {

    private Wrapper data;

    public ParserDealGetPackage(String content) {
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

    public ArrayList<PackageListItem> getResult() {
        return data.result;
    }

    public int getTotal() {
        return data.total;
    }

    public static class Wrapper {
        public Wrapper() {}

        public String status;
        public ArrayList<PackageListItem> result;
        public int total;
    }
}
