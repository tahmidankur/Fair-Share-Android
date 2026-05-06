package com.example.roomease.helpers;

import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.Chore;
import com.example.roomease.database.entities.ChoreHistory;
import com.example.roomease.database.entities.Roommate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ChoreRotationHelper — handles what happens when a chore is marked as done.
 *
 * The flow is:
 * 1. Save a record in ChoreHistory (who did it, when)
 * 2. If rotation is on, find the next roommate in the list
 * 3. Calculate the next due date based on the chore's frequency
 * 4. Reset the chore status to Pending for the next person
 * 5. Save the updated chore
 */
public class ChoreRotationHelper {

    private final RoomEaseDatabase db;

    public ChoreRotationHelper(RoomEaseDatabase db) {
        this.db = db;
    }

    /**
     * Call this when the user taps "Mark Complete" on a chore.
     */
    public void completeAndRotate(Chore chore) {
        String today = getTodayString();

        // Step 1 — Write this completion to the history log
        ChoreHistory record = new ChoreHistory(
            chore.getId(),
            chore.getAssignedToId(),
            today,
            chore.getTitle()
        );
        db.choreHistoryDao().insert(record);

        // Step 2 — If rotation is enabled, move to the next roommate
        if (chore.isRotationEnabled()) {
            rotateToNextRoommate(chore);
        }

        // Step 3 — Calculate when the chore is next due
        chore.setDueDate(calculateNextDueDate(chore.getFrequency(), today));

        // Step 4 — Reset status so the next person sees it as pending
        chore.setStatus("Pending");

        // Step 5 — Persist the changes
        db.choreDao().update(chore);
    }

    /**
     * Finds the next roommate in alphabetical order and assigns the chore to them.
     * Wraps around to the first person after the last one.
     */
    private void rotateToNextRoommate(Chore chore) {
        List<Roommate> everyone = db.roommateDao().getAllRoommates();

        // Need at least 2 people to rotate between
        if (everyone.size() < 2) return;

        // Find where the current person sits in the list
        int currentIndex = -1;
        for (int i = 0; i < everyone.size(); i++) {
            if (everyone.get(i).getId() == chore.getAssignedToId()) {
                currentIndex = i;
                break;
            }
        }

        // If not found (edge case), start from the beginning
        if (currentIndex == -1) {
            chore.setAssignedToId(everyone.get(0).getId());
            return;
        }

        // % everyone.size() wraps index back to 0 when we pass the last person
        int nextIndex = (currentIndex + 1) % everyone.size();
        chore.setAssignedToId(everyone.get(nextIndex).getId());
    }

    /**
     * Figures out the next due date based on how often the chore repeats.
     * For "One-time" chores, the date stays the same (no next occurrence).
     */
    private String calculateNextDueDate(String frequency, String fromDate) {
        if ("One-time".equals(frequency)) {
            return fromDate; // no future due date
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(fromDate);
            Calendar cal   = Calendar.getInstance();
            cal.setTime(startDate);

            switch (frequency) {
                case "Daily":     cal.add(Calendar.DAY_OF_YEAR, 1);  break;
                case "Weekly":    cal.add(Calendar.WEEK_OF_YEAR, 1); break;
                case "Bi-weekly": cal.add(Calendar.WEEK_OF_YEAR, 2); break;
                case "Monthly":   cal.add(Calendar.MONTH, 1);        break;
                default:          return fromDate;
            }

            return sdf.format(cal.getTime());

        } catch (Exception e) {
            // If parsing fails for any reason, keep the original date
            return fromDate;
        }
    }

    private String getTodayString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
