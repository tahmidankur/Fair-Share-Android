package com.example.roomease.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomease.R;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.Expense;
import com.example.roomease.database.entities.ExpenseSplit;
import com.example.roomease.database.entities.Roommate;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * AddExpenseActivity — form to log a new shared expense.
 *
 * The "Split With" section dynamically creates one CheckBox per roommate.
 * When saved, we calculate each person's equal share and store it in
 * the expense_splits table.
 */
public class AddExpenseActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etAmount, etDate, etNotes;
    private Spinner           spinnerPaidBy, spinnerCategory;
    private LinearLayout      llSplitOptions;
    private RoomEaseDatabase  db;

    private List<Roommate>   roommateList = new ArrayList<>();
    private List<CheckBox>   checkBoxes   = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        db = RoomEaseDatabase.getInstance(this);

        etTitle        = findViewById(R.id.et_expense_title);
        etAmount       = findViewById(R.id.et_expense_amount);
        etDate         = findViewById(R.id.et_expense_date);
        etNotes        = findViewById(R.id.et_expense_notes);
        spinnerPaidBy  = findViewById(R.id.spinner_paid_by);
        spinnerCategory= findViewById(R.id.spinner_category);
        llSplitOptions = findViewById(R.id.ll_split_options);

        setupRoommateSpinner();
        setupCategorySpinner();
        setupDatePicker();
        buildSplitCheckboxes();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save_expense).setOnClickListener(v -> saveExpense());
    }

    private void setupRoommateSpinner() {
        roommateList = db.roommateDao().getAllRoommates();

        if (roommateList.isEmpty()) {
            Toast.makeText(this, "Add roommates first!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        List<String> names = new ArrayList<>();
        for (Roommate r : roommateList) names.add(r.getName());

        ArrayAdapter<String> a = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaidBy.setAdapter(a);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> a = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(a);
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this,
                (view, year, month, day) ->
                    etDate.setText(String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        etDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime()));
    }

    /**
     * Creates one CheckBox for every roommate so the user can choose who
     * shares this expense. Defaults all boxes to checked (everyone splits).
     */
    private void buildSplitCheckboxes() {
        checkBoxes.clear();
        llSplitOptions.removeAllViews();

        for (Roommate r : roommateList) {
            CheckBox cb = new CheckBox(this);
            cb.setText(r.getName());
            cb.setChecked(true); // default: split equally with everyone
            cb.setTag(r.getId()); // store roommate ID in the tag for later
            llSplitOptions.addView(cb);
            checkBoxes.add(cb);
        }
    }

    private void saveExpense() {
        String title    = etTitle.getText()  != null ? etTitle.getText().toString().trim()  : "";
        String amountStr= etAmount.getText() != null ? etAmount.getText().toString().trim()  : "";
        String date     = etDate.getText()   != null ? etDate.getText().toString().trim()    : "";
        String notes    = etNotes.getText()  != null ? etNotes.getText().toString().trim()   : "";
        String category = spinnerCategory.getSelectedItem().toString();

        // --- Basic validation ---
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required"); etTitle.requestFocus(); return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Amount is required"); etAmount.requestFocus(); return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Enter a valid number"); etAmount.requestFocus(); return;
        }
        if (amount <= 0) {
            etAmount.setError("Amount must be greater than 0"); return;
        }
        if (TextUtils.isEmpty(date)) {
            etDate.setError("Date is required"); return;
        }

        // --- Find out who is splitting ---
        List<Roommate> selectedRoommates = new ArrayList<>();
        for (CheckBox cb : checkBoxes) {
            if (cb.isChecked()) {
                int roommateId = (int) cb.getTag();
                for (Roommate r : roommateList) {
                    if (r.getId() == roommateId) { selectedRoommates.add(r); break; }
                }
            }
        }

        if (selectedRoommates.isEmpty()) {
            Toast.makeText(this, "Select at least one person to split with", Toast.LENGTH_SHORT).show();
            return;
        }

        int paidById = roommateList.get(spinnerPaidBy.getSelectedItemPosition()).getId();

        // --- Save the Expense ---
        Expense expense = new Expense(title, amount, paidById, category, date,
                notes.isEmpty() ? null : notes);
        long expenseId = db.expenseDao().insert(expense);

        // --- Calculate each person's equal share and save splits ---
        double sharePerPerson = amount / selectedRoommates.size();
        for (Roommate r : selectedRoommates) {
            ExpenseSplit split = new ExpenseSplit(
                (int) expenseId,
                r.getId(),
                Math.round(sharePerPerson * 100.0) / 100.0, // round to 2 decimal places
                r.getId() == paidById // the payer is already "paid"
            );
            db.expenseSplitDao().insert(split);
        }

        Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
