package com.ashishvz.billsplitz.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.ashishvz.billsplitz.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    NavHostFragment navHostFragment;
    NavController navController;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getPermission();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        navController = navHostFragment.getNavController();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        navController.navigate(R.id.homeFragment);
                        return true;
                    case R.id.billSplit:
                        navController.navigate(R.id.billSplitFragment);
                        return true;
                    case R.id.transactions:
                        navController.navigate(R.id.transactionsFragment);
                        return true;
                }
                return false;
            }
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getLabel() != null && navDestination.getLabel().equals("HomeFragment")) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 1);
            }
        }
    }
}