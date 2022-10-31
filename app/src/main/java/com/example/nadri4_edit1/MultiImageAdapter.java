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

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder> implements ItemTouchHelperListener{

    private ArrayList<JSONObject> mData = null;
    private Context mContext = null;

    private boolean isEdit = false;
    private TreeSet<Integer> checkList = new TreeSet<>();

    public ArrayList<JSONObject> getmData() {
        return mData;
    }

    public MultiImageAdapter(ArrayList<JSONObject> list, Context context) {
        mData = list;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        EditText comment;
        FrameLayout item;
        CheckBox checkbox;

        //public ImageView photo_big;
        //public View photo_fore;
        //public TextView photo_text;
        ViewHolder(View itemView){
            super(itemView);
            //뷰 객체에 대한 참조
            image = itemView.findViewById(R.id.ivPhotos);
            comment = itemView.findViewById(R.id.edtComment);
            item = itemView.findViewById(R.id.pageItem);
            checkbox = itemView.findViewById(R.id.checkbox);

            //photo_big = itemView.findViewById(R.id.photo_big);
            //photo_fore = itemView.findViewById(R.id.photo_fore);
            //photo_text = itemView.findViewById(R.id.photo_text);
        }
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        Log.d("HWA", "움직움직! " + from_position + " " + to_position);
        //이동할 객체 저장
        JSONObject person = mData.get(from_position);
        //이동할 객체 삭제
        mData.remove(from_position);
        //이동하고 싶은 position에 추가
        mData.add(to_position,person);

        for(int i = 0; i < mData.size(); i++){
            try {
                Log.d("HWA", "mData : " + i + " " + mData.get(i).getString("uri"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Adapter에 데이터 이동알림
        AlbumPageActivity.adapter.notifyItemMoved(from_position,to_position);
        return true;
    }

    @Override
    public void onItemSwipe(int position) {

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
                photo_data.putExtra("photo_data", holder.getBindingAdapterPosition());
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

        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!isEdit){
                    Log.d("HWA", "편집모드 진입!");
                    isEdit = true;
                    AlbumPageActivity.adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEdit){
                    Log.d("HWA", "눌렀음! " + holder.getBindingAdapterPosition());
                    int i = holder.getBindingAdapterPosition();
                    try {
                        if(mData.get(i).getBoolean("isChecked")) {
                            Log.d("HWA", "true : ");
                            mData.get(i).put("isChecked", false);
                            holder.checkbox.setChecked(false);
                            checkList.add(i);
                        }
                        else{
                            Log.d("HWA", "false : ");
                            mData.get(i).put("isChecked", true);
                            holder.checkbox.setChecked(true);
                            checkList.remove(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {  //임시!
                    Context context = view.getContext();
                    Intent photo_data = new Intent(context, PhotoDataActivity.class);
                    photo_data.putExtra("photo_data", holder.getBindingAdapterPosition());
                    ((AlbumPageActivity)context).startActivity(photo_data);
                }
            }
        });

        if(isEdit){
            Log.d("HWA", "편집모드입니다!");
            holder.checkbox.setVisibility(View.VISIBLE);
        }

        try {
            holder.checkbox.setChecked(mData.get(holder.getBindingAdapterPosition()).getBoolean("isChecked"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}