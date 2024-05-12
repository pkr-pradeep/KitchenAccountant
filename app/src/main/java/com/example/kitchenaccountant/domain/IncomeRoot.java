package com.example.kitchenaccountant.domain;

import java.util.List;

public class IncomeRoot {
    public List<IncomeDomain> incomeDomains;

    public List<IncomeDomain> getIncomeDomains() {
        return incomeDomains;
    }

    public void setIncomeDomains(List<IncomeDomain> incomeDomains) {
        this.incomeDomains = incomeDomains;
    }
}