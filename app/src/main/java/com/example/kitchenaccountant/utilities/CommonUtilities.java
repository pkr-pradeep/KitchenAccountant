package com.example.kitchenaccountant.utilities;

import com.example.kitchenaccountant.domain.IncomeDomain;
import com.example.kitchenaccountant.domain.IncomeRoot;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CommonUtilities {

    public static String getDateStamp(String format, String timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormat.format(new Date());
    }

    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) return true;
        if (obj instanceof String) return ((String) obj).isEmpty();
        if (obj instanceof Collection) return ((Collection<?>) obj).isEmpty();
        if (obj instanceof Object[]) return ((Object[]) obj).length == 0;
        return false;
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

    public static String jsonModifier(String existingJson) {
        String mergedJson;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            IncomeDomain incomeDomain = objectMapper.readValue(existingJson, IncomeDomain.class);
            IncomeRoot incomeRoot = new IncomeRoot();
            incomeRoot.setIncomeDomains(Collections.singletonList(incomeDomain));
            mergedJson = objectMapper.writeValueAsString(incomeRoot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return mergedJson;
    }

    public static String mergeJsonIntoAnother(String existingJson, String newJson) {
        String mergedJson;
        try {
            IncomeRoot incomeRootExisting = null;
            ObjectMapper objectMapper = new ObjectMapper();
            if (!isNullOrEmpty(existingJson))
                incomeRootExisting = objectMapper.readValue(existingJson, IncomeRoot.class);
            else jsonModifier(newJson);
            IncomeDomain incomeDomain = objectMapper.readValue(newJson, IncomeDomain.class);
            List<IncomeDomain> incomeDomainList = incomeRootExisting.getIncomeDomains();
            incomeDomainList.addAll(Collections.singletonList(incomeDomain));
            incomeRootExisting.setIncomeDomains(incomeDomainList);
            mergedJson = objectMapper.writeValueAsString(incomeRootExisting);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mergedJson;
    }
}
