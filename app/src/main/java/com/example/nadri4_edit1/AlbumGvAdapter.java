package com.example.nadri4_edit1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlbumGvAdapter extends BaseAdapter {

    Context context;
    private ArrayList<JSONObject> gList = new ArrayList<JSONObject>();

    //생성자
    public AlbumGvAdapter(Context c){
        context = c;
    }
    public AlbumGvAdapter(Context c, ArrayList<JSONObject> items){
        context = c;
        gList = items;
    }

    @Override
    public int getCount() {
        return gList.size();
    }

    @Override
    public Object getItem(int i) {
        return gList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addItem(Object item){
        gList.add((JSONObject) item);
    }

    public void setItem(ArrayList<JSONObject> items) { gList = items; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.album_main_item, viewGroup, false);
            }

            ImageView iv = (ImageView) view.findViewById(R.id.ivAlbum);
            TextView tv = (TextView) view.findViewById(R.id.tvAlbum);

            Uri imageUri = Uri.parse(gList.get(i).getString("thumbnail"));
            String title = gList.get(i).getString("title");

            Glide.with(context).load(imageUri).thumbnail(0.1f).into(iv);
            iv.setClipToOutline(true);

            tv.setText(title);

            //년별앨범 -> 달별앨범 이동
            if(viewGroup.getId() == R.id.gvYearAlbum) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent viewIntent = new Intent(context, MonthAlbumActivity.class);
                        viewIntent.putExtra("year", title);
                        context.startActivity(viewIntent);
                    }
                });
            }
            else { //마이앨범, 달별앨범 -> 사진페이지로 이동
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent viewIntent = new Intent(context, AlbumPageActivity.class);
                        viewIntent.putExtra("title", title);
                        context.startActivity(viewIntent);
                    }
                });
            }
        } catch (JSONException e) {
            Log.e("GridAdapter", "Error: " + e);
        }

        return view;
    }
}
