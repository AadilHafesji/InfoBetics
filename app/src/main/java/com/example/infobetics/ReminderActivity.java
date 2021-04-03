package com.example.infobetics;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, NavigationView.OnNavigationItemSelectedListener {
    Button mOpenAndSaveReminderButton, mCancelReminderButton;

    Switch mRecurringSwitch;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        createAlarmChannel();

        mRecurringSwitch = findViewById(R.id.recurringSwitch);

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

        mOpenAndSaveReminderButton = findViewById(R.id.openAndSaveReminderButton);
        mCancelReminderButton = findViewById(R.id.cancelReminderButton);

        //Open and Save Button
        mOpenAndSaveReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), "Time Picker");
            }
        });

        //Cancel Button
        mCancelReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onTimeSet(TimePicker viewTime, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        startAlarm(calendar);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startAlarm(final Calendar calendar) {
        Intent alarmIntent = new Intent(this, AlertReceiver.class);
        final PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(ReminderActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //If time in the past
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        //Start Notification
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingAlarmIntent);
        Toast.makeText(ReminderActivity.this, "Reminder Set!", Toast.LENGTH_SHORT).show();

        //Setting Recurring Alarm with Switch Button
        mRecurringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //If the switch is ON
                if (isChecked) {
                    //Turn alarm on at specified time every day
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY ,pendingAlarmIntent);
                    Toast.makeText(ReminderActivity.this, "Reminder Set as Recurring!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createAlarmChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "InfoBetics Alarm!";
            String description = "Reminder Alarm for Insulin";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("alarmID", name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
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
            startActivity(new Intent(ReminderActivity.this, HomeScreenActivity.class));
        }

        if (menuItem.getItemId() == R.id.profile) {
            startActivity(new Intent(ReminderActivity.this, ProfileActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeOneDiabetes) {
            startActivity(new Intent(ReminderActivity.this, WhatIsTypeOneActivity.class));
        }

        if (menuItem.getItemId() == R.id.typeTwoDiabetes) {
            startActivity(new Intent(ReminderActivity.this, WhatIsTypeTwoActivity.class));
        }

        if (menuItem.getItemId() == R.id.difference) {
            startActivity(new Intent(ReminderActivity.this, WhatIsTheDifferenceActivity.class));
        }

        if (menuItem.getItemId() == R.id.theCauses) {
            startActivity(new Intent(ReminderActivity.this, WhatAreTheCausesActivity.class));
        }

        if (menuItem.getItemId() == R.id.theSymptoms) {
            startActivity(new Intent(ReminderActivity.this, WhatAreTheSymptomsActivity.class));
        }

        if (menuItem.getItemId() == R.id.theComplications) {
            startActivity(new Intent(ReminderActivity.this, WhatAreTheComplicationsActivity.class));
        }

        if (menuItem.getItemId() == R.id.manage) {
            startActivity(new Intent(ReminderActivity.this, HowToManageAndTreatActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseAndCarbohydrates) {
            startActivity(new Intent(ReminderActivity.this, BloodGlucoseAndCarbohydrateAndInsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.bloodGlucoseChart) {
            startActivity(new Intent(ReminderActivity.this, BloodGlucoseChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.carbohydrateChart) {
            startActivity(new Intent(ReminderActivity.this, CarbohydrateChartActivity.class));
        }

        if (menuItem.getItemId() == R.id.reminders) {
            startActivity(new Intent(ReminderActivity.this, ReminderActivity.class));
        }

        if (menuItem.getItemId() == R.id.insulin) {
            startActivity(new Intent(ReminderActivity.this, InsulinActivity.class));
        }

        if (menuItem.getItemId() == R.id.logout) {
            //Logout User
            firebaseAuth.signOut();
            Intent logoutIntent = new Intent(ReminderActivity.this, LoginScreenActivity.class);
            startActivity(logoutIntent);
            finish();
        }

        return true;
    }
}
