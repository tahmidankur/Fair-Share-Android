package com.example.roomease.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ChoreHistory — a record of every time a chore was completed.
 * Think of it like a log: "Ahmed completed 'Dishes' on 2024-03-15".
 */
@Entity(tableName = "chore_history")
public class ChoreHistory {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int    choreId;       // which chore was completed
    public int    completedById; // which roommate completed it
    public String completedDate; // "yyyy-MM-dd"
    public String choreTitle;    // stored here so history still makes sense if chore is deleted

    public ChoreHistory(int choreId, int completedById, String completedDate, String choreTitle) {
        this.choreId       = choreId;
        this.completedById = completedById;
        this.completedDate = completedDate;
        this.choreTitle    = choreTitle;
    }

    public int    getId()            { return id;            }
    public int    getChoreId()       { return choreId;       }
    public int    getCompletedById() { return completedById; }
    public String getCompletedDate() { return completedDate; }
    public String getChoreTitle()    { return choreTitle;    }
}
