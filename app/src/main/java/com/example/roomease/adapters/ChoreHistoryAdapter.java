package com.example.roomease.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomease.R;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.ChoreHistory;
import com.example.roomease.database.entities.Roommate;

import java.util.List;

public class ChoreHistoryAdapter extends RecyclerView.Adapter<ChoreHistoryAdapter.ViewHolder> {

    private List<ChoreHistory> history;
    private RoomEaseDatabase   db;

    public ChoreHistoryAdapter(Context ctx, List<ChoreHistory> history) {
        this.history = history;
        this.db      = RoomEaseDatabase.getInstance(ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chore_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ChoreHistory item = history.get(position);

        h.tvChoreTitle.setText(item.getChoreTitle());

        Roommate who = db.roommateDao().getRoommateById(item.getCompletedById());
        h.tvCompletedBy.setText("Done by: " + (who != null ? who.getName() : "Unknown"));

        h.tvDate.setText(item.getCompletedDate());
    }

    @Override
    public int getItemCount() { return history.size(); }

    public void updateData(List<ChoreHistory> newList) {
        this.history = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvChoreTitle, tvCompletedBy, tvDate;

        ViewHolder(View v) {
            super(v);
            tvChoreTitle  = v.findViewById(R.id.tv_history_chore_title);
            tvCompletedBy = v.findViewById(R.id.tv_history_completed_by);
            tvDate        = v.findViewById(R.id.tv_history_date);
        }
    }
}
