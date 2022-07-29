package com.ashishvz.billsplitz.models;

import java.util.List;

public class UserDetails {
    private String fullName;
    private String email;
    private String phoneNumber;
    private Long createdAt;
    private List<String> billSplits;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getBillSplits() {
        return billSplits;
    }

    public void setBillSplits(List<String> billSplits) {
        this.billSplits = billSplits;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
