package com.example.nkdfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity {

    String[] ings = {"Ingredient 1","Ingredient 2","Ingredient 3","Ingredient X"};
    DatabaseReference foodDB = FirebaseDatabase.getInstance().getReference().child("foods");
    ArrayList<String> ing = new ArrayList<>();
    ArrayAdapter adapt =null;
    ListView listView = null;
    Button back,save;
    TextView fn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        fn = findViewById(R.id.fn);
        back = findViewById(R.id.backbtn);
        save = findViewById(R.id.savebtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IngredientsActivity.this, PredictionActivity.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IngredientsActivity.this, "SAVED!", Toast.LENGTH_SHORT).show();
            }
        });
        ing = getIntent().getStringArrayListExtra("ings");


        if(ing.size() > 0){
            adapt = new ArrayAdapter<String>(IngredientsActivity.this,
                    R.layout.activity_listview, ing);

            listView = findViewById(R.id.mobile_list);
            listView.setAdapter(adapt);
        }
        else{
            adapt = new ArrayAdapter<String>(IngredientsActivity.this,
                    R.layout.activity_listview, ings);
            listView = findViewById(R.id.mobile_list);
            listView.setAdapter(adapt);
            showToast("Ingredients null");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
