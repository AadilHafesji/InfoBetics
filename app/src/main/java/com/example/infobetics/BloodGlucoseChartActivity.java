package com.example.infobetics;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BloodGlucoseChartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    private CollectionReference bgCarbRef = (CollectionReference) fdb.collection("BGAndCarbohydrate");

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String userID;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    //Array List to store blood glucose
    List<DataPoint> dataPointList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_glucose_chart);

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

        Query query = bgCarbRef.orderBy("currentDateAndTime").whereEqualTo("userID", userID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int index = 0;

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Map data = documentSnapshot.getData();

                        Double bloodGlucoseAmount = (Double) data.get("glucoseAmount");
                        Timestamp timestamp = (Timestamp) documentSnapshot.getData().get("currentDateAndTime");

                        dataPointList.add(new DataPoint(timestamp.toDate(), bloodGlucoseAmount));
                        index++;
                    }

                    GraphView graphView = (GraphView) findViewById(R.id.graphBG);

                    LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>(dataPointList.toArray(new DataPoint[dataPointList.size()]));

                    graphView.addSeries(lineGraphSeries);

                    //Style the graph
                    lineGraphSeries.setColor(Color.GREEN);
                    lineGraphSeries.setDrawDataPoints(true);
                    lineGraphSeries.setDataPointsRadius(10);
                    lineGraphSeries.setThickness(8);

                    //Styling the lines to connect
                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(10);
                    paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));

                    graphView.getViewport().setScrollable(true); //Allow horizontal scrolling
                    graphView.getViewport().setScrollableY(true); //Allow vertical scrolling
                    graphView.getViewport().setScalable(true); //Allow horizontal zooming and scrolling
                    graphView.getViewport().setScalableY(true); //Allow vertical zooming and scrolling
                } else {
                    Toast.makeText(BloodGlucoseChartActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                }
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
            startActivity(new Intent(BloodGlucoseChartActivity.this, HomeScreenActivity.class));
        }

        if (menuItem.getItemId() == R.id.profile) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, ProfileActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeOneDiabetes) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, WhatIsTypeOneActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeTwoDiabetes) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, WhatIsTypeTwoActivity.class));
        }

        if (menuItem.getItemId() == R.id.difference) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, WhatIsTheDifferenceActivity.class));
        }

        if (menuItem.getItemId() == R.id.theCauses) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, WhatAreTheCausesActivity.class));
        }

        if (menuItem.getItemId() == R.id.theSymptoms) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, WhatAreTheSymptomsActivity.class));
        }

        if (menuItem.getItemId() == R.id.theComplications) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, WhatAreTheComplicationsActivity.class));
        }

        if (menuItem.getItemId() == R.id.manage) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, HowToManageAndTreatActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseAndCarbohydrates) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, BloodGlucoseAndCarbohydrateAndInsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseChart) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, BloodGlucoseChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.carbohydrateChart) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, CarbohydrateChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.reminders) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, ReminderActivity.class));
        }

        if (menuItem.getItemId() == R.id.insulin) {
            startActivity(new Intent(BloodGlucoseChartActivity.this, InsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.logout) {
            //Logout User
            firebaseAuth.signOut();
            Intent logoutIntent = new Intent(BloodGlucoseChartActivity.this, LoginScreenActivity.class);
            startActivity(logoutIntent);
            finish();
        }

        return true;
    }
}
