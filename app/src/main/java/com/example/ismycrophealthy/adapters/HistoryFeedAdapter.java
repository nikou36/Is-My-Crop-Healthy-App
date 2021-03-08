package com.example.ismycrophealthy.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ismycrophealthy.AppExecutors;
import com.example.ismycrophealthy.MainActivity;
import com.example.ismycrophealthy.R;
import com.example.ismycrophealthy.databases.PlantDatabase;
import com.example.ismycrophealthy.entities.Plant;
import com.example.ismycrophealthy.models.PlantData;

import java.util.ArrayList;
import java.util.List;

public class HistoryFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Plant> data;
    private PlantDatabase db;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View layout;
        public ImageView plantImage;
        public TextView dateText;
        public TextView diagnosisText;
        public ImageButton deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            plantImage = (ImageView) itemView.findViewById(R.id.plantImage);
            dateText = (TextView) itemView.findViewById(R.id.dateText);
            diagnosisText = (TextView) itemView.findViewById(R.id.diagnosisText);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
            /*
            When deleted, the item is removed from the AdapterList as well as the database
             */
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePlant(data.get(getAdapterPosition()));
                    data.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }

    public HistoryFeedAdapter(List<Plant> list,Context context){
        data = list;
        this.context = context;
        db = PlantDatabase.getInstance(context);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.history_feed_item,parent,false);
        HistoryFeedAdapter.ViewHolder vh = new HistoryFeedAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Plant plant = data.get(position);
        HistoryFeedAdapter.ViewHolder vh = (HistoryFeedAdapter.ViewHolder) holder;
        vh.diagnosisText.setText(plant.getDiagnosis());
        vh.dateText.setText(plant.getDate());
        Bitmap bitmap = BitmapFactory.decodeByteArray(plant.getImage(),0,plant.getImage().length);
        vh.plantImage.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    /*
    Delete the plant's data from the local DB
     */

    private void deletePlant(Plant plant) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.plantDao().delete(plant);
                //data.remove(plant);
                //notifyDataSetChanged();
                ((MainActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
    }


}
