package com.example.infobetics;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BloodGlucoseAndCarbohydrateAndInsulinActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "TAG";

    private FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    private CollectionReference bgCarbRef = fdb.collection("BGAndCarbohydrate");
    private BGAndCarbAdapter bgAndCarbAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    String userID;
    String typeOfInsulin;
    Double glucoseAmount;
    Integer carbohydrateAmount;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    EditText mBGEditText, mCarbohydrateEditText;
    TextView mInsulinAmount;
    Button mShowInsulinButton, mSaveInsulinButton, mCancelInsulinButton, mViewAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_glucose_and_carbohydrate_and_insulin);

        mBGEditText = findViewById(R.id.bgEditText);
        mCarbohydrateEditText = findViewById(R.id.carbohydrateEditText);
        mInsulinAmount = findViewById(R.id.insulinAmount);

        mShowInsulinButton = findViewById(R.id.showInsulinButton);
        mSaveInsulinButton = findViewById(R.id.saveInsulinButton);
        mCancelInsulinButton = findViewById(R.id.cancelInsulinButton);
        mViewAllButton = findViewById(R.id.viewAllButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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

        //Button to show how much insulin to inject
        mShowInsulinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If nothing typed in
                if (mBGEditText.getText().toString().length() == 0) {
                    mBGEditText.setText("0");
                }

                if (mCarbohydrateEditText.getText().toString().length() == 0) {
                    mCarbohydrateEditText.setText("0");
                }

                //Get Glucose Amount and Carbohydrate Amount from Edit Text
                glucoseAmount = Double.parseDouble(mBGEditText.getText().toString());
                carbohydrateAmount = Integer.parseInt(mCarbohydrateEditText.getText().toString());

                //Calculation for Insulin
                double enteredCarbohydrate = carbohydrateAmount;
                int unitRatio = 10;
                long insulinCalculated = Math.round(enteredCarbohydrate / unitRatio);

                //If Glucose Amount Less Than 3.9
                if (glucoseAmount <= 3.9) {
                    mInsulinAmount.setText("Please eat something sugary quickly! No Units to be Injected.");
                } else if (glucoseAmount >= 4.0 && glucoseAmount <= 8.0) {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated));
                } else if (glucoseAmount >= 8.1 && glucoseAmount <= 10.0) {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated + 1));
                } else if (glucoseAmount >= 10.1 && glucoseAmount <= 14.0) {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated + 2));
                } else if (glucoseAmount >= 14.1 && glucoseAmount <= 18.0) {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated + 3));
                } else if (glucoseAmount >= 18.1 && glucoseAmount <= 20.0) {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated + 4));
                } else {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated + 5));
                }
            }
        });

        mSaveInsulinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call this method
                saveAllData();
            }
        });

        mViewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivityIntent = new Intent(getBaseContext(), ViewBGAndCarbActivity.class);
                newActivityIntent.putExtra("userID", userID);
                startActivity(newActivityIntent);
            }
        });

        mCancelInsulinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Spinner
        CollectionReference collectionReference = firebaseFirestore.collection("InsulinType");
        final Spinner insulinSpinner = (Spinner) findViewById(R.id.insulinSpinner);
        final List<String> insulinTypes = new ArrayList<>();
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, insulinTypes);

        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        insulinSpinner.setAdapter(stringArrayAdapter);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("insulinOne") != "") {
                            typeOfInsulin = document.getString("insulinOne");
                            insulinTypes.add(typeOfInsulin);
                        }

                        if (document.getString("insulinTwo") != "") {
                            typeOfInsulin = document.getString("insulinTwo");
                            insulinTypes.add(typeOfInsulin);
                        }

                        if (document.getString("insulinThree") != "") {
                            typeOfInsulin = document.getString("insulinThree");
                            insulinTypes.add(typeOfInsulin);
                        }

                        if (document.getString("insulinFour") != "") {
                            typeOfInsulin = document.getString("insulinFour");
                            insulinTypes.add(typeOfInsulin);
                        }

                        if (document.getString("insulinFive") != "") {
                            typeOfInsulin = document.getString("insulinFive");
                            insulinTypes.add(typeOfInsulin);
                        }
                    }
                    stringArrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void saveAllData() {
        double saveGlucoseAmount = Double.parseDouble(mBGEditText.getText().toString().trim());
        int saveCarbohydrateAmount = Integer.parseInt(mCarbohydrateEditText.getText().toString().trim());
        int saveInsulinAmount = Integer.parseInt(mInsulinAmount.getText().toString().trim());


        userID = firebaseAuth.getCurrentUser().getUid();
        CollectionReference dataRef = firebaseFirestore.getInstance().collection("BGAndCarbohydrate");
        BGAndCarbData bgAndCarbData = new BGAndCarbData(userID, saveGlucoseAmount, saveCarbohydrateAmount, saveInsulinAmount);
        dataRef.add(bgAndCarbData);

        Toast.makeText(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, "Data Saved Successfully!", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, HomeScreenActivity.class));
        }

        if (menuItem.getItemId() == R.id.profile) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, ProfileActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeOneDiabetes) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, WhatIsTypeOneActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeTwoDiabetes) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, WhatIsTypeTwoActivity.class));
        }

        if (menuItem.getItemId() == R.id.difference) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, WhatIsTheDifferenceActivity.class));
        }

        if (menuItem.getItemId() == R.id.theCauses) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, WhatAreTheCausesActivity.class));
        }

        if (menuItem.getItemId() == R.id.theSymptoms) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, WhatAreTheSymptomsActivity.class));
        }

        if (menuItem.getItemId() == R.id.theComplications) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, WhatAreTheComplicationsActivity.class));
        }

        if (menuItem.getItemId() == R.id.manage) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, HowToManageAndTreatActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseAndCarbohydrates) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, BloodGlucoseAndCarbohydrateAndInsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseChart) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, BloodGlucoseChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.carbohydrateChart) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, CarbohydrateChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.reminders) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, ReminderActivity.class));
        }

        if (menuItem.getItemId() == R.id.insulin) {
            startActivity(new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, InsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.logout) {
            //Logout User
            firebaseAuth.signOut();
            Intent logoutIntent = new Intent(BloodGlucoseAndCarbohydrateAndInsulinActivity.this, LoginScreenActivity.class);
            startActivity(logoutIntent);
            finish();
        }

        return true;
    }
}
