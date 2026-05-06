package com.example.roomease.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomease.R;
import com.example.roomease.activities.AddChoreActivity;
import com.example.roomease.activities.ChoreHistoryActivity;
import com.example.roomease.adapters.ChoreAdapter;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.Chore;
import com.example.roomease.helpers.ChoreRotationHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * ChoresFragment — shows all chores, with buttons to mark them done or view history.
 */
public class ChoresFragment extends Fragment {

    private ChoreAdapter     adapter;
    private RoomEaseDatabase db;
    private RecyclerView     rv;
    private TextView         tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chores, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db      = RoomEaseDatabase.getInstance(requireContext());
        rv      = view.findViewById(R.id.rv_chores);
        tvEmpty = view.findViewById(R.id.tv_empty_chores);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Chore> chores = db.choreDao().getAllChores();
        adapter = new ChoreAdapter(requireContext(), chores, new ChoreAdapter.OnChoreActionListener() {
            @Override
            public void onMarkComplete(Chore chore) {
                // Use the helper to handle rotation + history + date update
                new ChoreRotationHelper(db).completeAndRotate(chore);
                Toast.makeText(requireContext(), chore.getTitle() + " done! ✓",
                        Toast.LENGTH_SHORT).show();
                refreshList();
            }

            @Override
            public void onViewHistory(Chore chore) {
                Intent i = new Intent(requireContext(), ChoreHistoryActivity.class);
                i.putExtra(ChoreHistoryActivity.EXTRA_CHORE_ID, chore.getId());
                i.putExtra(ChoreHistoryActivity.EXTRA_CHORE_TITLE, chore.getTitle());
                startActivity(i);
            }
        });
        rv.setAdapter(adapter);

        updateEmptyState(chores);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_chore);
        fab.setOnClickListener(v ->
            startActivity(new Intent(requireContext(), AddChoreActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<Chore> chores = db.choreDao().getAllChores();
        if (adapter != null) adapter.updateData(chores);
        updateEmptyState(chores);
    }

    private void updateEmptyState(List<?> list) {
        boolean empty = list.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
