package com.example.nadri4_edit1;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;

import java.util.Calendar;

public class AlbumMainActivity extends AppCompatActivity {

    private static Context context;

    ImageButton imgbtn_calendar, imgbtn_search_a, stats_button; //캘린더 버튼, 검색버튼, 차트버튼
    static ImageView highlight_img, all_img;   //하이라이트 이미지뷰, 모두보기 이미지뷰
    GridLayout glNadriAlbum;    //나드리앨범 그리드레이아웃
    static GridView  gvCustomAlbum; //마이앨범 그리드뷰
    static GridView gvYearAlbum;    //달력앨범 그리드뷰
    static LinearLayout nadriAlbum, customAlbum, dateAlbum, create_album, all_album, highlight_album;

    static AlbumGvAdapter cAdapter; //마이앨범 어댑터
    static AlbumGvAdapter yAdapter; //달력앨범 어댑터

    static TextView highlight_tv;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_main_layout);

        context = this;

        //xml변수 연결
        create_album = (LinearLayout) findViewById(R.id.create_album);
        all_album = (LinearLayout) findViewById(R.id.all_alubm);
        highlight_album = (LinearLayout) findViewById(R.id.highlight_album);

        all_img = (ImageView) findViewById(R.id.all_img);
        highlight_img = (ImageView) findViewById(R.id.highlight_img);

        gvCustomAlbum = (GridView) findViewById(R.id.gvCustomAlbum);

        gvYearAlbum = (GridView) findViewById(R.id.gvYearAlbum);
        glNadriAlbum = (GridLayout) findViewById(R.id.glNadriAlbum);

        imgbtn_calendar = (ImageButton) findViewById(R.id.calendar_button); //캘린더로 이동하는 버튼
        imgbtn_search_a = (ImageButton) findViewById(R.id.a_search_button); //검색으로 이동하는 버튼
        stats_button = (ImageButton) findViewById(R.id.stats_button);

        nadriAlbum = (LinearLayout) findViewById(R.id.nadriAlbum);
        customAlbum = (LinearLayout) findViewById(R.id.customAlbum);
        dateAlbum = (LinearLayout) findViewById(R.id.dateAlbum);

        highlight_tv = (TextView) findViewById(R.id.highlight_tv);

        //사진 라운드 처리
        highlight_img.setClipToOutline(true);
        all_img.setClipToOutline(true);

        //앨범 목록 가져오기
        ReqServer.reqGetAlbums(AlbumMainActivity.this, 1);
        ReqServer.reqGetHighlight(this);    //하이라이트 테스트

        //뷰페이저(하이라이트) 이동 버튼
        highlight_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), HighlightActivity.class);
                startActivity(viewIntent);
            }
        });

        //이미지버튼(캘린더버튼) 이벤트 -> 캘린더뷰로 이동
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

        //앨범 생성 버튼
        create_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AlbumPageActivity.class);
                intent.putExtra("customAlbum", true);
                intent.putExtra("getImage", true);
                startActivity(intent);
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

        //처음 화면 셋팅
        setAlbumMainView();
        setAlbumMainViewVisibility();

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected static void setAlbumMainViewVisibility(){
        if(ReqServer.customAlbumList.isEmpty() && ReqServer.dateAlbumList.isEmpty()) {
            //마이앨범도 달력앨범도 비어있으면
            all_album.setVisibility(View.INVISIBLE);
            customAlbum.setVisibility(View.GONE);
            dateAlbum.setVisibility(View.GONE);
        }
        else if(ReqServer.customAlbumList.isEmpty()){
            //마이앨범만 비어있으면
            all_album.setVisibility(View.VISIBLE);
            customAlbum.setVisibility(View.GONE);
            dateAlbum.setVisibility(View.VISIBLE);
        }
        else if(ReqServer.dateAlbumList.isEmpty()){
            //달력앨범만 비어있으면
            all_album.setVisibility(View.VISIBLE);
            customAlbum.setVisibility(View.VISIBLE);
            dateAlbum.setVisibility(View.GONE);
        }
        else {
            //둘 다 있으면
            all_album.setVisibility(View.VISIBLE);
            customAlbum.setVisibility(View.VISIBLE);
            dateAlbum.setVisibility(View.VISIBLE);
        }

        //하이라이트 유무
        if(ReqServer.highlightList.isEmpty()){
            highlight_album.setVisibility(View.INVISIBLE);
        }
        else {
            highlight_album.setVisibility(View.VISIBLE);
        }
    }
}
