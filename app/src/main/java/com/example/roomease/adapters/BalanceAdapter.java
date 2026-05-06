package com.example.roomease.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomease.R;
import com.example.roomease.models.BalanceEntry;

import java.util.List;
import java.util.Locale;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder> {

    private List<BalanceEntry> entries;

    public BalanceAdapter(List<BalanceEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_balance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        BalanceEntry entry = entries.get(position);

        h.tvFrom.setText(entry.getFromName());
        h.tvTo.setText(entry.getToName());

        // Show "owes (for X)" for per-expense debts, or just "owes" for settlements
        if (entry.isRawDebt()) {
            h.tvLabel.setText("owes — " + entry.getExpenseTitle());
        } else {
            h.tvLabel.setText("owes");
        }

        h.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", entry.getAmount()));
    }

    @Override
    public int getItemCount() { return entries.size(); }

    public void updateData(List<BalanceEntry> newList) {
        this.entries = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFrom, tvLabel, tvTo, tvAmount;

        ViewHolder(View v) {
            super(v);
            tvFrom   = v.findViewById(R.id.tv_balance_from);
            tvLabel  = v.findViewById(R.id.tv_balance_label);
            tvTo     = v.findViewById(R.id.tv_balance_to);
            tvAmount = v.findViewById(R.id.tv_balance_amount);
        }
    }
}
