package com.ashishvz.billsplitz.models;

public class Expenses {
    public Expenses(Long spentAmount, String expenseType) {
        this.spentAmount = spentAmount;
        this.expenseType = expenseType;
    }

    public Long spentAmount;
    public String expenseType;
}
