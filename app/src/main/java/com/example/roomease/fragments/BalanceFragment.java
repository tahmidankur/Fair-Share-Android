package com.example.roomease.fragments;

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
import com.example.roomease.adapters.BalanceAdapter;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.helpers.ExpenseCalculationHelper;
import com.example.roomease.models.BalanceEntry;

import java.util.List;

/**
 * BalanceFragment — the "who owes whom" screen.
 *
 * Shows two sections:
 * 1. "Who Owes What"        — per-expense raw debts
 * 2. "Settlement Suggestions" — optimised minimum payments
 *
 * Both lists are computed by ExpenseCalculationHelper.
 */
public class BalanceFragment extends Fragment {

    private RoomEaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_balance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = RoomEaseDatabase.getInstance(requireContext());

        RecyclerView rvDebts       = view.findViewById(R.id.rv_debts);
        RecyclerView rvSettlements = view.findViewById(R.id.rv_settlements);

        rvDebts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSettlements.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDebts.setNestedScrollingEnabled(false);
        rvSettlements.setNestedScrollingEnabled(false);

        refreshBalance(view, rvDebts, rvSettlements);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            RecyclerView rvDebts       = getView().findViewById(R.id.rv_debts);
            RecyclerView rvSettlements = getView().findViewById(R.id.rv_settlements);
            refreshBalance(getView(), rvDebts, rvSettlements);
        }
    }

    private void refreshBalance(View root, RecyclerView rvDebts, RecyclerView rvSettlements) {
        ExpenseCalculationHelper calc = new ExpenseCalculationHelper(db);

        List<BalanceEntry> rawDebts     = calc.getRawDebts();
        List<BalanceEntry> settlements  = calc.getSettlements();

        TextView tvAllSettled    = root.findViewById(R.id.tv_all_settled);
        TextView tvLabelDebts    = root.findViewById(R.id.tv_label_debts);
        TextView tvLabelSettle   = root.findViewById(R.id.tv_label_settle);

        if (rawDebts.isEmpty() && settlements.isEmpty()) {
            // Nobody owes anyone — show the happy message
            tvAllSettled.setVisibility(View.VISIBLE);
            tvLabelDebts.setVisibility(View.GONE);
            tvLabelSettle.setVisibility(View.GONE);
            rvDebts.setVisibility(View.GONE);
            rvSettlements.setVisibility(View.GONE);
        } else {
            tvAllSettled.setVisibility(View.GONE);
            tvLabelDebts.setVisibility(View.VISIBLE);
            tvLabelSettle.setVisibility(View.VISIBLE);
            rvDebts.setVisibility(View.VISIBLE);
            rvSettlements.setVisibility(View.VISIBLE);

            rvDebts.setAdapter(new BalanceAdapter(rawDebts));
            rvSettlements.setAdapter(new BalanceAdapter(settlements));
        }
    }
}
