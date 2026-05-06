package com.example.roomease.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomease.R;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.Chore;
import com.example.roomease.database.entities.Roommate;

import java.util.List;

public class ChoreAdapter extends RecyclerView.Adapter<ChoreAdapter.ViewHolder> {

    public interface OnChoreActionListener {
        void onMarkComplete(Chore chore);
        void onViewHistory(Chore chore);
    }

    private List<Chore>           chores;
    private OnChoreActionListener listener;
    private RoomEaseDatabase      db;

    public ChoreAdapter(Context ctx, List<Chore> chores, OnChoreActionListener listener) {
        this.chores   = chores;
        this.listener = listener;
        this.db       = RoomEaseDatabase.getInstance(ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chore, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Chore chore = chores.get(position);

        h.tvTitle.setText(chore.getTitle());

        // Show description only if there is one
        if (chore.getDescription() != null && !chore.getDescription().isEmpty()) {
            h.tvDescription.setText(chore.getDescription());
            h.tvDescription.setVisibility(View.VISIBLE);
        } else {
            h.tvDescription.setVisibility(View.GONE);
        }

        // Look up the assigned roommate's name
        Roommate assigned = db.roommateDao().getRoommateById(chore.getAssignedToId());
        h.tvAssigned.setText("Assigned to: " + (assigned != null ? assigned.getName() : "—"));

        h.tvDue.setText("Due: " + (chore.getDueDate() != null ? chore.getDueDate() : "—"));

        // Colour the status badge depending on whether it's done or pending
        if ("Completed".equals(chore.getStatus())) {
            h.tvStatus.setText("✓ Done");
            h.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
            h.tvStatus.setTextColor(h.tvStatus.getContext().getColor(R.color.status_completed));
            h.btnMarkComplete.setEnabled(false);
            h.btnMarkComplete.setAlpha(0.4f);
        } else {
            h.tvStatus.setText("Pending");
            h.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            h.tvStatus.setTextColor(h.tvStatus.getContext().getColor(R.color.status_pending));
            h.btnMarkComplete.setEnabled(true);
            h.btnMarkComplete.setAlpha(1.0f);
        }

        // Show the rotation badge only if enabled
        h.tvRotationTag.setVisibility(chore.isRotationEnabled() ? View.VISIBLE : View.GONE);

        h.btnMarkComplete.setOnClickListener(v -> {
            if (listener != null) listener.onMarkComplete(chore);
        });

        h.btnViewHistory.setOnClickListener(v -> {
            if (listener != null) listener.onViewHistory(chore);
        });
    }

    @Override
    public int getItemCount() { return chores.size(); }

    public void updateData(List<Chore> newList) {
        this.chores = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvAssigned, tvDue, tvStatus, tvRotationTag;
        Button   btnMarkComplete, btnViewHistory;

        ViewHolder(View v) {
            super(v);
            tvTitle        = v.findViewById(R.id.tv_chore_title);
            tvDescription  = v.findViewById(R.id.tv_chore_description);
            tvAssigned     = v.findViewById(R.id.tv_chore_assigned);
            tvDue          = v.findViewById(R.id.tv_chore_due);
            tvStatus       = v.findViewById(R.id.tv_chore_status);
            tvRotationTag  = v.findViewById(R.id.tv_rotation_tag);
            btnMarkComplete= v.findViewById(R.id.btn_mark_complete);
            btnViewHistory = v.findViewById(R.id.btn_view_history);
        }
    }
}
