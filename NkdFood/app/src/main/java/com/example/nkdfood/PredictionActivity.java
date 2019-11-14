package com.example.nkdfood;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class PredictionActivity extends AppCompatActivity {

    ModelAI model;
    Bitmap image;
    ImageView imgV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        imgV = findViewById(R.id.image_view);

        Bundle bundleExtras = getIntent().getExtras();
        if(bundleExtras != null){
            image = bundleExtras.getParcelable("image");
            imgV.setImageBitmap(image);
            model = new ModelAI(PredictionActivity.this, getApplicationContext(), image);
            model.runModelInference();

        }
        else{
            // Uri not inside the intent data.
        }
    }
}
