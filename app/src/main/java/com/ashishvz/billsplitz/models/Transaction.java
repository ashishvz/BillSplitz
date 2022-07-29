package com.ashishvz.billsplitz.models;

import java.io.Serializable;

public class Transaction implements Serializable {
    public String transactionId;
    public Double transactionAmount;
    public String splitName;
    public Long createdAt;
    public String createdBy;
}
