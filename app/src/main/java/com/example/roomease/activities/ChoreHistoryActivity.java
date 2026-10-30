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

public class ChoreHistoryActivity extends AppCompatActivity {
    public static final String EXTRA_CHORE_ID    = "chore_id";
    public static final String EXTRA_CHORE_TITLE = "chore_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_history);

        int choreId     = getIntent().getIntExtra(EXTRA_CHORE_ID, -1);
        String choreTitle = getIntent().getStringExtra(EXTRA_CHORE_TITLE);

        TextView tvTitle = findViewById(R.id.tv_history_title);
        if (choreTitle != null) tvTitle.setText(choreTitle + " — History");

        RecyclerView rv = findViewById(R.id.rv_history);
        rv.setLayoutManager(new LinearLayoutManager(this));

        RoomEaseDatabase db = RoomEaseDatabase.getInstance(this);

        List<ChoreHistory> history = (choreId != -1)
                ? db.choreHistoryDao().getHistoryForChore(choreId)
                : db.choreHistoryDao().getAllHistory();

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
