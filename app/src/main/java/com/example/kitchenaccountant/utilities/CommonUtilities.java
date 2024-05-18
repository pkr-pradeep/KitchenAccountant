package com.example.kitchenaccountant.utilities;

import com.example.kitchenaccountant.domain.ExpenseDomain;
import com.example.kitchenaccountant.domain.ExpenseRoot;
import com.example.kitchenaccountant.domain.IncomeDomain;
import com.example.kitchenaccountant.domain.IncomeRoot;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
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

    public static String incomeJsonModifier(String existingJson) {
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

    public static String expenseJsonModifier(String existingJson) {
        String mergedJson;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ExpenseDomain expenseDomain = objectMapper.readValue(existingJson, ExpenseDomain.class);
            ExpenseRoot expenseRoot = new ExpenseRoot();
            expenseRoot.setExpenseDomains(Collections.singletonList(expenseDomain));
            mergedJson = objectMapper.writeValueAsString(expenseRoot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return mergedJson;
    }

    public static String mergeIncomeJsonIntoAnother(String existingJson, String newJson) {
        String mergedJson;
        try {
            IncomeRoot incomeRootExisting = null;
            ObjectMapper objectMapper = new ObjectMapper();
            if (!isNullOrEmpty(existingJson))
                incomeRootExisting = objectMapper.readValue(existingJson, IncomeRoot.class);
            else return incomeJsonModifier(newJson);
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

    public static String mergeExpenseJsonIntoAnother(String existingJson, String newJson) {
        String mergedJson;
        try {
            ExpenseRoot expenseRootExisting = null;
            ObjectMapper objectMapper = new ObjectMapper();
            if (!isNullOrEmpty(existingJson))
                expenseRootExisting = objectMapper.readValue(existingJson, ExpenseRoot.class);
            else return expenseJsonModifier(newJson);
            ExpenseDomain expenseDomain = objectMapper.readValue(newJson, ExpenseDomain.class);
            List<ExpenseDomain> expenseDomainList = expenseRootExisting.getExpenseDomains();
            expenseDomainList.add(expenseDomain);
            expenseRootExisting.setExpenseDomains(expenseDomainList);
            mergedJson = objectMapper.writeValueAsString(expenseRootExisting);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mergedJson;
    }

    public static List<IncomeDomain> getAllIncomes(String incomeJson) {
        try {
            IncomeRoot incomeRootExisting = null;
            ObjectMapper objectMapper = new ObjectMapper();
            if (!isNullOrEmpty(incomeJson))
                incomeRootExisting = objectMapper.readValue(incomeJson, IncomeRoot.class);
            return incomeRootExisting.getIncomeDomains();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}