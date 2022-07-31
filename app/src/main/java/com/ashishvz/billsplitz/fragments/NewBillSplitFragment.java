package com.ashishvz.billsplitz.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.adapters.ContactListAdapter;
import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.adapters.UnEvenSplitAdapter;
import com.ashishvz.billsplitz.models.Contact;
import com.ashishvz.billsplitz.models.SharedPrefUtils;
import com.ashishvz.billsplitz.models.Splits;
import com.ashishvz.billsplitz.models.UnevenSplit;
import com.ashishvz.billsplitz.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NewBillSplitFragment extends Fragment{

    private RecyclerView recyclerView;
    private List<Contact> contactList;
    private ContactListAdapter adapter;
    private MaterialTextView noContacts;
    private ProgressBar progressBar;
    private MaterialButton splitButton;
    private TextInputEditText splitAmountEditText;
    private TextInputEditText splitNameEditText;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private MaterialCheckBox unevenCheckbox;
    private RecyclerView unevenRecycler;
    private List<UnevenSplit> unevenSplits;
    private UnEvenSplitAdapter unEvenSplitAdapter;
    private Button unevenSplitBtn;
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_bill_split, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.contactListView);
        progressBar = view.findViewById(R.id.contactLoadingProgressBar);
        noContacts = view.findViewById(R.id.noContactsFoundText);
        splitButton = view.findViewById(R.id.splitButton);
        splitAmountEditText = view.findViewById(R.id.splitEditText);
        splitNameEditText = view.findViewById(R.id.splitNameEditText);
        unevenCheckbox = view.findViewById(R.id.unevenSpiltCheckBox);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Creating the split. Please wait!");
        progressDialog.setCancelable(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setVisibility(View.GONE);
        noContacts.setVisibility(View.GONE);
        contactList = getContactList();
        if (contactList.size() > 0) {
            adapter = new ContactListAdapter(getActivity(), contactList);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            noContacts.setVisibility(View.VISIBLE);
        }
        splitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Contact> selectedContact = adapter.getSelectedContacts();
                int selectedSize = 0;
                Map<Long, Boolean> contactData = new HashMap<>();
                contactData.put(SharedPrefUtils.getPhoneNumber(getActivity()), true);
                for (Contact con : selectedContact) {
                    if (con.isSelected) {
                        selectedSize = selectedSize + 1;
                        contactData.put(Long.parseLong(con.getPhoneNumber().replace("-", "").replace("(", "").replace(")", "")), false);
                    }
                }
                if (selectedSize > 0) {
                    if (splitNameEditText.getText() != null && !splitNameEditText.getText().toString().isEmpty()) {
                        if (splitAmountEditText.getText() != null && !splitAmountEditText.getText().toString().isEmpty()) {
                            progressDialog.show();
                            Double totalAmount = Double.parseDouble(splitAmountEditText.getText().toString());
                            Double splitAmount = Double.parseDouble(new DecimalFormat("0.00").format(Double.parseDouble(splitAmountEditText.getText().toString()) / (selectedSize + 1)));
                            List<String> data = new ArrayList<>();
                            for (Long ph : contactData.keySet()) {
                                data.add(String.valueOf(ph));
                            }
                            System.out.println(data);
                            db.collection("users")
                                    .whereIn("phoneNumber", data)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().size() > 0)
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d("Query", document.getId() + " => " + document.getData());
                                                    }
                                            } else {
                                                Log.d("Query", "No Data");
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    Log.d("Query", String.valueOf(queryDocumentSnapshots.getDocuments().size()));
                                    if (unevenCheckbox.isChecked()) {
                                        Dialog dialog = new Dialog(getActivity());
                                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.dialog_uneven_split);
                                        unevenRecycler = dialog.findViewById(R.id.unevenSplitRecyclerView);
                                        unevenSplitBtn = dialog.findViewById(R.id.unevenSplitButton);
                                        unevenSplits = new ArrayList<>();
                                        for (Long ph: contactData.keySet()) {
                                            UnevenSplit unevenSplit = new UnevenSplit();
                                            unevenSplit.amount = splitAmount;
                                            unevenSplit.phoneNumber = ph;
                                            unevenSplit.name = getContactName(ph.toString().replace("+91", ""));
                                            unevenSplits.add(unevenSplit);
                                        }
                                        unEvenSplitAdapter = new UnEvenSplitAdapter(getActivity(), unevenSplits);
                                        unevenRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        unevenRecycler.setAdapter(unEvenSplitAdapter);
                                        progressDialog.dismiss();
                                        unevenSplitBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                progressDialog.show();
                                                if (unEvenSplitAdapter.getSum().equals(totalAmount)) {
                                                    Splits splits = new Splits();
                                                    splits.splitAmount = splitAmount;
                                                    splits.totalAmount = Double.parseDouble(splitAmountEditText.getText().toString());
                                                    splits.createdBy = SharedPrefUtils.getUserName(getActivity());
                                                    splits.createdAt = new Date().getTime();
                                                    Map<String, Boolean> statusData = new HashMap<>();
                                                    for (String mob : data) {
                                                        if (Long.parseLong(mob) == SharedPrefUtils.getPhoneNumber(getActivity()))
                                                            statusData.put(mob, true);
                                                        else
                                                            statusData.put(mob, false);
                                                    }
                                                    splits.splitStatus = statusData;
                                                    splits.name = splitNameEditText.getText().toString();
                                                    splits.isUneven = true;
                                                    splits.unevenSplits = unevenSplits;
                                                    db.collection("splits").add(splits).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("billSplits", FieldValue.arrayUnion(documentReference.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    for (String ph : data) {
                                                                        if (ph.equalsIgnoreCase(String.valueOf(SharedPrefUtils.getPhoneNumber(getActivity())))) {
                                                                            continue;
                                                                        }
                                                                        db.collection("users").whereEqualTo("phoneNumber", ph)
                                                                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                                                                                    db.collection("users").document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                                                                            .update("billSplits", FieldValue.arrayUnion(documentReference.getId()))
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    Log.d("Update Users", "Updated");
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    progressDialog.dismiss();
                                                                    dialog.dismiss();
                                                                    Toast.makeText(getActivity(), "Split Created", Toast.LENGTH_SHORT).show();
                                                                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
                                                                    navController.navigate(R.id.action_newBillSplitFragment_to_billSplitFragment);

                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(getActivity(), "Total amount is greater than Expense amount", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                        dialog.show();
                                    } else {
                                        Splits splits = new Splits();
                                        splits.splitAmount = splitAmount;
                                        splits.totalAmount = Double.parseDouble(splitAmountEditText.getText().toString());
                                        splits.createdBy = SharedPrefUtils.getUserName(getActivity());
                                        splits.createdAt = new Date().getTime();
                                        Map<String, Boolean> statusData = new HashMap<>();
                                        for (String mob : data) {
                                            if (Long.parseLong(mob) == SharedPrefUtils.getPhoneNumber(getActivity()))
                                                statusData.put(mob, true);
                                            else
                                                statusData.put(mob, false);
                                        }
                                        splits.splitStatus = statusData;
                                        splits.name = splitNameEditText.getText().toString();
                                        splits.isUneven = false;
                                        splits.unevenSplits = null;
                                        db.collection("splits").add(splits).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("billSplits", FieldValue.arrayUnion(documentReference.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        for (String ph : data) {
                                                            if (ph.equalsIgnoreCase(String.valueOf(SharedPrefUtils.getPhoneNumber(getActivity())))) {
                                                                continue;
                                                            }
                                                            db.collection("users").whereEqualTo("phoneNumber", ph)
                                                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                                                                        db.collection("users").document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                                                                .update("billSplits", FieldValue.arrayUnion(documentReference.getId()))
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Log.d("Update Users", "Updated");
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), "Split Created", Toast.LENGTH_SHORT).show();
                                                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
                                                        navController.navigate(R.id.action_newBillSplitFragment_to_billSplitFragment);

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Please enter the split amount", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Please enter the split name", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Select contact to split bill", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<Contact> getContactList() {
        ContentResolver cr = getActivity().getContentResolver();
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<String>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "").replace("+91", "");
                    if (!mobileNoSet.contains(number)) {
                        contacts.add(new Contact(name, number, false));
                        mobileNoSet.add(number);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return contacts;
    }

    public String getContactName(final String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }
}
