package com.ashishvz.billsplitz.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.models.UnevenSplit;

import java.util.List;

public class UnEvenSplitAdapter extends RecyclerView.Adapter<UnEvenSplitAdapter.ViewHolder> {
    private final Context context;
    private final List<UnevenSplit> unevenSplits;

    public UnEvenSplitAdapter(Context context, List<UnevenSplit> unevenSplits) {
        this.context = context;
        this.unevenSplits = unevenSplits;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.uneven_split, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UnevenSplit unevenSplit = unevenSplits.get(position);
        holder.name.setText(unevenSplit.name);
        holder.amountET.setText(String.valueOf(unevenSplit.amount));
        holder.amountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    unevenSplit.amount = 0.0;
                else
                    unevenSplit.amount = Double.parseDouble(editable.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return unevenSplits.size();
    }

    public List<UnevenSplit> getSplitList() {
        return unevenSplits;
    }

    public Double getSum() {
        Double sum = 0d;
        for (UnevenSplit split: unevenSplits) {
            sum += split.amount;
        }
        return sum;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final EditText amountET;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.unevenName);
            amountET = itemView.findViewById(R.id.amountEditText);
        }
    }
}
