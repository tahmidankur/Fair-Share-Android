package com.example.roomease;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.roomease.fragments.BalanceFragment;
import com.example.roomease.fragments.ChoresFragment;
import com.example.roomease.fragments.ExpensesFragment;
import com.example.roomease.fragments.HomeFragment;
import com.example.roomease.fragments.RoommatesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity — the shell of the app.
 * It owns the BottomNavigationView and swaps the centre fragment
 * whenever the user taps a nav item.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Show Home fragment on first launch
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }

        // Swap fragment when user taps a bottom nav item
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if      (id == R.id.nav_home)      replaceFragment(new HomeFragment());
            else if (id == R.id.nav_chores)    replaceFragment(new ChoresFragment());
            else if (id == R.id.nav_expenses)  replaceFragment(new ExpensesFragment());
            else if (id == R.id.nav_roommates) replaceFragment(new RoommatesFragment());
            else if (id == R.id.nav_balance)   replaceFragment(new BalanceFragment());

            return true;
        });
    }

    /** Swaps the fragment shown in fragment_container. */
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
