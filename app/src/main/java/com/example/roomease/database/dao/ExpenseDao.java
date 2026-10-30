package com.example.roomease.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.roomease.database.entities.Expense;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    long insert(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    List<Expense> getAllExpenses();

    @Query("SELECT COUNT(*) FROM expenses")
    int getExpenseCount();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses")
    double getTotalExpenseAmount();
}
