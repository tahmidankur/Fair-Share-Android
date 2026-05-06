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

    // All chores sorted by due date (soonest first)
    @Query("SELECT * FROM chores ORDER BY dueDate ASC")
    List<Chore> getAllChores();

    // Only chores that still need to be done
    @Query("SELECT * FROM chores WHERE status = 'Pending' ORDER BY dueDate ASC")
    List<Chore> getPendingChores();

    @Query("SELECT * FROM chores WHERE id = :id")
    Chore getChoreById(int id);

    // Count of pending chores (used on the home dashboard)
    @Query("SELECT COUNT(*) FROM chores WHERE status = 'Pending'")
    int getPendingChoreCount();
}
