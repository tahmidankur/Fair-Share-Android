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

public class RoommateAdapter extends RecyclerView.Adapter<RoommateAdapter.ViewHolder> {
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

        holder.tvInitial.setText(r.getName().isEmpty() ? "?" :
                String.valueOf(r.getName().charAt(0)).toUpperCase());

        holder.tvName.setText(r.getName());

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

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDeleteClick(r);
        });
    }

    @Override
    public int getItemCount() { return roommates.size(); }

    public void updateData(List<Roommate> newList) {
        this.roommates = newList;
        notifyDataSetChanged();
    }

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
