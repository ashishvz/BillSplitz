package com.ashishvz.billsplitz.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.models.SharedPrefUtils;
import com.ashishvz.billsplitz.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class DetailsFragment extends Fragment {
    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private MaterialButton saveButton;
    private FirebaseFirestore db;
    private NavController navController;
    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getActivity() == null) {
            return;
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving the data. Please wait");
        navController = Navigation.findNavController(getActivity(), R.id.nav_host);
        nameEditText = view.findViewById(R.id.fullNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        saveButton = view.findViewById(R.id.detailSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getText() != null && nameEditText.getText().toString().isEmpty()) {
                    nameEditText.setError("Name cannot be empty");
                    return;
                }
                if (emailEditText.getText() != null && emailEditText.getText().toString().isEmpty()) {
                    emailEditText.setError("Email cannot be empty");
                    return;
                }
                progressDialog.show();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                UserDetails userDetails = new UserDetails();
                userDetails.setFullName(nameEditText.getText().toString().trim());
                userDetails.setEmail(emailEditText.getText().toString().trim());
                if (user != null && user.getPhoneNumber() != null)
                    userDetails.setPhoneNumber(user.getPhoneNumber().replace("+91", ""));
                else
                    userDetails.setPhoneNumber("NA");
                userDetails.setCreatedAt(new Date().getTime());
                userDetails.setBillSplits(Collections.emptyList());
                if (user != null) {
                    db.collection("users").document(user.getUid()).set(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (getActivity() == null)
                                    return;
                                SharedPrefUtils.setUserName(getActivity(), userDetails.getFullName());
                                navController.navigate(R.id.action_detailsFragment_to_homeFragment);
                            } else {
                                System.out.println(task.getResult());
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}
