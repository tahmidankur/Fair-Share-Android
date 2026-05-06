package com.example.roomease.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomease.database.entities.Roommate;
import java.util.List;

/**
 * RoommateDao — DAO = Data Access Object.
 * This interface defines all the database operations for roommates.
 * Room auto-generates the actual implementation at compile time.
 */
@Dao
public interface RoommateDao {

    // Add a new roommate — returns the auto-generated ID
    @Insert
    long insert(Roommate roommate);

    // Update an existing roommate's info
    @Update
    void update(Roommate roommate);

    // Remove a roommate from the database
    @Delete
    void delete(Roommate roommate);

    // Get every roommate, sorted A-Z by name
    @Query("SELECT * FROM roommates ORDER BY name ASC")
    List<Roommate> getAllRoommates();

    // Find one specific roommate by their ID
    @Query("SELECT * FROM roommates WHERE id = :id")
    Roommate getRoommateById(int id);

    // How many roommates do we have? (used for the home dashboard stats)
    @Query("SELECT COUNT(*) FROM roommates")
    int getRoommateCount();
}
