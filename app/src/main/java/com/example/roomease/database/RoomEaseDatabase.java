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

/**
 * RoomEaseDatabase — the main database class.
 *
 * Think of this like the front desk of a library:
 * - The @Database annotation tells Room which "tables" (entities) we have
 * - The abstract methods give us the "librarians" (DAOs) to fetch/save data
 * - The Singleton pattern makes sure there's only ever ONE copy of the database
 */
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

    // The single shared instance — only one database open at a time
    private static RoomEaseDatabase instance;

    // --- DAOs (one per entity table) ---
    public abstract RoommateDao    roommateDao();
    public abstract ChoreDao       choreDao();
    public abstract ChoreHistoryDao choreHistoryDao();
    public abstract ExpenseDao     expenseDao();
    public abstract ExpenseSplitDao expenseSplitDao();

    /**
     * Get the database instance. Creates it the first time, reuses it after.
     * synchronized = thread-safe (only one thread opens the DB at a time).
     */
    public static synchronized RoomEaseDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RoomEaseDatabase.class,
                    "roomease_db"
                )
                // fallbackToDestructiveMigration: if the schema changes,
                // just wipe and recreate (fine for learning / development)
                .fallbackToDestructiveMigration()
                // allowMainThreadQueries: normally you'd use background threads,
                // but for a beginner app this keeps things simple and readable
                .allowMainThreadQueries()
                .build();
        }
        return instance;
    }
}
