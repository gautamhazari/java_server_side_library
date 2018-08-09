package com.gsma.mobileconnect.r2.demo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by a.furs on 17.05.2018.
 */
public class UriUtils {

    public static Map<String, String> getQueryParams(URI requestUri) {
        Map<String, String> queryPairs = new LinkedHashMap<String, String>();
        String[] pairs = requestUri.getQuery().split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return queryPairs;
    }
}
