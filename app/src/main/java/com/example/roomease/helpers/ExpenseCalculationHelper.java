package com.example.roomease.helpers;

import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.Expense;
import com.example.roomease.database.entities.ExpenseSplit;
import com.example.roomease.database.entities.Roommate;
import com.example.roomease.models.BalanceEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseCalculationHelper {
    private final RoomEaseDatabase db;

    public ExpenseCalculationHelper(RoomEaseDatabase db) {
        this.db = db;
    }

    public List<BalanceEntry> getRawDebts() {
        List<BalanceEntry> debts = new ArrayList<>();

        for (Expense expense : db.expenseDao().getAllExpenses()) {
            Roommate payer = db.roommateDao().getRoommateById(expense.getPaidById());
            if (payer == null) continue;

            for (ExpenseSplit split : db.expenseSplitDao().getSplitsForExpense(expense.getId())) {
                if (split.getRoommateId() == expense.getPaidById()) continue;

                Roommate debtor = db.roommateDao().getRoommateById(split.getRoommateId());
                if (debtor == null) continue;

                debts.add(new BalanceEntry(
                    debtor.getName(),
                    payer.getName(),
                    split.getShareAmount(),
                    expense.getTitle()
                ));
            }
        }

        return debts;
    }

    public List<BalanceEntry> getSettlements() {
        Map<Integer, Double> net = new HashMap<>();

        for (Roommate r : db.roommateDao().getAllRoommates()) {
            net.put(r.getId(), 0.0);
        }

        for (Expense expense : db.expenseDao().getAllExpenses()) {
            for (ExpenseSplit split : db.expenseSplitDao().getSplitsForExpense(expense.getId())) {
                if (split.getRoommateId() == expense.getPaidById()) continue;

                double share = split.getShareAmount();
                net.merge(split.getRoommateId(), -share, Double::sum);
                net.merge(expense.getPaidById(), share, Double::sum);
            }
        }

        // work in cents so rounding doesn't leave people owing $0.01
        List<BalanceEntry> settlements = new ArrayList<>();
        List<int[]> debtors = new ArrayList<>();
        List<int[]> creditors = new ArrayList<>();

        for (Map.Entry<Integer, Double> entry : net.entrySet()) {
            int    id  = entry.getKey();
            double val = entry.getValue();
            if (val < -0.005) {
                debtors.add(new int[]{id, (int) Math.round(-val * 100)});
            } else if (val > 0.005) {
                creditors.add(new int[]{id, (int) Math.round(val * 100)});
            }
        }

        int di = 0, ci = 0;
        while (di < debtors.size() && ci < creditors.size()) {
            int[] debtor   = debtors.get(di);
            int[] creditor = creditors.get(ci);

            int payment = Math.min(debtor[1], creditor[1]);

            if (payment > 0) {
                Roommate from = db.roommateDao().getRoommateById(debtor[0]);
                Roommate to   = db.roommateDao().getRoommateById(creditor[0]);
                if (from != null && to != null) {
                    settlements.add(new BalanceEntry(from.getName(), to.getName(), payment / 100.0));
                }
            }

            debtor[1]   -= payment;
            creditor[1] -= payment;

            if (debtor[1]   <= 0) di++;
            if (creditor[1] <= 0) ci++;
        }

        return settlements;
    }
}
