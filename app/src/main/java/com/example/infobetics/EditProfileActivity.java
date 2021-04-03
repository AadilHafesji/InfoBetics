package com.example.infobetics;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "TAG";

    TextView mProfileTextView;
    EditText mFirstNameEditText, mSurnameEditText, mEmailEditText, mPhoneNumberEditText, mDateOfBirthEditText;
    Spinner mGenderEditText;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button mSaveProfileButton, mCancelProfileButton;
    DatePickerDialog datePickerDialog;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mProfileTextView = findViewById(R.id.editProfileTextView);

        mFirstNameEditText = findViewById(R.id.firstNameEditText);
        mSurnameEditText = findViewById(R.id.surnameEditText);
        mEmailEditText = findViewById(R.id.emailEditText);
        mPhoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        mDateOfBirthEditText = findViewById(R.id.dobEditText);
        mGenderEditText = findViewById(R.id.genderEditSpinner);
        radioGroup = findViewById(R.id.radioEditGroup);

        mSaveProfileButton = findViewById(R.id.saveProfileButton);
        mCancelProfileButton = findViewById(R.id.cancelProfileButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();
        final FirebaseUser fUser = firebaseAuth.getCurrentUser();

        //Calendar View for Date of Birth
        mDateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        mDateOfBirthEditText.setText(mDay + " - " + (mMonth+1) + " - " + mYear);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        //Array for the spinner
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.text, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Uses the items
        mGenderEditText.setAdapter(arrayAdapter);
        //React to clicks
        mGenderEditText.setOnItemSelectedListener(this);

        //Retrieve user information
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String firstName = documentSnapshot.getString("firstName");
                String surname = documentSnapshot.getString("surname");
                String email = documentSnapshot.getString("email");
                String phoneNumber = documentSnapshot.getString("phoneNumber");
                String dateOfBirth = documentSnapshot.getString("dateOfBirth");
                String gender = documentSnapshot.getString("gender");
                String userType = documentSnapshot.getString("userType");

                mFirstNameEditText.setText(firstName);
                mSurnameEditText.setText(surname);
                mEmailEditText.setText(email);
                mPhoneNumberEditText.setText(phoneNumber);
                mDateOfBirthEditText.setText(dateOfBirth);
                mGenderEditText.setSelected(Boolean.parseBoolean(gender));
                //radioButton.setText(userType);
            }
        });

        //Cancel update and Close Activity
        mCancelProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void editProfileData(View view) {
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        //Checks what radio button is selected
        radioButton = findViewById(radioButtonId);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);

        final String firstNameEdited = mFirstNameEditText.getText().toString().trim();
        final String surnameNameEdited = mSurnameEditText.getText().toString().trim();
        final String emailEdited = mEmailEditText.getText().toString().trim();
        final String phoneNumberEdited = mPhoneNumberEditText.getText().toString().trim();
        final String dateOfBirthEdited = mDateOfBirthEditText.getText().toString().trim();
        final String genderEdited = mGenderEditText.getSelectedItem().toString().trim();
        final String userTypeEdited = radioButton.getText().toString().trim();

        //Update the Data within the DB
        documentReference.update("firstName", firstNameEdited);
        documentReference.update("surname", surnameNameEdited);
        documentReference.update("email", emailEdited);
        documentReference.update("phoneNumber", phoneNumberEdited);
        documentReference.update("dateOfBirth", dateOfBirthEdited);
        documentReference.update("gender", genderEdited);
        documentReference.update("userType", userTypeEdited);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Toast.makeText(EditProfileActivity.this, "Successfully Updated User", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
