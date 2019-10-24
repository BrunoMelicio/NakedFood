package com.example.dbbase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DatabaseHandler();
    }

    @Override
    public void onStart() {
        super.onStart();
        //dbHandler.createNewUser("asd@fgh.jk", "passwd");
    }
}
