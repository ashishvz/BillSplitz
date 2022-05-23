package com.ashishvz.billsplitz.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.adapters.ExpenseAdapter;
import com.ashishvz.billsplitz.models.Expenses;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private PieChart pieChart;
    private ExtendedFloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<Expenses> expensesList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pieChart);
        floatingActionButton = view.findViewById(R.id.floatingActionBtn);
        recyclerView = view.findViewById(R.id.expenseRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        expensesList = new ArrayList<>();
        expensesList.add(new Expenses(12500L, "Shopping"));
        expensesList.add(new Expenses(5000L, "Food"));
        expensesList.add(new Expenses(5000L, "Household"));
        expensesList.add(new Expenses(10000L, "EMIs"));
        expensesList.add(new Expenses(6500L, "Groceries"));
        adapter = new ExpenseAdapter(getActivity(), expensesList);
        recyclerView.setAdapter(adapter);
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(5000L, "Food", getActivity().getResources().getColor(R.color.purple_200)));
        pieEntries.add(new PieEntry(12500L, "Shopping"));
        pieEntries.add(new PieEntry(10000L, "EMIs"));
        pieEntries.add(new PieEntry(5000L, "Household"));
        pieEntries.add(new PieEntry(6500L, "Groceries"));
        PieDataSet dataSet = new PieDataSet(pieEntries, "Expenses");
        List<Integer> colors = new ArrayList<>();
        colors.add(getActivity().getResources().getColor(R.color.purple_200));
        colors.add(getActivity().getResources().getColor(R.color.teal_200));
        colors.add(getActivity().getResources().getColor(R.color.purple_500));
        dataSet.setColors(colors);
        dataSet.setValueTextColor(getActivity().getResources().getColor(R.color.black));
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAddPersonalExpenses();
            }
        });
    }

    public void showAddPersonalExpenses() {
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
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }
}
