package com.example.roomease.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Chore — one household task (dishes, vacuuming, etc.).
 * Tracks who it's assigned to, when it's due, and how often it repeats.
 */
@Entity(tableName = "chores")
public class Chore {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public int    assignedToId;   // ID of the Roommate who should do this
    public String dueDate;        // stored as "yyyy-MM-dd"
    public String frequency;      // "Daily", "Weekly", "Bi-weekly", "Monthly", "One-time"
    public String status;         // "Pending" or "Completed"
    public boolean rotationEnabled; // if true, auto-rotate to next roommate after done

    public Chore(String title, String description, int assignedToId,
                 String dueDate, String frequency, String status, boolean rotationEnabled) {
        this.title           = title;
        this.description     = description;
        this.assignedToId    = assignedToId;
        this.dueDate         = dueDate;
        this.frequency       = frequency;
        this.status          = status;
        this.rotationEnabled = rotationEnabled;
    }

    // Getters
    public int     getId()               { return id;               }
    public String  getTitle()            { return title;            }
    public String  getDescription()      { return description;      }
    public int     getAssignedToId()     { return assignedToId;     }
    public String  getDueDate()          { return dueDate;          }
    public String  getFrequency()        { return frequency;        }
    public String  getStatus()           { return status;           }
    public boolean isRotationEnabled()   { return rotationEnabled;  }

    // Setters — needed when updating after completion
    public void setAssignedToId(int id)  { this.assignedToId = id;  }
    public void setDueDate(String date)  { this.dueDate = date;      }
    public void setStatus(String status) { this.status = status;     }
}
