package com.example.roomease.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Roommate — represents one person living in the house.
 * The @Entity annotation tells Room to create a table called "roommates" for this class.
 */
@Entity(tableName = "roommates")
public class Roommate {

    // autoGenerate = true means Room will assign a unique ID automatically
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String email;  // optional — can be null
    public String phone;  // optional — can be null

    // Constructor — called when we create a new Roommate object
    public Roommate(String name, String email, String phone) {
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    // --- Getters (used by adapters and helpers) ---
    public int    getId()    { return id;    }
    public String getName()  { return name;  }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
