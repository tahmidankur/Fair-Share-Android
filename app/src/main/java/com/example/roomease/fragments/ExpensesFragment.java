package com.example.roomease.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomease.R;
import com.example.roomease.activities.AddExpenseActivity;
import com.example.roomease.adapters.ExpenseAdapter;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.Expense;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

/**
 * ExpensesFragment — lists all logged expenses, newest first.
 * Header shows the running total.
 */
public class ExpensesFragment extends Fragment {

    private ExpenseAdapter   adapter;
    private RoomEaseDatabase db;
    private RecyclerView     rv;
    private TextView         tvEmpty, tvTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db      = RoomEaseDatabase.getInstance(requireContext());
        rv      = view.findViewById(R.id.rv_expenses);
        tvEmpty = view.findViewById(R.id.tv_empty_expenses);
        tvTotal = view.findViewById(R.id.tv_total_expenses);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Expense> expenses = db.expenseDao().getAllExpenses();
        adapter = new ExpenseAdapter(requireContext(), expenses);
        rv.setAdapter(adapter);

        updateUI(expenses);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_expense);
        fab.setOnClickListener(v ->
            startActivity(new Intent(requireContext(), AddExpenseActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Expense> expenses = db.expenseDao().getAllExpenses();
        if (adapter != null) adapter.updateData(expenses);
        updateUI(expenses);
    }

    private void updateUI(List<Expense> expenses) {
        boolean empty = expenses.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);

        double total = db.expenseDao().getTotalExpenseAmount();
        tvTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", total));
    }
}
