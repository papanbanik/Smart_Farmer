package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private EditText SignUpEmail, SignUpPassword; // Declare EditText variables
    private Button SignUpButton;
    private TextView SignInText;
    private FirebaseAuth mAuth;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.setTitle("Sign Up Activity");

        mAuth = FirebaseAuth.getInstance();
        progressbar = findViewById(R.id.progressbar);
        SignUpEmail = findViewById(R.id.SignUpEmail);
        SignUpPassword = findViewById(R.id.SignUpPassword);
        SignUpButton = findViewById(R.id.SignUpButton); // Initialize the button
        SignInText = findViewById(R.id.SignInText);
        SignInText.setOnClickListener(this);
        SignUpButton.setOnClickListener(this);

        // Add TextWatcher to the email field for live validation
        SignUpEmail.addTextChangedListener(emailTextWatcher);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SignUpButton) {
            userRegister();
        } else if (v.getId() == R.id.SignInText) {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        }
    }

    private void userRegister() {
        String email = SignUpEmail.getText().toString().trim();
        String password = SignUpPassword.getText().toString().trim();

        if (email.isEmpty()) {
            SignUpEmail.setError("Enter an email address");
            SignUpEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SignUpEmail.setError("Enter a valid email address");
            SignUpEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            SignUpPassword.setError("Enter a password");
            SignUpPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            SignUpPassword.setError("Minimum length should be 6");
            SignUpPassword.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        // Check if email already exists
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {

                    SignInMethodQueryResult result = task.getResult();
                    if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                        // User with this email already exists
                        Toast.makeText(SignUp.this, "User with this email already exists", Toast.LENGTH_SHORT).show();
                        progressbar.setVisibility(View.GONE);
                    } else {
                        // User with this email doesn't exist, proceed with registration
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressbar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    sendEmailVerification();

                                    Toast.makeText(getApplicationContext(), "Registration is successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(getApplicationContext(), "User is already registered", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            ((FirebaseUser) user).sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Email sent
                                Toast.makeText(getApplicationContext(), "Verification email sent", Toast.LENGTH_SHORT).show();
                            } else {
                                // Failed to send email
                                Toast.makeText(getApplicationContext(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // TextWatcher for live email validation
    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s != null && !s.toString().isEmpty()) { // Check if s is not null and not empty
                // Check email existence as the user types
                checkEmailExistence(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    // Check email existence
    private void checkEmailExistence(final String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                        // User with this email already exists
                        SignUpEmail.setError("User with this email already exists");
                    } else {
                        // User with this email doesn't exist
                        SignUpEmail.setError(null);
                    }
                }
            }
        });
    }
}
