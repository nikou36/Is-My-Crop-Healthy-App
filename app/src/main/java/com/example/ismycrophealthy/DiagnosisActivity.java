package com.example.ismycrophealthy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DiagnosisActivity extends AppCompatActivity {
    private TextView diagnosis;
    private TextView probability;
    private ImageView scannedImage;
    private Button scanAgainButton;
    private Button homeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);
        this.diagnosis = findViewById(R.id.diagnosisText);
        this.probability = findViewById(R.id.probabilityText);
        this.scannedImage = findViewById(R.id.scannedImage);
        this.scanAgainButton = findViewById(R.id.scanAgainButton);
        this.homeButton = findViewById(R.id.homeButton);
        initButtons();
        Bundle bundle = getIntent().getExtras();
        byte[] bytes = bundle.getByteArray("bitmap");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        this.scannedImage.setImageBitmap(bitmap);
        this.diagnosis.setText(bundle.getString("diagnosis"));
        this.probability.setText("With a probability of: " + bundle.getFloat("probability") +"%" );
        this.diagnosis.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_from_left));
        this.probability.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_from_left));
    }
    /*
    Set onClickListeners for Buttons
     */

    private void initButtons(){
        this.scanAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DiagnosisActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}