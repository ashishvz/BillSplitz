package com.ashishvz.billsplitz.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.adapters.ExpenseAdapter;
import com.ashishvz.billsplitz.databases.AppDatabase;
import com.ashishvz.billsplitz.databases.entities.Expense;
import com.ashishvz.billsplitz.models.Expenses;
import com.ashishvz.billsplitz.models.SharedPrefUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {
    private PieChart pieChart;
    private ExtendedFloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private List<Integer> colorsList;
    private ExpenseAdapter adapter;
    private List<Expenses> expensesList;
    private List<Expense> expenseList;
    private AppDatabase appDatabase;
    private List<PieEntry> pieEntries;
    private MaterialTextView spentAmount;
    private MaterialTextView greetingText;
    private MaterialTextView chartNoDataFound, recentTransactionNoDataFound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null)
            return;
        appDatabase = AppDatabase.getInstance(getActivity());
        pieChart = view.findViewById(R.id.pieChart);
        floatingActionButton = view.findViewById(R.id.floatingActionBtn);
        recyclerView = view.findViewById(R.id.expenseRecycler);
        spentAmount = view.findViewById(R.id.spendsAmount);
        greetingText = view.findViewById(R.id.greetingText);
        chartNoDataFound = view.findViewById(R.id.chartNoDataFound);
        recentTransactionNoDataFound = view.findViewById(R.id.recentTransactionNoDataFound);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        expensesList = new ArrayList<>();
        pieEntries = new ArrayList<>();
        colorsList = new ArrayList<>();
        adapter = new ExpenseAdapter(getActivity(), expensesList);
        recyclerView.setAdapter(adapter);
        PieDataSet dataSet = new PieDataSet(pieEntries, "Expenses");
        for (int i = 0; i < 20; i++) {
            Random random = new Random();
            colorsList.add(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        dataSet.setColors(colorsList);
        dataSet.setValueTextColor(getActivity().getResources().getColor(R.color.background_grey));
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
        getDataAndPopulate();
        setGreetingText();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPersonalExpenses();
            }
        });
    }

    public void getDataAndPopulate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Long sum = appDatabase.expenseDao().getSumOfTop10();
                expenseList = appDatabase.expenseDao().getTop10Expenses();
                if (expenseList == null)
                    return;
                if (expenseList.size() <= 0) {
                    spentAmount.setText(String.format(Locale.US, "%s%d", "₹", 0));
                    return;
                }
                for (Expense expense : expenseList) {
                    expensesList.add(new Expenses(expense.expenseAmount, expense.expenseType));
                    pieEntries.add(new PieEntry(expense.expenseAmount, expense.expenseType));
                }
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chartNoDataFound.setVisibility(View.GONE);
                        recentTransactionNoDataFound.setVisibility(View.GONE);
                        spentAmount.setText(String.format(Locale.US, "%s%d", "₹", sum));
                        adapter.notifyItemRangeChanged(0, expensesList.size());
                        pieChart.notifyDataSetChanged();
                        pieChart.invalidate();
                    }
                });
            }
        }).start();
    }

    public void showAddPersonalExpenses() {
        if (getActivity() == null)
            return;
        MaterialAlertDialogBuilder materialDialogs = new MaterialAlertDialogBuilder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_personal_expenses, null);
        materialDialogs.setView(view);
        Spinner spinner = view.findViewById(R.id.expense_type_spinner);
        TextInputEditText editText = view.findViewById(R.id.amountEditText);
        Button save = view.findViewById(R.id.saveBtn);
        Button cancel = view.findViewById(R.id.cancelBtn);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getActivity().getResources().getStringArray(R.array.expense_type));
        spinner.setAdapter(adapter);
        AlertDialog alertDialog = materialDialogs.create();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner.getSelectedItemPosition() <= -1) {
                    Toast.makeText(getActivity(), "Please select expense type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editText.getText() == null) {
                    editText.setError("Amount cannot be empty");
                    return;
                }
                Long amount = Long.parseLong(editText.getText().toString());
                DateFormat dateFormat = new SimpleDateFormat("MMM", Locale.US);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() == null)
                            return;
                        appDatabase.expenseDao().insert(new Expense(null, getActivity().getResources().getStringArray(R.array.expense_type)[spinner.getSelectedItemPosition()], amount,
                                new Date().getTime(), dateFormat.format(new Date())));
                        alertDialog.cancel();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Your personal expense added", Toast.LENGTH_SHORT).show();
                            }
                        });
                        expensesList.clear();
                        pieEntries.clear();
                        getDataAndPopulate();
                    }
                }).start();

            }
        });
        alertDialog.show();
    }

    public void setGreetingText() {
        if (getActivity() != null) {
            greetingText.setText(String.format("%s %s", greetingText.getText().toString(), SharedPrefUtils.getUserName(getActivity())));
        }
    }
}
