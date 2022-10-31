package com.example.nadri4_edit1;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AllPhotoActivity extends AppCompatActivity {
    private static Context context;

    ImageButton imgbtn_search_a, stats_button; //검색버튼, 차트버튼
    static GridView gvAllPhoto;
    static PhotoGvAdapter aAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_photo_layout);

        context = this;

        //xml변수 연결
        gvAllPhoto = (GridView) findViewById(R.id.gvAllPhoto);

        imgbtn_search_a = (ImageButton) findViewById(R.id.a_search_button); //검색으로 이동하는 버튼
        stats_button = (ImageButton) findViewById(R.id.stats_button);

        //사진 목록 가져오기
        ReqServer.reqGetAll(this);

        //이미지버튼(검색버튼) 이벤트 -> 검색뷰로 이동
        imgbtn_search_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), SearchMainActivity.class);
                startActivity(viewIntent);
            }
        });

        //이미지버튼(차트버튼) 이벤트 -> 차트뷰로 이동
        stats_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
                startActivity(intent);
            }
        });

        //앨범 결과 화면 설정
        aAdapter = new PhotoGvAdapter(this);
        aAdapter.setItem(ReqServer.allList);
        gvAllPhoto.setAdapter(aAdapter);

    }
}
