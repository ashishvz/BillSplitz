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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LoginFragment extends Fragment {
    private TextInputEditText phoneNumberEditText, otpEditText;
    private TextInputLayout phoneNumberInputLayout, otpInputLayout;
    private MaterialButton verifyButton;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationId;
    private NavController navController;
    private Boolean isOTPGenerated = false;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private Long phoneNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            navController.navigate(R.id.action_loginFragment_to_homeFragment);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getActivity() == null)
            return;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        phoneNumberInputLayout = view.findViewById(R.id.phoneNumberInputLayout);
        otpEditText = view.findViewById(R.id.otpEditText);
        otpInputLayout = view.findViewById(R.id.otpInputLayout);
        verifyButton = view.findViewById(R.id.verifyPhoneNumberButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() == null)
                    return;
                if (!isOTPGenerated) {
                    if (phoneNumberEditText.getText() != null && !phoneNumberEditText.getText().toString().isEmpty() && phoneNumberEditText.getText().toString().length() == 10) {
                        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber("+91" + phoneNumberEditText.getText().toString().trim())
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(getActivity())
                                .setCallbacks(callbacks)
                                .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);
                        phoneNumberInputLayout.setVisibility(View.INVISIBLE);
                        otpInputLayout.setVisibility(View.VISIBLE);
                        verifyButton.setText("Verify");
                        isOTPGenerated = true;
                        phoneNumber = Long.parseLong(phoneNumberEditText.getText().toString().trim());
                    }
                } else {
                    if (otpEditText.getText() != null && !otpEditText.getText().toString().isEmpty() && otpEditText.getText().toString().length() == 6) {
                        progressDialog.setMessage("Verifying the OTP");
                        progressDialog.show();
                        PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationId, otpEditText.getText().toString().trim());
                        signInWithPhoneAuthCredential(authCredential);
                    }
                }
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if (getActivity() == null)
            return;
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            if (user != null) {
                                DocumentReference reference = db.collection("users").document(user.getUid());
                                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                                        if (userDetails != null) {
                                            if (getActivity() == null)
                                                return;
                                            SharedPrefUtils.setUserName(getActivity(), userDetails.getFullName());
                                            SharedPrefUtils.setPhoneNumber(getActivity(), phoneNumber);
                                            navController.navigate(R.id.action_loginFragment_to_homeFragment);
                                        } else {
                                            SharedPrefUtils.setPhoneNumber(getActivity(), phoneNumber);
                                            navController.navigate(R.id.action_loginFragment_to_detailsFragment);
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        } else {
                            System.out.println(task.getResult());
                        }
                    }
                });
    }
}
