package com.example.nadri4_edit1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AlbumMainActivity extends AppCompatActivity {

    ImageView folder1, folder2, folder3;
    ImageButton imgbtn_calendar, imgbtn_search_a;
    GridLayout testFolderLayout;
    GridView gridView;

    String src;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_main_layout);

        //xml변수 연결
        folder1 = (ImageView) findViewById(R.id.imageView1);
        folder2 = (ImageView) findViewById(R.id.imageView2);
        folder3 = (ImageView) findViewById(R.id.imageView3);

        gridView = (GridView) findViewById(R.id.gvYearAlbum);

        imgbtn_calendar = (ImageButton) findViewById(R.id.calendar_button);
        imgbtn_search_a = (ImageButton) findViewById(R.id.a_search_button);

        testFolderLayout = (GridLayout) findViewById(R.id.testFolderLayout);

        folder1.setClipToOutline(true);
        folder2.setClipToOutline(true);
        folder3.setClipToOutline(true);

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
                Intent viewIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(viewIntent);
            }
        });

        GridAdapter gAdapter = new GridAdapter(this);
        gAdapter.setItem(ReqServer.yearAlbumList);
        gridView.setAdapter(gAdapter);

    }
}
