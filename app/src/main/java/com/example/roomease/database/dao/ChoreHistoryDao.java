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

    @Query("SELECT * FROM chore_history ORDER BY completedDate DESC")
    List<ChoreHistory> getAllHistory();

    @Query("SELECT * FROM chore_history WHERE choreId = :choreId ORDER BY completedDate DESC")
    List<ChoreHistory> getHistoryForChore(int choreId);

    @Query("SELECT * FROM chore_history ORDER BY completedDate DESC LIMIT 5")
    List<ChoreHistory> getRecentHistory();
}
