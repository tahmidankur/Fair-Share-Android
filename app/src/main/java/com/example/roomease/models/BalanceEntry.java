package com.example.roomease.models;

/**
 * BalanceEntry — a simple data object representing one debt.
 * Example: "Ahmed owes Tahmid $6.00"
 *
 * We reuse this for both the raw debt list AND the settlement suggestions.
 * The optional 'expenseTitle' field is only set for per-expense raw debts.
 */
public class BalanceEntry {

    private String fromName;       // person who owes money
    private String toName;         // person who is owed money
    private double amount;         // how much is owed
    private String expenseTitle;   // null for net/settlement entries

    // Constructor for a per-expense debt entry
    public BalanceEntry(String fromName, String toName, double amount, String expenseTitle) {
        this.fromName     = fromName;
        this.toName       = toName;
        this.amount       = amount;
        this.expenseTitle = expenseTitle;
    }

    // Constructor for a net/settlement entry (no expense title needed)
    public BalanceEntry(String fromName, String toName, double amount) {
        this(fromName, toName, amount, null);
    }

    public String getFromName()     { return fromName;     }
    public String getToName()       { return toName;       }
    public double getAmount()       { return amount;       }
    public String getExpenseTitle() { return expenseTitle; }
    public boolean isRawDebt()      { return expenseTitle != null; }
}
