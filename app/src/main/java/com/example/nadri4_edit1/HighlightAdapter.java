package com.example.nadri4_edit1;

import android.content.Context;
import android.net.Uri;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HighlightAdapter extends RecyclerView.Adapter<HighlightAdapter.ViewHolder> {
    //private static final String TAG = "SliderAdapter";
    private Context context;
    //private String[] Items = new String[5];
    private ArrayList<JSONObject> Items = new ArrayList<JSONObject>();
    //int[] images;
    //ArrayList<String> Items = new ArrayList<String>();
    //private List<String> Items;

    public HighlightAdapter(Context context, ArrayList<JSONObject> Items) {
        this.context = context;
        //this.highlightImage = highlightImage;
        this.Items = Items;
    }

    @NonNull
    @Override
    public HighlightAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highlight_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.pagerText.setImageResource(Integer.parseInt(Items.get(position)));
        //holder.pagerText.setText(Items.get(position));
        holder.bindSliderImage(Items.get(position));
    }

    @Override
    public int getItemCount() {
        //return highlightImage.length;
        Log.d("sizeis: ", String.valueOf(Items.size()));
        return Items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView mImageView;
        //ImageView imageView;
        ImageView pagerText;
        //private

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pagerText = itemView.findViewById(R.id.highlight_item);
            //imageView = itemView.findViewById(R.id.highlight_item);
            //mImageView = itemView.findViewById(R.id.highlight_item);
        }

        public void bindSliderImage(JSONObject image){
            try {
                Uri imageURI = Uri.parse(image.getString("uri"));
                Glide.with(itemView)    //with()안에 context말고 itemView 써줘야 함..ㅠㅠ ViewHolder 안에서 쓸 경우엔 ㅠㅠ..
                        .load(imageURI)
                        .into(pagerText);
            } catch (Exception e){
                Log.d("Error", e.getMessage());
            }
        }
    }

}
