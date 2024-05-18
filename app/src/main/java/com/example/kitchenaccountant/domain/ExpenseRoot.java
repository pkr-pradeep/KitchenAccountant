package com.example.kitchenaccountant.domain;

import java.util.List;

public class ExpenseRoot {
    public List<ExpenseDomain> expenseDomains;

    public List<ExpenseDomain> getExpenseDomains() {
        return expenseDomains;
    }

    public void setExpenseDomains(List<ExpenseDomain> expenseDomains) {
        this.expenseDomains = expenseDomains;
    }
}
