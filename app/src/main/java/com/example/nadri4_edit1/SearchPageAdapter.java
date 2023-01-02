package com.example.nadri4_edit1;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class SearchPageAdapter extends RecyclerView.Adapter<SearchPageAdapter.ViewHolder> {

    private ArrayList<JSONObject> mData = null;
    private Context mContext = null;

    private boolean isEdit = false;
    private TreeSet<Integer> checkList = new TreeSet<>();

    public ArrayList<JSONObject> getmData() {
        return mData;
    }

    public SearchPageAdapter(ArrayList<JSONObject> list, Context context) {
        mData = list;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        EditText comment;
        FrameLayout item;

        ViewHolder(View itemView){
            super(itemView);
            //뷰 객체에 대한 참조
            image = itemView.findViewById(R.id.ivPhotos);
            comment = itemView.findViewById(R.id.edtComment);
            item = itemView.findViewById(R.id.pageItem);

            comment.setFocusableInTouchMode(false);
        }
    }


    @NonNull
    @Override
    public SearchPageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        //context에서 LayoutInflater 객체를 얻는다.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.album_page_item, parent, false);
        SearchPageAdapter.ViewHolder viewHolder = new SearchPageAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //이미지 셋팅하기
        Uri imageUri = null;
        try {
            imageUri = Uri.parse(mData.get(position).getString("uri"));

            Glide.with(mContext).load(imageUri).thumbnail(0.1f).into(holder.image);

        } catch (Exception e) {
            Log.e("SearchPageAdapter", "onBindViewHolder Get Uri Thumbnail Error: " + e);
        }


        //클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "클릭", Toast.LENGTH_SHORT).show();

                Context context = view.getContext();
                Intent photo_data = new Intent(context, PhotoDataActivity.class);
                photo_data.putExtra("photo_data", holder.getBindingAdapterPosition());
                context.startActivity(photo_data);
            }
        });

        //코멘트 셋팅하기
        JSONObject imageObject = mData.get(holder.getBindingAdapterPosition());
        try {
            if(imageObject.has("comment")) {    //코멘트가 있으면
                holder.comment.setText(imageObject.getString("comment"));
            }
            else {
                if(imageObject.has("tags")){    //코멘트가 없고 태그가 있으면
                    JSONArray tags = new JSONArray(imageObject.getString("tags"));
                    String comment = "";
                    for(int i = 0; i < tags.length(); i++){
                        comment += "#" + tags.getJSONObject(i).getString("tag_ko1") + " ";
                    }
                    holder.comment.setText(comment);
                }
            }
        } catch (JSONException e) {
            Log.e("SearchPageAdapter", "onBindViewHolder Get Set Comment Error: " + e);
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent photo_data = new Intent(context, PhotoDataActivity.class);
                photo_data.putExtra("photo_data", holder.getBindingAdapterPosition());
                photo_data.putExtra("search", true);
                context.startActivity(photo_data);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}