package com.example.nkdfood;

public class ModelAI{

}
/*
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ModelAI {

    private FirebaseModelInterpreter mInterpreter;
    private static final String TAG = "HomeActivity";
    private ImageView mImageView;
    private Bitmap mSelectedImage;
    private static final String HOSTED_MODEL_NAME = "nakedfood";
    private static final String LOCAL_MODEL_ASSET = "model.tflite";
    private static final String LABEL_PATH = "labels.txt";
    private static final int RESULTS_TO_SHOW = 3;
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;
    private List<String> mLabelList;
    private GraphicOverlay mGraphicOverlay;
    private FirebaseModelInputOutputOptions mDataOptions;
    private final int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
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

    private Activity activity;
    private Context context;

    public ModelAI(Activity activity, Context context, Bitmap image) throws FirebaseMLException {
        this.activity = activity;
        this.context = context;
        this.mSelectedImage = image;
        //initCustomModel();
        runInference();
    }
    private float[][][][] bitmapToInputArray() {
        // [START mlkit_bitmap_input]
        Bitmap bitmap = this.mSelectedImage;
        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

        int batchNum = 0;
        float[][][][] input = new float[1][224][224][3];
        for (int x = 0; x < 224; x++) {
            for (int y = 0; y < 224; y++) {
                int pixel = bitmap.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
            }
        }
        // [END mlkit_bitmap_input]

        return input;
    }


    private void showToast(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }

    private FirebaseCustomRemoteModel configureHostedModelSource() {
        // [START mlkit_cloud_model_source]
        FirebaseCustomRemoteModel remoteModel =
                new FirebaseCustomRemoteModel.Builder(HOSTED_MODEL_NAME).build();
        // [END mlkit_cloud_model_source]
        return remoteModel;
    }

    private void startModelDownloadTask(FirebaseCustomRemoteModel remoteModel) {
        // [START mlkit_model_download_task]
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Success.
                    }
                });
        // [END mlkit_model_download_task]
    }

    private FirebaseCustomLocalModel configureLocalModelSource() {
        // [START mlkit_local_model_source]
        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath(LOCAL_MODEL_ASSET)
                .build();
        // [END mlkit_local_model_source]
        return localModel;
    }

    private FirebaseModelInterpreter createInterpreter(FirebaseCustomLocalModel localModel) throws FirebaseMLException {
        // [START mlkit_create_interpreter]
        FirebaseModelInterpreter interpreter = null;
        try {
            FirebaseModelInterpreterOptions options =
                    new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
        } catch (FirebaseMLException e) {
            // ...
        }
        // [END mlkit_create_interpreter]

        return interpreter;
    }

    private FirebaseModelInterpreter createInterpreter2(FirebaseCustomRemoteModel remoteModel) throws FirebaseMLException {
        // [START mlkit_create_interpreter]
        FirebaseModelInterpreter interpreter = null;
        try {
            FirebaseModelInterpreterOptions options =
                    new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
        } catch (FirebaseMLException e) {
            // ...
        }
        // [END mlkit_create_interpreter]

        return interpreter;
    }

    private void checkModelDownloadStatus(
            final FirebaseCustomRemoteModel remoteModel,
            final FirebaseCustomLocalModel localModel) {
        // [START mlkit_check_download_status]


        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isDownloaded) {
                        FirebaseModelInterpreterOptions options;
                        if (isDownloaded) {
                            options = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
                        } else {
                            options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
                        }
                        try {
                            FirebaseModelInterpreter interpreter = FirebaseModelInterpreter.getInstance(options);
                            // ...
                        } catch (FirebaseMLException e) {
                            e.printStackTrace();
                        }
                    }
                });
        // [END mlkit_check_download_status]
    }

    private void addDownloadListener(
            FirebaseCustomRemoteModel remoteModel,
            FirebaseModelDownloadConditions conditions) {
        // [START mlkit_remote_model_download_listener]
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        // Download complete. Depending on your app, you could enable
                        // the ML feature, or switch from the local model to the remote
                        // model, etc.
                    }
                });
        // [END mlkit_remote_model_download_listener]
    }

    private FirebaseModelInputOutputOptions createInputOutputOptions() throws FirebaseMLException {
        // [START mlkit_create_io_options]
        FirebaseModelInputOutputOptions inputOutputOptions =
                new FirebaseModelInputOutputOptions.Builder()
                        .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 224, 224, 3})
                        .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 22})
                        .build();
        // [END mlkit_create_io_options]

        return inputOutputOptions;
    }


    private void runInference() throws FirebaseMLException {
        final FirebaseCustomLocalModel localModel = configureLocalModelSource();
        final FirebaseCustomRemoteModel remoteModel = configureHostedModelSource();

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showToast("Downloaded succesfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Failed to download");
                        e.printStackTrace();
                    }
                });

        FirebaseModelInterpreter firebaseInterpreter = createInterpreter2(remoteModel);

        if(firebaseInterpreter == null){
            showToast("Interpreter null!");
            firebaseInterpreter = createInterpreter(localModel);
            if(firebaseInterpreter==null){
                showToast("Error anyway");
            }
        }

        float[][][][] input = bitmapToInputArray();
        FirebaseModelInputOutputOptions inputOutputOptions = createInputOutputOptions();

        // [START mlkit_run_inference]
        FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                .add(input)  // add() as many input arrays as your model requires
                .build();
        firebaseInterpreter.run(inputs, inputOutputOptions)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseModelOutputs>() {
                            @Override
                            public void onSuccess(FirebaseModelOutputs result) {
                                // [START_EXCLUDE]
                                // [START mlkit_read_result]
                                float[][] output = result.getOutput(0);
                                float[] probabilities = output[0];
                                showToast("Success on inference!");
                                // [END mlkit_read_result]
                                // [END_EXCLUDE]
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                showToast("Error on inference");
                                e.printStackTrace();
                            }
                        });
        // [END mlkit_run_inference]
    }
}
*/