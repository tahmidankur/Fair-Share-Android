package com.example.roomease.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ExpenseSplit — how much each person owes for a specific expense.
 * Example: For a $30 grocery bill split 3 ways, there would be 3 ExpenseSplit rows,
 * each with shareAmount = 10.0.
 */
@Entity(tableName = "expense_splits")
public class ExpenseSplit {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int     expenseId;   // which expense this belongs to
    public int     roommateId;  // which roommate owes this amount
    public double  shareAmount; // how much this person owes
    public boolean isPaid;      // has this person settled up?

    public ExpenseSplit(int expenseId, int roommateId, double shareAmount, boolean isPaid) {
        this.expenseId   = expenseId;
        this.roommateId  = roommateId;
        this.shareAmount = shareAmount;
        this.isPaid      = isPaid;
    }

    public int     getId()          { return id;          }
    public int     getExpenseId()   { return expenseId;   }
    public int     getRoommateId()  { return roommateId;  }
    public double  getShareAmount() { return shareAmount; }
    public boolean isPaid()         { return isPaid;      }
    public void    setPaid(boolean paid) { this.isPaid = paid; }
}
