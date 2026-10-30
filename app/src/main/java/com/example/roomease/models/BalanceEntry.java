package com.example.roomease.models;

public class BalanceEntry {
    private String fromName;
    private String toName;
    private double amount;
    private String expenseTitle;

    public BalanceEntry(String fromName, String toName, double amount, String expenseTitle) {
        this.fromName     = fromName;
        this.toName       = toName;
        this.amount       = amount;
        this.expenseTitle = expenseTitle;
    }

    public BalanceEntry(String fromName, String toName, double amount) {
        this(fromName, toName, amount, null);
    }

    public String getFromName()     { return fromName;     }
    public String getToName()       { return toName;       }
    public double getAmount()       { return amount;       }
    public String getExpenseTitle() { return expenseTitle; }
    public boolean isRawDebt()      { return expenseTitle != null; }
}
