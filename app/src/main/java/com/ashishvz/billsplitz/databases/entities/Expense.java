package com.ashishvz.billsplitz.databases.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity(tableName = "Expense")
public class Expense {

    public Expense(Integer id, String expenseType, Long expenseAmount, Long createdAt, String month) {
        this.id = id;
        this.expenseType = expenseType;
        this.expenseAmount = expenseAmount;
        this.createdAt = createdAt;
        this.month = month;
    }

    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public String expenseType;
    public Long expenseAmount;
    public Long createdAt;
    public String month;
}
