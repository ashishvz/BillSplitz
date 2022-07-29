package com.ashishvz.billsplitz.models;

import java.io.Serializable;

public class SplitStatus implements Serializable {

    public SplitStatus(String phoneNumber, Boolean status) {
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public String phoneNumber;
    public Boolean status;
}
