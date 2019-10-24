package com.example.dbbase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DatabaseHandler {

    private FirebaseAuth mAuth;

    public DatabaseHandler() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser checkSignedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser;
    }

    public void createNewUser(String email, String password) {
        if (email.equals("") || password.equals("")) return;
        mAuth.createUserWithEmailAndPassword(email, password);
    }

    public void signIn(String email, String password) {
        if (email.equals("") || password.equals("")) return;
        mAuth.signInWithEmailAndPassword(email, password);
    }
}

