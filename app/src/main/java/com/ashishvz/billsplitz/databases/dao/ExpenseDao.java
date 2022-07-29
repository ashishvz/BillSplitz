package com.ashishvz.billsplitz.databases.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ashishvz.billsplitz.databases.entities.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Expense expense);

    @Query("SELECT id,\n" +
            "       expenseType,\n" +
            "       SUM(expenseAmount) AS expenseAmount,\n" +
            "       createdAt,\n" +
            "       month\n" +
            "  FROM expense\n" +
            " GROUP BY expenseType\n" +
            " ORDER BY createdAt DESC")
    List<Expense> getTop10Expenses();

    @Query("Select sum(expenseAmount) from expense")
    Long getSumOfTop10();
}
