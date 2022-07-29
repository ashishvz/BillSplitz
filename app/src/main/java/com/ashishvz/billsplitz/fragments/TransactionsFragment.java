package com.ashishvz.billsplitz.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.adapters.TransactionAdapter;
import com.ashishvz.billsplitz.models.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private List<Transaction> transactionList;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.transactionsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        transactionList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching transactions. Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        db.collection("transactions").whereEqualTo("createdBy", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                                transactionList.add(queryDocumentSnapshot.toObject(Transaction.class));
                            }
                            adapter = new TransactionAdapter(getActivity(), transactionList);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
