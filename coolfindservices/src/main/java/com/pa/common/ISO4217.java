package com.pa.common;

import java.util.HashMap;
import java.util.Map;

public class ISO4217 {

    private static Map<String, String> entities;

    public static String translate(String currency) {
        if (entities == null) {
            entities = new HashMap<>();
            entities.put("SGD", "SGD702");
            entities.put("MYR", "MYR458");
            entities.put("IDR", "IDR360");
        }

        return entities.get(currency);
    }

}
