package com.example.kitchenaccountant.domain;

public class ExpenseDomain {
    private String category;
    private String description;
    private String amount;
    private String expenseDate;
    private String remittanceName;
    private String remittanceMobile;
    private String remittanceAddress;
    private String payVia;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getRemittanceName() {
        return remittanceName;
    }

    public void setRemittanceName(String remittanceName) {
        this.remittanceName = remittanceName;
    }

    public String getRemittanceMobile() {
        return remittanceMobile;
    }

    public void setRemittanceMobile(String remittanceMobile) {
        this.remittanceMobile = remittanceMobile;
    }

    public String getRemittanceAddress() {
        return remittanceAddress;
    }

    public void setRemittanceAddress(String remittanceAddress) {
        this.remittanceAddress = remittanceAddress;
    }

    public String getPayVia() {
        return payVia;
    }

    public void setPayVia(String payVia) {
        this.payVia = payVia;
    }
}
