package com.example.infobetics;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class EditBGAndCarbActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "TAG";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String userID;
    Double glucoseAmount;
    Integer carbohydrateAmount;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    EditText mEditBGEditText, mEditCarbohydrateEditText;
    TextView mInsulinAmount;
    Button mShowInsulinButton, mSaveInsulinButton, mCancelInsulinButton;

    private FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    private DocumentReference editRef = fdb.collection("BGAndCarbohydrate").document();
    private CollectionReference editBGCarbRef = fdb.collection("BGAndCarbohydrate");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_b_g_and_carb);

        mEditBGEditText = findViewById(R.id.editBgEditText);
        mEditCarbohydrateEditText = findViewById(R.id.editCarbohydrateEditText);
        mInsulinAmount = findViewById(R.id.insulinAmount);

        mShowInsulinButton = findViewById(R.id.showInsulinButton);
        mSaveInsulinButton = findViewById(R.id.saveInsulinButton);
        mCancelInsulinButton = findViewById(R.id.cancelInsulinButton);

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

        //Retrieve Data
        String id = getIntent().getExtras().getString("documentID");
        final DocumentReference documentReference = firebaseFirestore.collection("BGAndCarbohydrate").document(id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Double glucoseAmount = documentSnapshot.getDouble("glucoseAmount");
                Long carbohydrateAmount = ((Number) documentSnapshot.get("carbohydrateAmount")).longValue();

                String stringGlucoseAmount = Double.toString(glucoseAmount);
                mEditBGEditText.setText(stringGlucoseAmount);

                String stringCarbohydrateAmount = Long.toString(carbohydrateAmount);
                mEditCarbohydrateEditText.setText(stringCarbohydrateAmount);
            }
        });

        //Button to show how much insulin to inject
        mShowInsulinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If nothing typed in
                if (mEditBGEditText.getText().toString().length() == 0) {
                    mEditBGEditText.setText("0");
                }

                if (mEditCarbohydrateEditText.getText().toString().length() == 0) {
                    mEditCarbohydrateEditText.setText("0");
                }

                //Get Glucose Amount and Carbohydrate Amount from Edit Text
                glucoseAmount = Double.parseDouble(mEditBGEditText.getText().toString());
                carbohydrateAmount = Integer.parseInt(mEditCarbohydrateEditText.getText().toString());

                //Calculation for Insulin
                double enteredCarbohydrate = carbohydrateAmount;
                int unitRatio = 10;
                long insulinCalculated = Math.round(enteredCarbohydrate / unitRatio);

                //If Glucose Amount Less Than 3.9
                if (glucoseAmount <= 3.9) {
                    mInsulinAmount.setText("Please eat something sugary quickly! No Units to be Injected.");
                } else if (glucoseAmount >= 4 && glucoseAmount <= 8) {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated));
                } else if (glucoseAmount >= 8.1 && glucoseAmount <= 10) {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated + 1));
                } else if (glucoseAmount >= 10.1 && glucoseAmount <= 14) {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated + 2));
                } else if (glucoseAmount >= 14.1 && glucoseAmount <= 18) {
                    mInsulinAmount.setText(String.valueOf(insulinCalculated + 3));
                } else if (glucoseAmount >= 18.1 && glucoseAmount <= 20) {
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
                updateAllData();
            }
        });

        mCancelInsulinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateAllData() {
        final double saveGlucoseAmount = Double.parseDouble(mEditBGEditText.getText().toString().trim());
        final int saveCarbohydrateAmount = Integer.parseInt(mEditCarbohydrateEditText.getText().toString().trim());
        final int saveInsulinAmount = Integer.parseInt(mInsulinAmount.getText().toString().trim());
        String id = getIntent().getExtras().getString("documentID");

        final DocumentReference documentReference = firebaseFirestore.collection("BGAndCarbohydrate").document(id);

        documentReference.update("glucoseAmount", saveGlucoseAmount);
        documentReference.update("carbohydrateAmount", saveCarbohydrateAmount);
        documentReference.update("insulinAmount", saveInsulinAmount);

        Toast.makeText(EditBGAndCarbActivity.this, "Data Updated Successfully!", Toast.LENGTH_SHORT).show();
        Intent changeActivity = new Intent(EditBGAndCarbActivity.this, BloodGlucoseAndCarbohydrateAndInsulinActivity.class);
        startActivity(changeActivity);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
