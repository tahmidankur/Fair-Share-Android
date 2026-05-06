package com.example.roomease.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomease.R;
import com.example.roomease.adapters.ChoreHistoryAdapter;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.ChoreHistory;

import java.util.List;

/**
 * ChoreHistoryActivity — shows every time a specific chore was completed.
 * Launched from the "History" button on a chore card.
 *
 * Receives the chore ID via Intent extras.
 */
public class ChoreHistoryActivity extends AppCompatActivity {

    public static final String EXTRA_CHORE_ID    = "chore_id";
    public static final String EXTRA_CHORE_TITLE = "chore_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_history);

        int choreId     = getIntent().getIntExtra(EXTRA_CHORE_ID, -1);
        String choreTitle = getIntent().getStringExtra(EXTRA_CHORE_TITLE);

        // Update the header title with the chore name
        TextView tvTitle = findViewById(R.id.tv_history_title);
        if (choreTitle != null) tvTitle.setText(choreTitle + " — History");

        RecyclerView rv = findViewById(R.id.rv_history);
        rv.setLayoutManager(new LinearLayoutManager(this));

        RoomEaseDatabase db = RoomEaseDatabase.getInstance(this);

        // Load history for this specific chore
        List<ChoreHistory> history = (choreId != -1)
                ? db.choreHistoryDao().getHistoryForChore(choreId)
                : db.choreHistoryDao().getAllHistory();

        // Show or hide the empty-state message
        TextView tvEmpty = findViewById(R.id.tv_empty_history);
        if (history.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setAdapter(new ChoreHistoryAdapter(this, history));
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}
