package com.example.roomease.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "roommates")
public class Roommate {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String email;  // optional — can be null
    public String phone;  // optional — can be null

    public Roommate(String name, String email, String phone) {
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    public int    getId()    { return id;    }
    public String getName()  { return name;  }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
