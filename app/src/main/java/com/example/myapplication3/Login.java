package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
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
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText SignInEmail, SignInPassword;
    private TextView SignUpText;
    private Button SignInButton;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private boolean isEmailValid = false;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setTitle("Sign In Activity");
        mAuth = FirebaseAuth.getInstance();

        SignInEmail = findViewById(R.id.SignInEmail);
        SignInPassword = findViewById(R.id.SignInPassword);
        SignInButton = findViewById(R.id.SignInButton);
        SignUpText = findViewById(R.id.SignUpText);
        progressbar = findViewById(R.id.progressbar);

        SignUpText.setOnClickListener(this);
        SignInButton.setOnClickListener(this);

        // Add TextWatcher to the email field for live validation
        SignInEmail.addTextChangedListener(emailTextWatcher);

        // Set up the password visibility toggle
        SignInPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if touch is on the drawableEnd (visibility icon)
                if (event.getRawX() >= (SignInPassword.getRight() - SignInPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    // TextWatcher for live email validation
    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateEmailFormat(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    // Validate email format
    private void validateEmailFormat(String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailValid = true;
        } else {
            isEmailValid = false;
        }
    }

    // Toggle password visibility
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            SignInPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            SignInPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password, 0, R.drawable.ic_visibility_off, 0);
        } else {
            // Show password
            SignInPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            SignInPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password, 0, R.drawable.ic_visibility_off, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        SignInPassword.setSelection(SignInPassword.length()); // Move cursor to end
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SignInButton) {
            if (isEmailValid) {
                userLogin();
            } else {
                Toast.makeText(getApplicationContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.SignUpText) {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        }
    }

    private void userLogin() {
        String email = SignInEmail.getText().toString().trim();
        String password = SignInPassword.getText().toString().trim();

        if (email.isEmpty()) {
            SignInEmail.setError("Enter an email address");
            SignInEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            SignInPassword.setError("Enter a password");
            SignInPassword.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressbar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    // Check if the user's email is verified
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        // User's email is not verified
                        Toast.makeText(getApplicationContext(), "Please verify your email address before logging in", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
