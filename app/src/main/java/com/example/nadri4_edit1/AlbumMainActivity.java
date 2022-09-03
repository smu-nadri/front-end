package com.example.nadri4_edit1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AlbumMainActivity extends AppCompatActivity {

    ImageView folder1, folder2, folder3;
    ImageButton imgbtn_calendar, imgbtn_search_a;
    GridLayout glNadriAlbum;
    GridView  gvCustomAlbum, gvYearAlbum;
    LinearLayout nadriAlbum, customAlbum, dateAlbum;

    String src;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_main_layout);

        //xml변수 연결
        folder1 = (ImageView) findViewById(R.id.imageView1);
        folder2 = (ImageView) findViewById(R.id.imageView2);
        folder3 = (ImageView) findViewById(R.id.imageView3);
        gvYearAlbum = (GridView) findViewById(R.id.gvYearAlbum);
        imgbtn_calendar = (ImageButton) findViewById(R.id.calendar_button); //캘린더로 이동하는 버튼
        imgbtn_search_a = (ImageButton) findViewById(R.id.a_search_button); //검색으로 이동하는 버튼
        glNadriAlbum = (GridLayout) findViewById(R.id.glNadriAlbum);
        nadriAlbum = (LinearLayout) findViewById(R.id.nadriAlbum);
        customAlbum = (LinearLayout) findViewById(R.id.customAlbum);
        dateAlbum = (LinearLayout) findViewById(R.id.dateAlbum);

        //사진 라운드 처리
        folder1.setClipToOutline(true);
        folder2.setClipToOutline(true);
        folder3.setClipToOutline(true);

        //앨범리스트가 없으면 GONE으로 변경
        if(ReqServer.customAlbumList.isEmpty()) {
            customAlbum.setVisibility(View.GONE);
            if(ReqServer.dateAlbumList.isEmpty()){
                nadriAlbum.setVisibility(View.GONE);
                dateAlbum.setVisibility(View.GONE);
            }
            else {
                nadriAlbum.setVisibility(View.VISIBLE);
                dateAlbum.setVisibility(View.VISIBLE);
            }
        }
        else {
            customAlbum.setVisibility(View.VISIBLE);
        }

        //이미지버튼(앨범버튼) 이벤트 -> 앨범뷰로 이동
        imgbtn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), CalendarMainActivity.class);
                startActivity(viewIntent);
            }
        });

        //이미지버튼(검색버튼) 이벤트 -> 검색뷰로 이동
        imgbtn_search_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), SearchMainActivity.class);
                startActivity(viewIntent);
            }
        });

        //처음 화면 셋팅
        DateAlbumAdapter gAdapter = new DateAlbumAdapter(this);
        gAdapter.setItem(ReqServer.yearAlbumList);
        gvYearAlbum.setAdapter(gAdapter);

    }
}
