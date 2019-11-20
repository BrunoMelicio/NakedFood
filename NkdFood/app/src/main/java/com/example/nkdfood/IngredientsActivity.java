package com.example.nkdfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);


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
