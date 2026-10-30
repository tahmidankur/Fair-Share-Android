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
import com.example.roomease.adapters.ChoreHistoryAdapter;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.ChoreHistory;

import java.util.List;

public class HomeFragment extends Fragment {
    private RoomEaseDatabase   db;
    private ChoreHistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = RoomEaseDatabase.getInstance(requireContext());

        RecyclerView rv = view.findViewById(R.id.rv_recent_history);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<ChoreHistory> recent = db.choreHistoryDao().getRecentHistory();
        historyAdapter = new ChoreHistoryAdapter(requireContext(), recent);
        rv.setAdapter(historyAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStats();
    }

    private void refreshStats() {
        if (getView() == null) return;

        int roommateCount = db.roommateDao().getRoommateCount();
        int pendingChores = db.choreDao().getPendingChoreCount();
        int expenseCount  = db.expenseDao().getExpenseCount();

        ((TextView) getView().findViewById(R.id.tv_stat_roommates)).setText(String.valueOf(roommateCount));
        ((TextView) getView().findViewById(R.id.tv_stat_chores)).setText(String.valueOf(pendingChores));
        ((TextView) getView().findViewById(R.id.tv_stat_expenses)).setText(String.valueOf(expenseCount));

        List<ChoreHistory> recent = db.choreHistoryDao().getRecentHistory();
        TextView tvNoRecent = getView().findViewById(R.id.tv_no_recent);

        if (historyAdapter != null) {
            historyAdapter.updateData(recent);
        }

        tvNoRecent.setVisibility(recent.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
