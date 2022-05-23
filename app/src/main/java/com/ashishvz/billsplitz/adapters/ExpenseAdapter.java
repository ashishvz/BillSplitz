package com.ashishvz.billsplitz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.models.Expenses;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    public Context context;
    public List<Expenses> expenses;

    public ExpenseAdapter(Context context, List<Expenses> expenses) {
        this.context = context;
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_expenses, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expenses ex = expenses.get(position);
        holder.amountText.setText(String.format(Locale.US,"%s%d", context.getResources().getString(R.string.rupee_symbol), ex.spentAmount));
        holder.shoppingText.setText(ex.expenseType);
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView shoppingText, amountText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shoppingText = itemView.findViewById(R.id.textExpenseType);
            amountText = itemView.findViewById(R.id.textAmount);
        }
    }
}
