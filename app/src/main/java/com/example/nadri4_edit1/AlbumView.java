package com.example.nadri4_edit1;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AlbumView extends AppCompatActivity {

    ImageView folder1, folder2, folder3;
    ImageButton imgbtn_calendar;
    GridLayout testFolderLayout;

    String src;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_view);

        //xml변수 연결
        folder1 = (ImageView) findViewById(R.id.imageView1);
        folder2 = (ImageView) findViewById(R.id.imageView2);
        folder3 = (ImageView) findViewById(R.id.imageView3);

        imgbtn_calendar = (ImageButton) findViewById(R.id.calendar_button);

        testFolderLayout = (GridLayout) findViewById(R.id.testFolderLayout);

        //이미지버튼(앨범버튼) 이벤트 -> 앨범뷰로 이동
        imgbtn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(viewIntent);
            }
        });




    }
}
