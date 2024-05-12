package com.example.kitchenaccountant.domain;

public class IncomeDomain {
    public String category;
    public String collectedFrom;
    public String amount;
    public String receivedDate;
    public String remarks;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCollectedFrom() {
        return collectedFrom;
    }

    public void setCollectedFrom(String collectedFrom) {
        this.collectedFrom = collectedFrom;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}