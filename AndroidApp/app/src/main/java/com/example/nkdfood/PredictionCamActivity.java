package com.example.nkdfood;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Collections;

public class PredictionCamActivity extends AppCompatActivity {

    private FirebaseModelInterpreter mInterpreter;

    DatabaseReference foodDB = FirebaseDatabase.getInstance().getReference();

    private static final String TAG = "PredictionCamActivity";
    private ImageView mImageView;
    private Button getIng;
    private Button backBtn;
    private Button predictBtn;
    private Bitmap mSelectedImage;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;
    /**
     * Name of the model file hosted with Firebase.
     */
    private static final String HOSTED_MODEL_NAME = "model3_10";
    private static final String LOCAL_MODEL_ASSET = "model5_10.tflite";
    /**
     * Name of the label file stored in Assets.
     */
    private static final String LABEL_PATH = "labels.txt";
    /**
     * Number of results to show in the UI.
     */
    private static final int RESULTS_TO_SHOW = 3;
    /**
     * Dimensions of inputs.
     */
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;
    /**
     * Labels corresponding to the output of the vision model.
     */
    private List<String> mLabelList;

    private final PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float>
                                o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });
    /* Preallocated buffers for storing image data. */
    private final int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

    /**
     * Data configuration of input & output data of model.
     */
    private FirebaseModelInputOutputOptions mDataOptions;

    String foodLabel = "";
    String foodLabel2 = "";
    String foodLabel3 = "";
    String selectedLabel = "";
    ArrayList<String> ing = new ArrayList<String>();
    String f;

    TextView foodN,foodN2,foodN3, more;
    RelativeLayout rl;
    int mor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_cam);

        mImageView = findViewById(R.id.image_view);

        getIng = findViewById(R.id.ingBtn);
        backBtn = findViewById(R.id.backBtn);
        predictBtn = findViewById(R.id.predictBtn);

        foodN = findViewById(R.id.foodName);
        foodN2 = findViewById(R.id.foodName2);
        foodN3 = findViewById(R.id.foodName3);
        more = findViewById(R.id.more);

        rl = findViewById(R.id.predBody);

        Bundle bundleExtras = getIntent().getExtras();
        if(bundleExtras != null){
            String path = bundleExtras.getString("image");
            mSelectedImage = BitmapFactory.decodeFile(path);
            mImageView.setImageBitmap(mSelectedImage);
        }
        else{

        }

        getIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ing = getIngredients(selectedLabel);

            }
        });
        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runModelInference();
                //f = (String) mCloudButton.getText();
                getIng.setVisibility(View.VISIBLE);
                more.setVisibility(View.VISIBLE);
                foodN.setBackgroundColor(Color.parseColor("#00FF14"));
                rl.setBackgroundColor(Color.parseColor("#1eac32"));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PredictionCamActivity.this, HomeActivity.class));
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mor == 0){
                    mor = 1;
                    more.setText("Less");
                    foodN2.setVisibility(View.VISIBLE);
                    foodN3.setVisibility(View.VISIBLE);
                }
                else{
                    mor = 0;
                    more.setText("More");
                    foodN2.setVisibility(View.GONE);
                    foodN3.setVisibility(View.GONE);
                }

            }
        });
        foodN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLabel = foodLabel;
                foodN.setBackgroundColor(Color.parseColor("#00FF14"));
                foodN2.setBackgroundColor(Color.parseColor("#1eac32"));
                foodN3.setBackgroundColor(Color.parseColor("#1eac32"));
            }
        });
        foodN2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLabel = foodLabel2;
                foodN2.setBackgroundColor(Color.parseColor("#00FF14"));
                foodN.setBackgroundColor(Color.parseColor("#1eac32"));
                foodN3.setBackgroundColor(Color.parseColor("#1eac32"));
            }
        });
        foodN3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLabel = foodLabel3;
                foodN3.setBackgroundColor(Color.parseColor("#00FF14"));
                foodN2.setBackgroundColor(Color.parseColor("#1eac32"));
                foodN.setBackgroundColor(Color.parseColor("#1eac32"));
            }
        });


        initCustomModel();

    }

    private ArrayList<String> getIngredients(String label){
        final ArrayList<String> ingre = new ArrayList<>();
        foodDB.child("food").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(label != "") {
                    for (DataSnapshot ds : dataSnapshot.child(label).getChildren()) {
                        String str = ds.getValue(String.class);
                        ingre.add(str);
                    }
                    Intent ingr = new Intent(PredictionCamActivity.this, IngredientsCamActivity.class);
                    ingr.putStringArrayListExtra("ings", ingre);
                    startActivity(ingr);

                }
                else{
                    showToast("asdasd");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return ingre;
    }

    private void initCustomModel() {
        mLabelList = loadLabelList(this);

        int[] inputDims = {DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE};
        int[] outputDims = {DIM_BATCH_SIZE, mLabelList.size()};
        try {
            mDataOptions =
                    new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.FLOAT32, inputDims)
                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, outputDims)
                            .build();
            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions
                    .Builder()
                    .requireWifi()
                    .build();
            FirebaseRemoteModel remoteModel = new FirebaseRemoteModel.Builder
                    (HOSTED_MODEL_NAME)
                    .enableModelUpdates(true)
                    .setInitialDownloadConditions(conditions)
                    .setUpdatesDownloadConditions(conditions)  // You could also specify
                    // different conditions
                    // for updates
                    .build();
            FirebaseLocalModel localModel =
                    new FirebaseLocalModel.Builder("asset")
                            .setAssetFilePath(LOCAL_MODEL_ASSET).build();
            FirebaseModelManager manager = FirebaseModelManager.getInstance();
            manager.registerRemoteModel(remoteModel);
            manager.registerLocalModel(localModel);
            FirebaseModelOptions modelOptions =
                    new FirebaseModelOptions.Builder()
                            .setRemoteModelName(HOSTED_MODEL_NAME)
                            .setLocalModelName("asset")
                            .build();
            mInterpreter = FirebaseModelInterpreter.getInstance(modelOptions);
        } catch (FirebaseMLException e) {
            showToast("Error while setting up the model");
            e.printStackTrace();
        }
    }

    private void runModelInference() {
        if (mInterpreter == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.");
            return;
        }
        // Create input data.
        ByteBuffer imgData = convertBitmapToByteBuffer(mSelectedImage, mSelectedImage.getWidth(),
                mSelectedImage.getHeight());

        try {
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder().add(imgData).build();
            // Here's where the magic happens!!
            mInterpreter
                    .run(inputs, mDataOptions)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            showToast("Error running model inference");
                        }
                    })
                    .continueWith(
                            new Continuation<FirebaseModelOutputs, List<String>>() {
                                @Override
                                public List<String> then(Task<FirebaseModelOutputs> task) {
                                    float[][] labelProbArray = task.getResult()
                                            .<float[][]>getOutput(0);
                                    List<String> topLabels = getTopLabels(labelProbArray);


                                    foodLabel = topLabels.get(0).split(":")[0];
                                    foodLabel = foodLabel.substring(0, 1).toUpperCase() + foodLabel.substring(1);
                                    selectedLabel = foodLabel;
                                    foodLabel2 = topLabels.get(1).split(":")[0];
                                    foodLabel2 = foodLabel2.substring(0, 1).toUpperCase() + foodLabel2.substring(1);
                                    foodLabel3 = topLabels.get(2).split(":")[0];
                                    foodLabel3 = foodLabel3.substring(0, 1).toUpperCase() + foodLabel3.substring(1);

                                    if (!foodLabel.equals("")){
                                        //mCloudButton.setText(foodLabel);
                                        foodN.setText(topLabels.get(0));
                                    }
                                    else{
                                        showToast("Label error");
                                    }
                                    if (!foodLabel2.equals("")){
                                        //mCloudButton.setText(foodLabel);
                                        foodN2.setText(topLabels.get(1));
                                    }
                                    if (!foodLabel3.equals("")){
                                        //mCloudButton.setText(foodLabel);
                                        foodN3.setText(topLabels.get(2));
                                    }


                                    /*mGraphicOverlay.clear();
                                    GraphicOverlay.Graphic labelGraphic = new LabelGraphic
                                            (mGraphicOverlay, topLabels);
                                    mGraphicOverlay.add(labelGraphic);
                                    */


                                    return topLabels;
                                }
                            });
        } catch (FirebaseMLException e) {
            e.printStackTrace();
            showToast("Error running model inference");
        }

    }



    /**
     * Gets the top labels in the results.
     */
    private synchronized List<String> getTopLabels(float[][] labelProbArray) {
        for (int i = 0; i < mLabelList.size(); ++i) {
            sortedLabels.add(
                    new AbstractMap.SimpleEntry<>(mLabelList.get(i), labelProbArray[0][i]));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }
        List<String> result = new ArrayList<>();
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            result.add(label.getKey() + ":" + round(label.getValue()*100, 2) + "%");
        }
        Collections.reverse(result);
        Log.d(TAG, "labels: " + result.toString());
        return result;
    }

    /**
     * Reads label list from Assets.
     */
    private List<String> loadLabelList(Activity activity) {
        List<String> labelList = new ArrayList<>();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(activity.getAssets().open
                             (LABEL_PATH)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read label list.", e);
        }
        return labelList;
    }

    /**
     * Writes Image data into a {@code ByteBuffer}.
     */
    private synchronized ByteBuffer convertBitmapToByteBuffer(
            Bitmap bitmap, int width, int height) {
        ByteBuffer imgData =
                ByteBuffer.allocateDirect(
                        DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE * 4);
        imgData.order(ByteOrder.nativeOrder());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y,
                true);
        imgData.rewind();
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight());
        // Convert the image to int points.
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                imgData.putFloat(((val >> 16) & 0xFF)/255.f);
                imgData.putFloat(((val >> 8) & 0xFF)/255.f);
                imgData.putFloat((val & 0xFF)/255.f);
            }
        }
        return imgData;
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


}


/*
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.google.firebase.ml.common.FirebaseMLException;

import java.io.IOException;

public class PredictionCamActivity extends AppCompatActivity {

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
            Uri imageUri = bundleExtras.getParcelable("image");
            try {
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgV.setImageBitmap(image);
                model = new ModelAI(PredictionCamActivity.this, getApplicationContext(), image);
                //model.runModelInference();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FirebaseMLException e) {
                e.printStackTrace();
            }


        }
        else{
            // Uri not inside the intent data.
        }
    }
}*/
