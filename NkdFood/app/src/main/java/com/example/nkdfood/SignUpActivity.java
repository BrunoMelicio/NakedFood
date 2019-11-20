package com.example.nkdfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    EditText emailId, passwordId;
    TextView tvSignIn;
    Button btnSignUp;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email);
        passwordId = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.login);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = passwordId.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter email");
                    emailId.requestFocus();
                }

                else if(pwd.isEmpty()){
                    passwordId.setError("Please enter password");
                    passwordId.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Error Signing up!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignUpActivity.this, "Something unexpected happened", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
        });
    }
}
