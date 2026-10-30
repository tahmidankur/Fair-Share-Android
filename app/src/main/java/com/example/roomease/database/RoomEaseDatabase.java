package com.example.roomease.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.roomease.database.dao.ChoreDao;
import com.example.roomease.database.dao.ChoreHistoryDao;
import com.example.roomease.database.dao.ExpenseDao;
import com.example.roomease.database.dao.ExpenseSplitDao;
import com.example.roomease.database.dao.RoommateDao;
import com.example.roomease.database.entities.Chore;
import com.example.roomease.database.entities.ChoreHistory;
import com.example.roomease.database.entities.Expense;
import com.example.roomease.database.entities.ExpenseSplit;
import com.example.roomease.database.entities.Roommate;

@Database(
    entities = {
        Roommate.class,
        Chore.class,
        ChoreHistory.class,
        Expense.class,
        ExpenseSplit.class
    },
    version = 1,
    exportSchema = false
)
public abstract class RoomEaseDatabase extends RoomDatabase {
    private static RoomEaseDatabase instance;

    public abstract RoommateDao    roommateDao();
    public abstract ChoreDao       choreDao();
    public abstract ChoreHistoryDao choreHistoryDao();
    public abstract ExpenseDao     expenseDao();
    public abstract ExpenseSplitDao expenseSplitDao();

    public static synchronized RoomEaseDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RoomEaseDatabase.class,
                    "roomease_db"
                )
                .fallbackToDestructiveMigration()
                // queries are tiny, so running them on the main thread is fine here
                .allowMainThreadQueries()
                .build();
        }
        return instance;
    }
}
