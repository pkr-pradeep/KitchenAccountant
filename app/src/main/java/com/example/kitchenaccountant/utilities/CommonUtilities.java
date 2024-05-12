package com.example.kitchenaccountant.utilities;

import com.example.kitchenaccountant.domain.IncomeDomain;
import com.example.kitchenaccountant.domain.IncomeRoot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

public class CommonUtilities {

    public static String getDateStamp(String format, String timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormat.format(new Date());
    }

    public static String mergeJson(String existingJson, String newJson) {
        JSONObject mergedJsonObject;
        try {
            JSONObject jsonObject1 = new JSONObject(existingJson);
            JSONObject jsonObject2 = new JSONObject(newJson);
            mergedJsonObject = new JSONObject();
            mergedJsonObject.put("Object1", jsonObject1);
            mergedJsonObject.put("Object2", jsonObject2);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return mergedJsonObject.toString();
    }

    public static String mergeJsonWithJackson(String existingJson) {
        String mergedJson;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            IncomeDomain incomeDomain = objectMapper.readValue(existingJson, IncomeDomain.class);
            IncomeRoot incomeRoot = new IncomeRoot();
            incomeRoot.setIncomeDomains(Arrays.asList(incomeDomain));
            mergedJson = objectMapper.writeValueAsString(incomeRoot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return mergedJson;
    }

}
