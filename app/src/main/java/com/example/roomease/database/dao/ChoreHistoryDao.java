package com.example.roomease.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.roomease.database.entities.ChoreHistory;
import java.util.List;

@Dao
public interface ChoreHistoryDao {

    @Insert
    long insert(ChoreHistory choreHistory);

    // Full history, newest entries first
    @Query("SELECT * FROM chore_history ORDER BY completedDate DESC")
    List<ChoreHistory> getAllHistory();

    // History for a specific chore (used on the ChoreHistory screen)
    @Query("SELECT * FROM chore_history WHERE choreId = :choreId ORDER BY completedDate DESC")
    List<ChoreHistory> getHistoryForChore(int choreId);

    // Three most recent completions (used on the Home dashboard)
    @Query("SELECT * FROM chore_history ORDER BY completedDate DESC LIMIT 5")
    List<ChoreHistory> getRecentHistory();
}
