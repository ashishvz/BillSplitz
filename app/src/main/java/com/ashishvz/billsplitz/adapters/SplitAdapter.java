package com.ashishvz.billsplitz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.models.Splits;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Locale;

public class SplitAdapter extends RecyclerView.Adapter<SplitAdapter.ViewHolder> {
    private final List<Splits> splits;
    private final Context context;
    private final splitCardClick splitCardClick;

    public SplitAdapter(List<Splits> splits, Context context, splitCardClick splitCardClick) {
        this.splits = splits;
        this.context = context;
        this.splitCardClick = splitCardClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_bill_split, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Splits split = splits.get(position);
        holder.name.setText(split.name);
        holder.amount.setText(String.format(Locale.US, "%s%s", context.getResources().getString(R.string.rupee_symbol), split.totalAmount));
        holder.createdBy.setText(String.format(Locale.US, "%s %s", "Created By", split.createdBy));
        holder.splitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                splitCardClick.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return splits.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, createdBy;
        MaterialCardView splitCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.splitName);
            amount = itemView.findViewById(R.id.splitAmount);
            createdBy = itemView.findViewById(R.id.splitCreatedBy);
            splitCard = itemView.findViewById(R.id.splitCard);
        }
    }

    public interface splitCardClick {
        void onClick(int position);
    }
}
