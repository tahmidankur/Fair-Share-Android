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

/**
 * ExpenseCalculationHelper — figures out who owes whom and how much.
 *
 * Two types of results:
 * 1. getRawDebts()    — per-expense breakdown, e.g. "Ahmed owes Tahmid $10 for Groceries"
 * 2. getSettlements() — simplified totals using the minimum number of transactions
 */
public class ExpenseCalculationHelper {

    private final RoomEaseDatabase db;

    public ExpenseCalculationHelper(RoomEaseDatabase db) {
        this.db = db;
    }

    /**
     * Returns one BalanceEntry for each individual debt across all expenses.
     * The payer is never listed as owing themselves.
     */
    public List<BalanceEntry> getRawDebts() {
        List<BalanceEntry> debts = new ArrayList<>();

        for (Expense expense : db.expenseDao().getAllExpenses()) {
            Roommate payer = db.roommateDao().getRoommateById(expense.getPaidById());
            if (payer == null) continue;

            for (ExpenseSplit split : db.expenseSplitDao().getSplitsForExpense(expense.getId())) {
                // Skip the payer — you don't owe yourself
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

    /**
     * Returns the minimum set of payments needed to settle all debts.
     *
     * How it works:
     * 1. Calculate each person's net balance (positive = owed money, negative = owes money)
     * 2. Match the biggest debtor with the biggest creditor
     * 3. Repeat until everyone is settled
     *
     * Example: Ahmed owes $6, John owes $4, Tahmid is owed $10
     * → Ahmed pays Tahmid $6, John pays Tahmid $4 (2 transactions, done)
     */
    public List<BalanceEntry> getSettlements() {
        // --- Step 1: build net balance map ---
        Map<Integer, Double> net = new HashMap<>();

        // Initialise every roommate at $0
        for (Roommate r : db.roommateDao().getAllRoommates()) {
            net.put(r.getId(), 0.0);
        }

        // Go through every split and adjust the net amounts
        for (Expense expense : db.expenseDao().getAllExpenses()) {
            for (ExpenseSplit split : db.expenseSplitDao().getSplitsForExpense(expense.getId())) {
                if (split.getRoommateId() == expense.getPaidById()) continue;

                double shareAmt = split.getShareAmount();

                // The debtor's balance goes more negative
                double debtorNet = net.getOrDefault(split.getRoommateId(), 0.0);
                net.put(split.getRoommateId(), debtorNet - shareAmt);

                // The payer's balance goes more positive (they're owed more)
                double payerNet = net.getOrDefault(expense.getPaidById(), 0.0);
                net.put(expense.getPaidById(), payerNet + shareAmt);
            }
        }

        // --- Step 2: match debtors to creditors greedily ---
        List<BalanceEntry> settlements = new ArrayList<>();

        // Separate into "owes money" (negative net) and "is owed money" (positive net)
        List<int[]>    debtors   = new ArrayList<>(); // [id, cents_owed]
        List<int[]>    creditors = new ArrayList<>(); // [id, cents_owed_to_them]

        for (Map.Entry<Integer, Double> entry : net.entrySet()) {
            int    id  = entry.getKey();
            double val = entry.getValue();
            if (val < -0.005) {
                // Store as positive cents internally for easy comparison
                debtors.add(new int[]{id, (int) Math.round(-val * 100)});
            } else if (val > 0.005) {
                creditors.add(new int[]{id, (int) Math.round(val * 100)});
            }
        }

        // Greedy matching — pair the biggest debtor with the biggest creditor
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
