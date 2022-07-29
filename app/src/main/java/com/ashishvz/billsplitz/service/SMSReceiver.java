package com.ashishvz.billsplitz.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ashishvz.billsplitz.databases.AppDatabase;
import com.ashishvz.billsplitz.databases.entities.Expense;
import com.ashishvz.billsplitz.models.SMS;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {

    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public AppDatabase appDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null &&
                ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
            appDatabase = AppDatabase.getInstance(context);
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++)
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            StringBuilder bodyText = new StringBuilder();
            for (SmsMessage message : messages) {
                bodyText.append(message.getMessageBody());
            }
            String body = bodyText.toString();
            extractAmountNameFromSMS(body);
        }
    }

    private void extractAmountNameFromSMS(String message) {
        try {
            if (message.contains("debited")) {
                Pattern regEx = Pattern.compile("(?i)(?:RS|INR|MRP)\\.?\\s?(\\d+(:?,\\d+)?(,\\d+)?(\\.\\d{1,2})?)");
                Matcher m = regEx.matcher(message);
                if (m.find()) {
                    try {
                        Log.e("amount_value= ", "" + m.group(0));
                        String amount = (m.group(0).replaceAll("inr", ""));
                        amount = amount.replaceAll("rs", "");
                        amount = amount.replaceAll("inr", "");
                        amount = amount.replaceAll(" ", "");
                        amount = amount.replaceAll(",", "");
                        amount = amount.replaceAll("INR", "");
                        amount = amount.replaceAll("Rs.", "");
                        String amountStr = amount;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (Double.parseDouble(amountStr) > 0) {
                                    DateFormat dateFormat = new SimpleDateFormat("MMM", Locale.US);
                                    appDatabase.expenseDao().insert(new Expense(null, "Bank Expense",
                                            (long) Double.parseDouble(amountStr), new Date().getTime(), dateFormat.format(new Date())));
                                } else {
                                    Log.d("SMS Parser", "Error inserting");
                                }
                            }
                        }).start();
                        System.out.println("Amount - " + Double.valueOf(amount));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Not found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
