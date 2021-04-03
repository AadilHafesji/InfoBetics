package com.example.infobetics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class WhatAreTheComplicationsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView mWhatAreTheComplications, mComplicationsInfo;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_are_the_complications);

        mWhatAreTheComplications = findViewById(R.id.whatAreTheComplicationsTextView);
        mComplicationsInfo = findViewById(R.id.complicationsInformationTextView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.drawerToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        //Opening and Closing the Drawer with Toggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //Closing the Drawer
        drawerLayout.closeDrawer(GravityCompat.START);

        if (menuItem.getItemId() == R.id.home) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, HomeScreenActivity.class));
        }

        if (menuItem.getItemId() == R.id.profile) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, ProfileActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeOneDiabetes) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, WhatIsTypeOneActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeTwoDiabetes) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, WhatIsTypeTwoActivity.class));
        }

        if (menuItem.getItemId() == R.id.difference) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, WhatIsTheDifferenceActivity.class));
        }

        if (menuItem.getItemId() == R.id.theCauses) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, WhatAreTheCausesActivity.class));
        }

        if (menuItem.getItemId() == R.id.theSymptoms) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, WhatAreTheSymptomsActivity.class));
        }

        if (menuItem.getItemId() == R.id.theComplications) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, WhatAreTheComplicationsActivity.class));
        }

        if (menuItem.getItemId() == R.id.manage) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, HowToManageAndTreatActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseAndCarbohydrates) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, BloodGlucoseAndCarbohydrateAndInsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseChart) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, BloodGlucoseChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.carbohydrateChart) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, CarbohydrateChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.reminders) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, ReminderActivity.class));
        }

        if (menuItem.getItemId() == R.id.insulin) {
            startActivity(new Intent(WhatAreTheComplicationsActivity.this, InsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.logout) {
            //Logout User
            firebaseAuth.signOut();
            Intent logoutIntent = new Intent(WhatAreTheComplicationsActivity.this, LoginScreenActivity.class);
            startActivity(logoutIntent);
            finish();
        }

        return true;
    }
}
