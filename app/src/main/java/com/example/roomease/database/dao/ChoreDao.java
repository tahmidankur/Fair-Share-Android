package com.example.roomease.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomease.database.entities.Chore;
import java.util.List;

@Dao
public interface ChoreDao {
    @Insert
    long insert(Chore chore);

    @Update
    void update(Chore chore);

    @Delete
    void delete(Chore chore);

    @Query("SELECT * FROM chores ORDER BY dueDate ASC")
    List<Chore> getAllChores();

    @Query("SELECT COUNT(*) FROM chores WHERE status = 'Pending'")
    int getPendingChoreCount();
}
