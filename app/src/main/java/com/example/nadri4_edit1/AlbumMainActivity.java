package com.example.nadri4_edit1;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumMainActivity extends AppCompatActivity {

    ImageView folder1, folder2, folder3;
    ImageButton imgbtn_calendar, imgbtn_search_a, btnGetImage;
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
        gvCustomAlbum = (GridView) findViewById(R.id.gvCustomAlbum);
        gvYearAlbum = (GridView) findViewById(R.id.gvYearAlbum);
        imgbtn_calendar = (ImageButton) findViewById(R.id.calendar_button); //캘린더로 이동하는 버튼
        imgbtn_search_a = (ImageButton) findViewById(R.id.a_search_button); //검색으로 이동하는 버튼
        btnGetImage = (ImageButton) findViewById(R.id.btnGetImage);
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

        //이미지 가져오기 버튼
        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AlbumPageActivity.class);
                intent.putExtra("customAlbum", true);
                intent.putExtra("getImage", true);
                startActivity(intent);
            }
        });

        //처음 화면 셋팅
        AlbumGvAdapter cAdapter = new AlbumGvAdapter(this);
        cAdapter.setItem(ReqServer.customAlbumList);
        gvCustomAlbum.setAdapter(cAdapter);

        AlbumGvAdapter yAdapter = new AlbumGvAdapter(this);
        yAdapter.setItem(ReqServer.yearAlbumList);
        gvYearAlbum.setAdapter(yAdapter);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ReqServer.reqGetAlbums(this);
    }
}
