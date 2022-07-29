package com.ashishvz.billsplitz.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ashishvz.billsplitz.databases.dao.ExpenseDao;
import com.ashishvz.billsplitz.databases.entities.Expense;

@Database(entities = {Expense.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "bill-database").fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract ExpenseDao expenseDao();
}
