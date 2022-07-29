package com.ashishvz.billsplitz.models;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {
    public final static String fileName = "BILL_SPLIT";
    public final static String userName = "USER_NAME";
    public final static String email = "EMAIL";
    public final static String phoneNumber = "PHONE_NUMBER";

    public static void setUserName(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userName, name);
        editor.apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(userName, null);
    }

    public static void setPhoneNumber(Context context, Long number) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(phoneNumber, number);
        editor.apply();
    }

    public static Long getPhoneNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(phoneNumber, 0);
    }



}
