package com.example.roomease.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Expense — a shared household cost (groceries, electricity bill, etc.).
 * Records who paid and how much. The actual split is stored in ExpenseSplit.
 */
@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public double amount;    // total amount paid
    public int    paidById;  // roommate ID of who paid
    public String category;  // "Groceries", "Utilities", "Rent", etc.
    public String date;      // "yyyy-MM-dd"
    public String notes;     // optional extra info

    public Expense(String title, double amount, int paidById,
                   String category, String date, String notes) {
        this.title    = title;
        this.amount   = amount;
        this.paidById = paidById;
        this.category = category;
        this.date     = date;
        this.notes    = notes;
    }

    public int    getId()       { return id;       }
    public String getTitle()    { return title;    }
    public double getAmount()   { return amount;   }
    public int    getPaidById() { return paidById; }
    public String getCategory() { return category; }
    public String getDate()     { return date;     }
    public String getNotes()    { return notes;    }
}
