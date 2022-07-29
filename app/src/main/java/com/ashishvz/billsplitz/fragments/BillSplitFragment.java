package com.ashishvz.billsplitz.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.adapters.SplitAdapter;
import com.ashishvz.billsplitz.models.Splits;
import com.ashishvz.billsplitz.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BillSplitFragment extends Fragment implements SplitAdapter.splitCardClick {
    private ExtendedFloatingActionButton floatingActionButton;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private List<Splits> splits;
    private RecyclerView splitsRecycler;
    private SplitAdapter adapter;
    private MaterialTextView noSplitFound;
    private NavController navController;
    private List<String> documentIdList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bill_split, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        floatingActionButton = view.findViewById(R.id.newSplitBill);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        splits = new ArrayList<>();
        documentIdList = new ArrayList<>();
        splitsRecycler = view.findViewById(R.id.bill_split_recyclerView);
        noSplitFound = view.findViewById(R.id.noSplitText);
        splitsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Bill Splits");
        progressDialog.setCancelable(false);
        progressDialog.show();
        navController = Navigation.findNavController(getActivity(), R.id.nav_host);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.newBillSplitFragment);
            }
        });
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                            if (userDetails != null && userDetails.getBillSplits() != null && userDetails.getBillSplits().size() > 0) {
                                db.collection("splits").orderBy("createdAt", Query.Direction.DESCENDING).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (userDetails.getBillSplits().contains(document.getId())) {
                                                            Log.d("Splits", document.getId() + " => " + document.getData());
                                                            Splits split = document.toObject(Splits.class);
                                                            splits.add(split);
                                                            documentIdList.add(document.getId());
                                                        }
                                                    }
                                                    adapter = new SplitAdapter(splits, getActivity(), BillSplitFragment.this);
                                                    splitsRecycler.setAdapter(adapter);
                                                    noSplitFound.setVisibility(View.GONE);
                                                } else {
                                                    Log.d("Splits", "Error getting documents: ", task.getException());
                                                }
                                                progressDialog.dismiss();
                                            }
                                        });
                            } else  {
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("split", splits.get(position));
        bundle.putString("docId", documentIdList.get(position));
        navController.navigate(R.id.billSplitStatusFragment, bundle);
    }
}
