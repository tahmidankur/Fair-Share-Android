package com.example.roomease.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.roomease.database.entities.Roommate;
import java.util.List;

@Dao
public interface RoommateDao {
    @Insert
    long insert(Roommate roommate);

    @Update
    void update(Roommate roommate);

    @Delete
    void delete(Roommate roommate);

    @Query("SELECT * FROM roommates ORDER BY name ASC")
    List<Roommate> getAllRoommates();

    @Query("SELECT * FROM roommates WHERE id = :id")
    Roommate getRoommateById(int id);

    @Query("SELECT COUNT(*) FROM roommates")
    int getRoommateCount();
}
