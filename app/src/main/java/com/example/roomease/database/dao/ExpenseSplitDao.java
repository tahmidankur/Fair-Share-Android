package com.example.roomease.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.roomease.database.entities.ExpenseSplit;
import java.util.List;

@Dao
public interface ExpenseSplitDao {
    @Insert
    void insert(ExpenseSplit split);

    @Query("SELECT * FROM expense_splits WHERE expenseId = :expenseId")
    List<ExpenseSplit> getSplitsForExpense(int expenseId);
}
