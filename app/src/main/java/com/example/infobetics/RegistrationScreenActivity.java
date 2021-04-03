package com.example.infobetics;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationScreenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "TAG";
    EditText mFirstName, mSurname, mEmail, mPhone, mDOB, mPassword;
    TextView mHaveAccount;
    Spinner mSpinner;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button mRegisterButton;
    DatePickerDialog datePickerDialog;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen);

        mEmail = findViewById(R.id.emailRegisterText);
        mFirstName = findViewById(R.id.firstNameText);
        mHaveAccount = findViewById(R.id.haveAccountTextView);
        mPassword = findViewById(R.id.passwordRegisterText);
        mPhone = findViewById(R.id.phoneText);
        mRegisterButton = findViewById(R.id.registerButton);
        mSpinner = findViewById(R.id.genderSpinner);
        mSurname = findViewById(R.id.surnameText);
        radioGroup = findViewById(R.id.radioGroup);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Checking if User is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
            finish();
        }

        //Calendar View for Date of Birth
        mDOB = findViewById(R.id.dobTextView);
        mDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(RegistrationScreenActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        mDOB.setText(mDay + " - " + (mMonth+1) + " - " + mYear);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        //Array for the spinner
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.text, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Uses the items
        mSpinner.setAdapter(arrayAdapter);
        //React to clicks
        mSpinner.setOnItemSelectedListener(this);

        //Register Button
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstName = mFirstName.getText().toString().trim();
                final String surname = mSurname.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                final String phoneNumber = mPhone.getText().toString().trim();
                final String dateOfBirth = mDOB.getText().toString().trim();
                final String gender = mSpinner.getSelectedItem().toString().trim();
                final String userType = radioButton.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(firstName)) {
                    mFirstName.setError("First Name is needed!");
                    return;
                }

                if (TextUtils.isEmpty(surname)) {
                    mSurname.setError("Surname is needed!");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is needed!");
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    mPhone.setError("Phone Number is needed!");
                    return;
                }

                if (TextUtils.isEmpty(dateOfBirth)) {
                    mDOB.setError("Date of Birth is needed!");
                    return;
                }

                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegistrationScreenActivity.this, "Please Select User Type", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is needed!");
                    return;
                }

                //Password is less than 8 characters
                if (password.length() < 8) {
                    mPassword.setError("Password must be more than 8 characters!");
                    return;
                }

                //Registering the user with Firebase
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //If Registration is Successful
                        if (task.isSuccessful()) {
                            //Send Verification Email Link
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegistrationScreenActivity.this, "Verification Email has been Sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email could not be sent " + e.getMessage());
                                }
                            });

                            Toast.makeText(RegistrationScreenActivity.this, "User Created Successfully!", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);

                            //Creating Data
                            Map<String, Object> user = new HashMap<>();
                            user.put("firstName", firstName);
                            user.put("surname", surname);
                            user.put("email", email);
                            user.put("phoneNumber", phoneNumber);
                            user.put("dateOfBirth", dateOfBirth);
                            user.put("gender", gender);
                            user.put("userType", userType);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegistrationScreenActivity.this, "User Created Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegistrationScreenActivity.this, "User Not Created!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                        } else {
                            Toast.makeText(RegistrationScreenActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //Need to Login
        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginScreenActivity.class));
            }
        });
    }

    //Method for radio buttons
    public void checkButton(View v) {
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        //Checks what radio button is selected
        radioButton = findViewById(radioButtonId);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
