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

public class ChoreRotationHelper {
    private final RoomEaseDatabase db;

    public ChoreRotationHelper(RoomEaseDatabase db) {
        this.db = db;
    }

    public void completeAndRotate(Chore chore) {
        String today = getTodayString();

        ChoreHistory record = new ChoreHistory(
            chore.getId(),
            chore.getAssignedToId(),
            today,
            chore.getTitle()
        );
        db.choreHistoryDao().insert(record);

        if (chore.isRotationEnabled()) {
            rotateToNextRoommate(chore);
        }

        chore.setDueDate(calculateNextDueDate(chore.getFrequency(), today));

        chore.setStatus("Pending");

        db.choreDao().update(chore);
    }

    private void rotateToNextRoommate(Chore chore) {
        List<Roommate> everyone = db.roommateDao().getAllRoommates();

        if (everyone.size() < 2) return;

        int currentIndex = -1;
        for (int i = 0; i < everyone.size(); i++) {
            if (everyone.get(i).getId() == chore.getAssignedToId()) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            chore.setAssignedToId(everyone.get(0).getId());
            return;
        }

        int nextIndex = (currentIndex + 1) % everyone.size();
        chore.setAssignedToId(everyone.get(nextIndex).getId());
    }

    private String calculateNextDueDate(String frequency, String fromDate) {
        if ("One-time".equals(frequency)) {
            return fromDate;
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
            return fromDate;
        }
    }

    private String getTodayString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
