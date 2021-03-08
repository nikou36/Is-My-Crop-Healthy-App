package com.example.ismycrophealthy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ismycrophealthy.adapters.HistoryFeedAdapter;
import com.example.ismycrophealthy.databases.PlantDatabase;
import com.example.ismycrophealthy.entities.Plant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button scanButton;
    private TextView emptyText;
    private RecyclerView plantFeed;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private PlantDatabase db;
    private List<Plant> plantList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.db = PlantDatabase.getInstance(this);
        this.scanButton = findViewById(R.id.goToScanActivity);
        this.emptyText = findViewById(R.id.emptyText);
        this.plantFeed = findViewById(R.id.plantFeed);
        this.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,ScanActivity.class);
                startActivity(i);
            }
        });

    }
    /*
    Query the entire database to populate recyclerview
     */
    private void queryAll() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Plant> persons = db.plantDao().getAll();
                plantList  = persons;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initRecyclerView();
                    }
                });
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
        //Call query again in the event the DB was updated
        queryAll();
    }
    /*
    If the recyclerview is empty, display the hidden text view saying so. Otherwise, show the recyclerview
    and attach data.
     */

    public void initRecyclerView(){
        if(plantList.size() == 0){
            this.plantFeed.setVisibility(View.INVISIBLE);
            this.emptyText.setVisibility(View.VISIBLE);
        }else{
            this.mAdapter = new HistoryFeedAdapter(plantList,this);
            this.mLayoutManager = new LinearLayoutManager(this);
            plantFeed.setLayoutManager(mLayoutManager);
            plantFeed.setAdapter(mAdapter);
        }
    }
}