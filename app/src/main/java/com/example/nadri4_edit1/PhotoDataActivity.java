package com.example.nadri4_edit1;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class PhotoDataActivity extends AppCompatActivity {
    static MultiImageAdapter adapter;

    FrameLayout photoLayout;
    ImageView photo_big;
    View photo_fore;
    TextView photo_text;

    ImageButton btnGetImage, btnSave;
    TextView tvPageDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_data_layout);

        //xml연결
        photoLayout = findViewById(R.id.photoLayout);
        photo_big = findViewById(R.id.imgView);
        photo_fore = findViewById(R.id.photo_fore);
        photo_text = findViewById(R.id.photo_text);

        //xml연결
        btnGetImage = (ImageButton) findViewById(R.id.btnGetImage);
        tvPageDate = (TextView) findViewById(R.id.tvPageDate);
        btnSave = (ImageButton) findViewById(R.id.btnSave);


        //인텐트
        Intent intent = getIntent();
        String photo_data = intent.getStringExtra("photo_data");

        String title = null;
        String uri = null;
        JSONArray tags;
        //String comment = null;

        Log.d("PHOTO ", photo_data);

        try {
            JSONObject photo_data_json = new JSONObject(photo_data);
            title = photo_data_json.getJSONObject("album").getString("title");
            uri = photo_data_json.getString("uri");
            tags = photo_data_json.getJSONArray("tags");
                    //{ _id, tag_en, tag_ko1, tag_ko2 }
            //comment = photo_data_json.getString("comment");

            tvPageDate.setText(title);
            //photo_text.append(comment);

            //photo_big.setImageResource(Uri.parseUri(uri));
            Glide.with(this).load(Uri.parse(uri)).into(photo_big);



        } catch (JSONException e) {
            Log.d("검사 ", title + ", ");
        }




        //보여줄 사진 선택


        //사진 정보 가져오기



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
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String dateFormat(Calendar calendar, int day){  //2022년 5월

        //년월 포맷
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;

        String date = year + "년  " + month + "월  " + day + "일 ";

        return date;
    }
    //제목 설정
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setView(int day){
        tvPageDate.setText(dateFormat(CalendarUtil.selectedDate, day));
    }
}
