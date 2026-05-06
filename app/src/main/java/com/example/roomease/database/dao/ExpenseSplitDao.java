package com.example.roomease.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomease.database.entities.ExpenseSplit;
import java.util.List;

@Dao
public interface ExpenseSplitDao {

    @Insert
    void insert(ExpenseSplit split);

    @Update
    void update(ExpenseSplit split);

    // Get all splits for a given expense
    @Query("SELECT * FROM expense_splits WHERE expenseId = :expenseId")
    List<ExpenseSplit> getSplitsForExpense(int expenseId);

    // All splits across all expenses (used to calculate overall balances)
    @Query("SELECT * FROM expense_splits")
    List<ExpenseSplit> getAllSplits();
}
