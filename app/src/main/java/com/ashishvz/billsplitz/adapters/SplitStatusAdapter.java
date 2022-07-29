package com.ashishvz.billsplitz.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.models.SharedPrefUtils;
import com.ashishvz.billsplitz.models.SplitStatus;
import com.ashishvz.billsplitz.models.Splits;

import java.util.List;

public class SplitStatusAdapter extends RecyclerView.Adapter<SplitStatusAdapter.ViewHolder> {
    private final Context context;
    private final List<SplitStatus> splitStatuses;
    private final OnSplitPayClick onSplitPayClick;

    public SplitStatusAdapter(Context context, List<SplitStatus> splits, OnSplitPayClick onSplitPayClick) {
        this.context = context;
        this.splitStatuses = splits;
        this.onSplitPayClick = onSplitPayClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_status, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SplitStatus splitStatus = splitStatuses.get(position);
        String name = getContactName(splitStatus.phoneNumber.replace("+91", ""));
        holder.name.setText(name != null ? name : splitStatus.phoneNumber);
        if (splitStatus.status) {
            holder.button.setText("Paid");
            holder.button.setBackgroundColor(context.getColor(R.color.green));
        } else  {
            holder.button.setText("Pay");
            holder.button.setBackgroundColor(context.getColor(R.color.pay));
        }
        holder.button.setEnabled(false);
        if (splitStatus.phoneNumber.equalsIgnoreCase(String.valueOf(SharedPrefUtils.getPhoneNumber(context)))) {
            holder.name.setText(SharedPrefUtils.getUserName(context));
            if (holder.button.getText().toString().equalsIgnoreCase("Pay")) {
                holder.button.setEnabled(true);
            }
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSplitPayClick.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return splitStatuses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final Button button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            button = itemView.findViewById(R.id.payButton);
        }
    }


    public String getContactName(final String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName = null;
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst())
                contactName = cursor.getString(0);
            cursor.close();
        }
        return contactName;
    }

    public interface OnSplitPayClick {
        void onClick(int position);
    }
}
