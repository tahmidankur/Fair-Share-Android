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
import com.example.roomease.database.entities.Expense;
import com.example.roomease.database.entities.Roommate;

import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense>    expenses;
    private RoomEaseDatabase db;

    public ExpenseAdapter(Context ctx, List<Expense> expenses) {
        this.expenses = expenses;
        this.db       = RoomEaseDatabase.getInstance(ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Expense e = expenses.get(position);

        h.tvTitle.setText(e.getTitle());
        // Format the amount as currency, e.g. "$18.50"
        h.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", e.getAmount()));
        h.tvCategory.setText(e.getCategory());

        Roommate payer = db.roommateDao().getRoommateById(e.getPaidById());
        h.tvPaidBy.setText("Paid by: " + (payer != null ? payer.getName() : "Unknown"));

        h.tvDate.setText(e.getDate());
    }

    @Override
    public int getItemCount() { return expenses.size(); }

    public void updateData(List<Expense> newList) {
        this.expenses = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvCategory, tvPaidBy, tvDate;

        ViewHolder(View v) {
            super(v);
            tvTitle    = v.findViewById(R.id.tv_expense_title);
            tvAmount   = v.findViewById(R.id.tv_expense_amount);
            tvCategory = v.findViewById(R.id.tv_expense_category);
            tvPaidBy   = v.findViewById(R.id.tv_expense_paid_by);
            tvDate     = v.findViewById(R.id.tv_expense_date);
        }
    }
}
