package com.example.roomease.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomease.R;
import com.example.roomease.database.RoomEaseDatabase;
import com.example.roomease.database.entities.Roommate;
import com.google.android.material.textfield.TextInputEditText;

public class AddRoommateActivity extends AppCompatActivity {
    private TextInputEditText etName, etEmail, etPhone;
    private RoomEaseDatabase  db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_roommate);

        db = RoomEaseDatabase.getInstance(this);

        etName  = findViewById(R.id.et_roommate_name);
        etEmail = findViewById(R.id.et_roommate_email);
        etPhone = findViewById(R.id.et_roommate_phone);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_save_roommate).setOnClickListener(v -> saveRoommate());
    }

    private void saveRoommate() {
        String name  = etName.getText()  != null ? etName.getText().toString().trim()  : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim()  : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim()  : "";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        Roommate r = new Roommate(name,
                email.isEmpty()  ? null : email,
                phone.isEmpty()  ? null : phone);
        db.roommateDao().insert(r);

        Toast.makeText(this, name + " added!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
