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
import android.widget.EditText;
import android.widget.ImageView;
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

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder> {

    private ArrayList<JSONObject> mData = null;
    private Context mContext = null;


    public MultiImageAdapter(ArrayList<JSONObject> list, Context context) {
        mData = list;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        EditText comment;

        //public ImageView photo_big;
        //public View photo_fore;
        //public TextView photo_text;
        ViewHolder(View itemView){
            super(itemView);
            //뷰 객체에 대한 참조
            image = itemView.findViewById(R.id.ivPhotos);
            comment = itemView.findViewById(R.id.edtComment);

            //photo_big = itemView.findViewById(R.id.photo_big);
            //photo_fore = itemView.findViewById(R.id.photo_fore);
            //photo_text = itemView.findViewById(R.id.photo_text);
        }
    }


    @NonNull
    @Override
    public MultiImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        //context에서 LayoutInflater 객체를 얻는다.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.album_page_item, parent, false);
        MultiImageAdapter.ViewHolder viewHolder = new MultiImageAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //이미지 셋팅하기
        Uri imageUri = null;
        try {
            imageUri = Uri.parse(mData.get(position).getString("uri"));
            InputStream inputStream = mContext.getContentResolver().openInputStream(imageUri);
            ExifInterface exif = new ExifInterface(inputStream);

            Glide.with(mContext).load(imageUri).thumbnail(0.1f).into(holder.image);

        } catch (Exception e) {
            Log.e("MultiImageAdapter", "onBindViewHolder Get Uri Thumbnail Error: " + e);
        }


        //클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "클릭", Toast.LENGTH_SHORT).show();

                /*Context context = view.getContext();
                Intent photo_data = new Intent(context, PhotoCustomDialog.class);
                ((AlbumPageActivity)context).startActivity(photo_data);*/


                //-------------
                Context context = view.getContext();
                Intent photo_data = new Intent(context, PhotoDataActivity.class);
                photo_data.putExtra("photo_data", String.valueOf(mData.get(holder.getBindingAdapterPosition())));
                ((AlbumPageActivity)context).startActivity(photo_data);

                //-------------
                /*ImageView photo_big = holder.itemView.findViewById(R.id.photo_big);
                View photo_fore = holder.itemView.findViewById(R.id.photo_fore);
                TextView photo_text = holder.itemView.findViewById(R.id.photo_text); //=> 안되나바... 클릭하는 순간 앱 꺼짐..

                photo_big.setVisibility(View.VISIBLE);

                // 클릭하면 사진 정보 보여주기
                photo_big.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(photo_fore.getVisibility() == View.INVISIBLE){
                            photo_fore.setVisibility(View.VISIBLE);
                            photo_text.setVisibility(View.VISIBLE);
                        }
                        else{
                            photo_fore.setVisibility(View.INVISIBLE);
                            photo_text.setVisibility(View.INVISIBLE);
                        }
                    }
                });*/

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
            Log.e("MultiImageAdapter", "onBindViewHolder Get Set Comment Error: " + e);
        }

        holder.comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    //작성한 코멘트 서버로 보낼 변수에 넣기
                    mData.get(holder.getBindingAdapterPosition()).put("comment", editable.toString());
                } catch (Exception e) {
                    Log.e("MultiImageAdapter", "Put Comment Error: " + e);
                }
                ;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}