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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class PhotoGvAdapter extends BaseAdapter {
    Context context;
    private ArrayList<JSONObject> gList = new ArrayList<JSONObject>();

    public PhotoGvAdapter(Context c){
        context = c;
    }
    public PhotoGvAdapter(Context c, ArrayList<JSONObject> items){
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
                view = inflater.inflate(R.layout.search_result_item, viewGroup, false);
            }

            ImageView iv = (ImageView) view.findViewById(R.id.ivPhoto);

            //이미지 셋팅
            Uri imageUri = Uri.parse(gList.get(i).getString("uri"));
            Glide.with(context).load(imageUri).thumbnail(0.1f).into(iv);
            iv.setClipToOutline(true);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //해당 앨범으로 이동 추가해야 됨
                }
            });

        } catch (Exception e) {
            Log.e("PhotoGvAdapter", "getView Error: " + e);
        }

        return view;
    }
}
