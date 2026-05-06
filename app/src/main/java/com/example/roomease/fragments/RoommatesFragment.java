package com.example.roomease.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomease.R;
import com.example.roomease.activities.AddRoommateActivity;
import com.example.roomease.adapters.RoommateAdapter;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.Roommate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * RoommatesFragment — lists all roommates and lets you add or delete them.
 */
public class RoommatesFragment extends Fragment {

    private RoommateAdapter  adapter;
    private RoomEaseDatabase db;
    private RecyclerView     rv;
    private TextView         tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roommates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db      = RoomEaseDatabase.getInstance(requireContext());
        rv      = view.findViewById(R.id.rv_roommates);
        tvEmpty = view.findViewById(R.id.tv_empty_roommates);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Roommate> list = db.roommateDao().getAllRoommates();
        adapter = new RoommateAdapter(list, this::confirmDelete);
        rv.setAdapter(adapter);

        updateEmptyState(list);

        // FAB opens the Add Roommate screen
        FloatingActionButton fab = view.findViewById(R.id.fab_add_roommate);
        fab.setOnClickListener(v ->
            startActivity(new Intent(requireContext(), AddRoommateActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    /** Shows a confirmation dialog before deleting a roommate. */
    private void confirmDelete(Roommate r) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Remove Roommate")
            .setMessage("Remove " + r.getName() + "? This cannot be undone.")
            .setPositiveButton("Remove", (d, w) -> {
                db.roommateDao().delete(r);
                refreshList();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void refreshList() {
        List<Roommate> list = db.roommateDao().getAllRoommates();
        if (adapter != null) adapter.updateData(list);
        updateEmptyState(list);
    }

    private void updateEmptyState(List<?> list) {
        boolean empty = list.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
