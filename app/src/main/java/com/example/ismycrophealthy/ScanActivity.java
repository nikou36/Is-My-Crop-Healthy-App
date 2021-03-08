package com.example.ismycrophealthy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ismycrophealthy.databases.PlantDatabase;
import com.example.ismycrophealthy.entities.Plant;
import com.example.ismycrophealthy.ml.LiteModelDiseaseClassification1;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.time.LocalDateTime.now;

public class ScanActivity extends AppCompatActivity {
    private ImageView selectedImage;
    private ImageButton cameraButton;
    private ImageButton galleryButton;
    private Button scanButton;
    private boolean photoIsChosen;
    private ArrayList<String> categories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initCategoriesList();
        photoIsChosen = false;
        this.selectedImage = findViewById(R.id.selectedImageView);
        this.cameraButton = findViewById(R.id.cameraButton);
        this.galleryButton = findViewById(R.id.imagePickerButton);
        this.scanButton = findViewById(R.id.scanButton);
        initButtons();


    }
    /*
    Set up onClickListeners for buttons
     */

    private void initButtons(){
        /*
        Choose image from gallery
         */
        this.galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);

            }
        });
        /*
        Take an image with camera
         */

        this.cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);//zero can be replaced with any ac
            }
        });
        /*
        If not photo is chosen(either from gallery or camera) then remind the user to chose a photo via Toast. Else,
        Proccess the photo with the machine learning model
         */

        this.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoIsChosen){
                    BitmapDrawable drawable = (BitmapDrawable) selectedImage.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    callModel(byteArray);
                }else{
                    Toast pickImgToast = new Toast(v.getContext());
                    pickImgToast.makeText(v.getContext(),"Please pick an image before scanning",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /*
    Calls the neural network, categorizes the photo and saves the results into the local DB.
     */

    private void callModel(byte[] bytes){
        BitmapDrawable drawable = (BitmapDrawable) selectedImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        try {
            LiteModelDiseaseClassification1 model = LiteModelDiseaseClassification1.newInstance(this);

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(bitmap);

            // Runs model inference and gets result.
            LiteModelDiseaseClassification1.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();
            Log.i("Probability Size",probability.size()+"");
            // Releases model resources if no longer used.
            model.close();
            float max = -1;
            int index = -1;
            for(int i = 0; i < probability.size();i++){
                if(probability.get(i).getScore() > max){
                    max = probability.get(i).getScore();
                    index = i;
                }
            }
            Log.i("Index",categories.get(index));
            Log.i("Score",max+"");

            //Prep for insertion into DB
            String diagnosis = categories.get(index);
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            java.util.Date dateNow = Calendar.getInstance().getTime();
            String today = dateFormat.format(dateNow);

            Plant plant = new Plant(today,diagnosis,bytes);
            //Insert into DB
            PlantDatabase db = PlantDatabase.getInstance(this);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    db.plantDao().insertAll(plant);
                }
            });

            Intent i = new Intent(ScanActivity.this,DiagnosisActivity.class);
            //Round the probability
            max = Math.round(max * 100);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            i.putExtra("diagnosis",diagnosis);
            i.putExtra("probability", max);
            i.putExtra("bitmap",byteArray);
            startActivity(i);

        } catch (IOException e) {
            Toast pickImgToast = new Toast(this);
            pickImgToast.makeText(this,"Error while scanning. Please Try again",Toast.LENGTH_SHORT).show();
        }
    }
    /*
    All possible classifications
     */

    private void initCategoriesList(){
        categories = new ArrayList<>();
        categories.add("A Healthy Tomato");
        categories.add("Tomato Septoria Leaf Spot");
        categories.add("Tomato Bacterial Spot");
        categories.add("Tomato Blight");
        categories.add("A Healthy Cabbage");
        categories.add("Tomato Spider Mite");
        categories.add("Tomato Leaf Mold");
        categories.add("Tomato Yellow Leaf Curl Virus");
        categories.add("Soy Frogeye Leaf Spot");
        categories.add("Soy Downy Mildew");
        categories.add("Maize Ravi Corn Rust");
        categories.add("A Healthy Maize");
        categories.add("Maize Grey Leaf Spot");
        categories.add("Maize Leath Necrosis");
        categories.add("A Healthy Soy");
        categories.add("Cabbage Black Rot");
    }
    /*
    Either start the gallery picker or camera depending on which option was chosen
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    this.photoIsChosen = true;
                    this.selectedImage.setImageBitmap(imageBitmap);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    this.photoIsChosen = true;
                    this.selectedImage.setImageURI(selectedImage);
                }
                break;
        }
    }

}