package com.example.infobetics;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class InsulinActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditText mInsulinOne, mInsulinTwo, mInsulinThree, mInsulinFour, mInsulinFive;
    Button mSaveInsulinTypeButton, mCancelInsulinTypeButton;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    String userID;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulin);

        mInsulinOne = findViewById(R.id.insulinOne);
        mInsulinTwo = findViewById(R.id.insulinTwo);
        mInsulinThree = findViewById(R.id.insulinThree);
        mInsulinFour = findViewById(R.id.insulinFour);
        mInsulinFive = findViewById(R.id.insulinFive);

        mSaveInsulinTypeButton = findViewById(R.id.saveInsulinTypeButton);
        mCancelInsulinTypeButton = findViewById(R.id.cancelInsulinTypeButton);

        firebaseAuth = FirebaseAuth.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();
        final FirebaseUser fUser = firebaseAuth.getCurrentUser();

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

        //Retrieve user information
        DocumentReference documentReference = firebaseFirestore.collection("InsulinType").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String insulinOne = documentSnapshot.getString("insulinOne");
                String insulinTwo = documentSnapshot.getString("insulinTwo");
                String insulinThree = documentSnapshot.getString("insulinThree");
                String insulinFour = documentSnapshot.getString("insulinFour");
                String insulinFive = documentSnapshot.getString("insulinFive");

                mInsulinOne.setText(insulinOne);
                mInsulinTwo.setText(insulinTwo);
                mInsulinThree.setText(insulinThree);
                mInsulinFour.setText(insulinFour);
                mInsulinFive.setText(insulinFive);
            }
        });

        mSaveInsulinTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final String insulinOne = mInsulinOne.getText().toString().trim();
                final String insulinTwo = mInsulinTwo.getText().toString().trim();
                final String insulinThree = mInsulinThree.getText().toString().trim();
                final String insulinFour = mInsulinFour.getText().toString().trim();
                final String insulinFive = mInsulinFive.getText().toString().trim();

                DocumentReference documentReference = firebaseFirestore.collection("InsulinType").document(userID);
                //Creating Data
                Map<String, Object> user = new HashMap<>();
                user.put("insulinOne", insulinOne);
                user.put("insulinTwo", insulinTwo);
                user.put("insulinThree", insulinThree);
                user.put("insulinFour", insulinFour);
                user.put("insulinFive", insulinFive);

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override public void onSuccess(Void aVoid) {
                        Toast.makeText(InsulinActivity.this, "Successfully Saved Data!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mCancelInsulinTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
            startActivity(new Intent(InsulinActivity.this, HomeScreenActivity.class));
        }

        if (menuItem.getItemId() == R.id.profile) {
            startActivity(new Intent(InsulinActivity.this, ProfileActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeOneDiabetes) {
            startActivity(new Intent(InsulinActivity.this, WhatIsTypeOneActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeTwoDiabetes) {
            startActivity(new Intent(InsulinActivity.this, WhatIsTypeTwoActivity.class));
        }

        if (menuItem.getItemId() == R.id.difference) {
            startActivity(new Intent(InsulinActivity.this, WhatIsTheDifferenceActivity.class));
        }

        if (menuItem.getItemId() == R.id.theCauses) {
            startActivity(new Intent(InsulinActivity.this, WhatAreTheCausesActivity.class));
        }

        if (menuItem.getItemId() == R.id.theSymptoms) {
            startActivity(new Intent(InsulinActivity.this, WhatAreTheSymptomsActivity.class));
        }

        if (menuItem.getItemId() == R.id.theComplications) {
            startActivity(new Intent(InsulinActivity.this, WhatAreTheComplicationsActivity.class));
        }

        if (menuItem.getItemId() == R.id.manage) {
            startActivity(new Intent(InsulinActivity.this, HowToManageAndTreatActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseAndCarbohydrates) {
            startActivity(new Intent(InsulinActivity.this, BloodGlucoseAndCarbohydrateAndInsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseChart) {
            startActivity(new Intent(InsulinActivity.this, BloodGlucoseChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.carbohydrateChart) {
            startActivity(new Intent(InsulinActivity.this, CarbohydrateChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.reminders) {
            startActivity(new Intent(InsulinActivity.this, ReminderActivity.class));
        }

        if (menuItem.getItemId() == R.id.insulin) {
            startActivity(new Intent(InsulinActivity.this, InsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.logout) {
            //Logout User
            firebaseAuth.signOut();
            Intent logoutIntent = new Intent(InsulinActivity.this, LoginScreenActivity.class);
            startActivity(logoutIntent);
            finish();
        }

        return true;
    }
}
