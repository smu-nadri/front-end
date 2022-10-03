package com.example.nadri4_edit1;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.TextView;
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


    private static Context context;

    ImageView folder1, folder2, folder3;
    ImageButton imgbtn_calendar, imgbtn_search_a, btnGetImage;
    GridLayout glNadriAlbum;
    static GridView  gvCustomAlbum;
    static GridView gvYearAlbum;
    LinearLayout nadriAlbum, customAlbum, dateAlbum, highlight_album;

    static AlbumGvAdapter cAdapter;
    static AlbumGvAdapter yAdapter;

    String src;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_main_layout);

        context = this;

        //xml변수 연결
        highlight_album = (LinearLayout) findViewById(R.id.highlight_album);
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

        //앨범 목록 가져오기
        ReqServer.reqGetAlbums(AlbumMainActivity.this, 1);

        //뷰페이저 이동 버튼
        highlight_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), HighlightActivity.class);
                startActivity(viewIntent);
            }
        });

        /*//앨범리스트가 없으면 GONE으로 변경
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
        }*/

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
        setAlbumMainView();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ReqServer.reqGetAlbums(this, 1);
    }

    protected static void setAlbumMainView() {
        cAdapter = new AlbumGvAdapter(context);
        cAdapter.setItem(ReqServer.customAlbumList);
        gvCustomAlbum.setAdapter(cAdapter);

        yAdapter = new AlbumGvAdapter(context);
        yAdapter.setItem(ReqServer.yearAlbumList);
        gvYearAlbum.setAdapter(yAdapter);
    }
}
