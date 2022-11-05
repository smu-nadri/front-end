package com.example.nadri4_edit1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TagGvAdapter extends BaseAdapter {
    Context context;
    private ArrayList<JSONObject> gList = new ArrayList<JSONObject>();

    public TagGvAdapter(Context c){
        context = c;
    }
    public TagGvAdapter(Context c, ArrayList<JSONObject> items){
        context = c;
        gList = items;
    }

    public void addItem(Object item){
        gList.add((JSONObject) item);
    }

    public void setItem(ArrayList<JSONObject> items) { gList = items; }

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

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.album_main_item, viewGroup, false);
            }

            ImageView iv = (ImageView) view.findViewById(R.id.ivAlbum);
            TextView tv = (TextView) view.findViewById(R.id.tvAlbum);

            if(viewGroup.getId() == R.id.gvFaceList){
                int left = gList.get(i).getInt("left");
                int top = gList.get(i).getInt("top");
                int width = gList.get(i).getInt("width");
                int height = gList.get(i).getInt("height");

                //썸네일 셋팅
                Uri imageUri = Uri.parse(gList.get(i).getString("thumbnail"));
                Bitmap cropImg = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), imageUri));
                cropImg = Bitmap.createBitmap(cropImg, left, top, width, height);
                iv.setImageBitmap(cropImg);
                iv.setClipToOutline(true);

                //태그 이름 셋팅
                String tag = gList.get(i).getString("name");
                String faceId = gList.get(i).getString("faceId");
                tv.setText(tag);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SearchPageActivity.class);
                        intent.putExtra("title", tag);
                        intent.putExtra("target", faceId);
                        intent.putExtra("type", "face");
                        context.startActivity(intent);
                    }
                });
            }
            else if(viewGroup.getId() == R.id.gvTagList) {
                //썸네일 셋팅
                Uri imageUri = Uri.parse(gList.get(i).getString("thumbnail"));
                Glide.with(context).load(imageUri).thumbnail(0.1f).into(iv);
                iv.setClipToOutline(true);

                //태그 이름 셋팅
                String tag = gList.get(i).getJSONObject("tag").getString("tag_ko1");
                String idx = gList.get(i).getJSONObject("tag").getString("_id");
                tv.setText(tag);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SearchPageActivity.class);
                        intent.putExtra("title", tag);
                        intent.putExtra("target", idx);
                        intent.putExtra("type", "tag");
                        context.startActivity(intent);
                    }
                });
            }

        } catch (JSONException | IOException e) {
            Log.e("TagGvAdapter", "Error: " + e);
        }

        return view;
    }
}
