package com.example.roomease.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.roomease.R;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.Chore;
import com.example.roomease.database.entities.Roommate;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * AddChoreActivity — form to add a new household chore.
 *
 * Features:
 * - Assign to a roommate (Spinner populated from DB)
 * - Pick a due date with a date picker dialog
 * - Choose frequency (Daily / Weekly / Bi-weekly / Monthly / One-time)
 * - Toggle auto-rotation (chore moves to next roommate after completion)
 */
public class AddChoreActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription, etDueDate;
    private Spinner           spinnerAssignTo, spinnerFrequency;
    private SwitchCompat      switchRotation;
    private RoomEaseDatabase  db;

    // We keep the full Roommate list so we can get the ID from the selection
    private List<Roommate>    roommateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chore);

        db = RoomEaseDatabase.getInstance(this);

        etTitle        = findViewById(R.id.et_chore_title);
        etDescription  = findViewById(R.id.et_chore_description);
        etDueDate      = findViewById(R.id.et_due_date);
        spinnerAssignTo= findViewById(R.id.spinner_assign_to);
        spinnerFrequency=findViewById(R.id.spinner_frequency);
        switchRotation = findViewById(R.id.switch_rotation);

        setupRoommateSpinner();
        setupFrequencySpinner();
        setupDatePicker();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save_chore).setOnClickListener(v -> saveChore());
    }

    /** Populates the Assign To spinner with roommates from the database. */
    private void setupRoommateSpinner() {
        roommateList = db.roommateDao().getAllRoommates();

        if (roommateList.isEmpty()) {
            Toast.makeText(this, "Add at least one roommate first!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        List<String> names = new ArrayList<>();
        for (Roommate r : roommateList) names.add(r.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignTo.setAdapter(adapter);
    }

    /** Populates the Frequency spinner from the string-array in strings.xml */
    private void setupFrequencySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(adapter);
        spinnerFrequency.setSelection(1); // default to Weekly
    }

    /** Opens a calendar dialog when the user taps the due date field. */
    private void setupDatePicker() {
        etDueDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this,
                (view, year, month, day) -> {
                    // Format as yyyy-MM-dd
                    String formatted = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, day);
                    etDueDate.setText(formatted);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Pre-fill with today's date
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
        etDueDate.setText(today);
    }

    private void saveChore() {
        String title       = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null
                           ? etDescription.getText().toString().trim() : "";
        String dueDate     = etDueDate.getText() != null ? etDueDate.getText().toString().trim() : "";
        String frequency   = spinnerFrequency.getSelectedItem().toString();
        boolean rotation   = switchRotation.isChecked();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dueDate)) {
            etDueDate.setError("Due date is required");
            return;
        }
        if (spinnerAssignTo.getSelectedItemPosition() < 0) return;

        // Get the selected roommate's actual database ID
        Roommate selected = roommateList.get(spinnerAssignTo.getSelectedItemPosition());

        Chore chore = new Chore(
            title,
            description.isEmpty() ? null : description,
            selected.getId(),
            dueDate,
            frequency,
            "Pending",
            rotation
        );
        db.choreDao().insert(chore);

        Toast.makeText(this, "Chore added!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
