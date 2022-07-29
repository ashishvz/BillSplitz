package com.ashishvz.billsplitz.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.adapters.SplitStatusAdapter;
import com.ashishvz.billsplitz.models.SharedPrefUtils;
import com.ashishvz.billsplitz.models.SplitStatus;
import com.ashishvz.billsplitz.models.Splits;
import com.ashishvz.billsplitz.models.Transaction;
import com.ashishvz.billsplitz.models.UnevenSplit;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class BillSplitStatusFragment extends Fragment implements SplitStatusAdapter.OnSplitPayClick {
    private TextView name;
    private TextView createdBy;
    private TextView totalAmount;
    private TextView splitAmount;
    private RecyclerView recyclerView;
    private Splits splits;
    private List<SplitStatus> splitStatuses;
    private String documentId;
    private SplitStatusAdapter adapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_split_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        name = view.findViewById(R.id.splitName);
        createdBy = view.findViewById(R.id.splitCreatedBy);
        totalAmount = view.findViewById(R.id.splitTotalAmount);
        splitAmount = view.findViewById(R.id.splitAmount);
        recyclerView = view.findViewById(R.id.splitStatusRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (getArguments() != null) {
            splits = (Splits) getArguments().getSerializable("split");
            documentId = getArguments().getString("docId");
        }
        if (splits != null) {
            name.setText(splits.name);
            createdBy.setText(String.format(Locale.US, "%s %s", "Created By", splits.createdBy));
            totalAmount.setText(String.format(Locale.US, "%s%.2f", getActivity().getResources().getString(R.string.rupee_symbol), splits.totalAmount));
            splitAmount.setText(splits.isUneven ? "Uneven" : String.format(Locale.US, "%s%.2f", getActivity().getResources().getString(R.string.rupee_symbol), splits.splitAmount));
            if (splits.splitStatus != null) {
                splitStatuses = splits.getSplitStatusToList();
                adapter = new SplitStatusAdapter(getActivity(), splitStatuses, BillSplitStatusFragment.this);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onClick(int position) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_payment);
        dialog.setCancelable(false);
        MaterialButton payButton = dialog.findViewById(R.id.payButton);
        MaterialButton payCancelButton = dialog.findViewById(R.id.payCancelButton);
        TextView payText = dialog.findViewById(R.id.payText);
        Double amount = 0d;
        if (splits.isUneven) {
            for (UnevenSplit split: splits.unevenSplits)
                if (split.phoneNumber.toString().equals(SharedPrefUtils.getPhoneNumber(requireContext()).toString().replace("+91", "")))
                    amount = split.amount;
        } else {
            amount = splits.splitAmount;
        }
        payText.setText(String.format(Locale.US, "%s %s %.2f %s", "Paying", getActivity().getString(R.string.rupee_symbol), amount, splits.createdBy));
        payButton.setText(String.format(Locale.US, "%s %s%.2f", "Pay", getActivity().getString(R.string.rupee_symbol), amount));
        Double finalAmount = amount;
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Processing Payment. Please wait");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        db.collection("splits").document(documentId).update("splitStatus." + splitStatuses.get(position).phoneNumber, true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        splitStatuses.get(position).status = true;
                                        adapter.notifyItemChanged(position);
                                        Transaction transaction = new Transaction();
                                        transaction.transactionId = UUID.randomUUID().toString();
                                        transaction.transactionAmount = finalAmount;
                                        transaction.splitName = splits.name;
                                        transaction.createdAt = new Date().getTime();
                                        transaction.createdBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        db.collection("transactions").document().set(transaction)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        dialog.dismiss();
                                                        Toast.makeText(getActivity(), "Payment Successful", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                    }
                }, 5000);
            }
        });
        payCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
