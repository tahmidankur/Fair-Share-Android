package com.example.roomease.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomease.R;
import com.example.roomease.database.entities.Roommate;

import java.util.List;

/**
 * RoommateAdapter — bridges the roommates list and the RecyclerView.
 *
 * How a RecyclerView Adapter works (for beginners):
 * - onCreateViewHolder: inflates (creates) the card layout for one item
 * - onBindViewHolder:   fills that card with the actual data
 * - getItemCount:       tells RecyclerView how many items to show
 */
public class RoommateAdapter extends RecyclerView.Adapter<RoommateAdapter.ViewHolder> {

    // Callback interface — called when user taps Delete
    public interface OnDeleteClickListener {
        void onDeleteClick(Roommate roommate);
    }

    private List<Roommate>          roommates;
    private OnDeleteClickListener   deleteListener;

    public RoommateAdapter(List<Roommate> roommates, OnDeleteClickListener listener) {
        this.roommates      = roommates;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_roommate, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Roommate r = roommates.get(position);

        // Show the first letter of the name in the avatar circle
        holder.tvInitial.setText(r.getName().isEmpty() ? "?" :
                String.valueOf(r.getName().charAt(0)).toUpperCase());

        holder.tvName.setText(r.getName());

        // Only show email/phone rows if they have a value
        if (r.getEmail() != null && !r.getEmail().isEmpty()) {
            holder.tvEmail.setText(r.getEmail());
            holder.tvEmail.setVisibility(View.VISIBLE);
        } else {
            holder.tvEmail.setVisibility(View.GONE);
        }

        if (r.getPhone() != null && !r.getPhone().isEmpty()) {
            holder.tvPhone.setText(r.getPhone());
            holder.tvPhone.setVisibility(View.VISIBLE);
        } else {
            holder.tvPhone.setVisibility(View.GONE);
        }

        // Wire the delete button to our callback
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDeleteClick(r);
        });
    }

    @Override
    public int getItemCount() { return roommates.size(); }

    // Update the list and redraw
    public void updateData(List<Roommate> newList) {
        this.roommates = newList;
        notifyDataSetChanged();
    }

    // ViewHolder — holds references to the views in one card so we don't
    // have to call findViewById() on every scroll (that would be slow)
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView    tvInitial, tvName, tvEmail, tvPhone;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tv_roommate_initial);
            tvName    = itemView.findViewById(R.id.tv_roommate_name);
            tvEmail   = itemView.findViewById(R.id.tv_roommate_email);
            tvPhone   = itemView.findViewById(R.id.tv_roommate_phone);
            btnDelete = itemView.findViewById(R.id.btn_delete_roommate);
        }
    }
}
