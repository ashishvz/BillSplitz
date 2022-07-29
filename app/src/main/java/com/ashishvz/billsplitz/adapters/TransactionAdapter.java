package com.ashishvz.billsplitz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.models.Transaction;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private final Context context;
    private final List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_transactions, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.splitName.setText(transaction.splitName);
        holder.amount.setText(String.format(Locale.US, "%s%s", context.getResources().getString(R.string.rupee_symbol), transaction.transactionAmount));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView splitName;
        private final TextView amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            splitName = itemView.findViewById(R.id.splitName);
            amount = itemView.findViewById(R.id.amountText);
        }
    }
}
