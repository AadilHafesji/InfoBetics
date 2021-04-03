package com.example.infobetics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreenActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    TextView mNeedToRegister, mForgotPassword;
    Button mLoginButton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        mEmail = findViewById(R.id.emailLoginText);
        mLoginButton = findViewById(R.id.loginButton);
        mNeedToRegister = findViewById(R.id.needToRegisterTextView);
        mPassword = findViewById(R.id.passwordLoginText);
        mForgotPassword = findViewById(R.id.forgotPasswordTextView);

        firebaseAuth = FirebaseAuth.getInstance();

        //Login Button
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                //Email is empty
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is needed!");
                    return;
                }

                //Password is empty
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is needed!");
                    return;
                }

                //Password is less than 8 characters
                if (password.length() < 8) {
                    mPassword.setError("Password must be more than 8 characters!");
                    return;
                }

                //Authenticating the User
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //If Login is Successful
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginScreenActivity.this, "Login is Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                        } else {
                            Toast.makeText(LoginScreenActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //Forgot Password
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetEmail = new EditText(v.getContext());
                final AlertDialog.Builder resetPassword = new AlertDialog.Builder(v.getContext());
                resetPassword.setTitle("Reset Password?");
                resetPassword.setMessage("Enter your Email Address");
                resetPassword.setView(resetEmail);

                resetPassword.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Extract the Email and send Reset Link
                        String email = resetEmail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginScreenActivity.this, "Reset Link Sent to your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginScreenActivity.this, "Error! Reset Link Could Not Be Sent " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                resetPassword.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Close Dialog
                    }
                });

                //Dialog
                resetPassword.create().show();
            }
        });

        //Need to Register
        mNeedToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationScreenActivity.class));
            }
        });
    }
}
