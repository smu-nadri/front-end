package com.example.nadri4_edit1;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator3;

public class HighlightActivity extends AppCompatActivity {
    static MultiImageAdapter adapter;

    ViewPager2 viewPager;
    HighlightAdapter pagerAdapter;
    //private FragmentStateAdapter pagerAdapter;
    private int num_pager = ReqServer.highlightList.size();
    private CircleIndicator3 cIndicator;


    FrameLayout photoLayout;
    ImageView photo_big;
    View photo_fore;
    TextView photo_text;

    ImageButton btnGetImage, btnMenu;
    static TextView tvPageDate;

    //타이머!
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;      //작업 실행 전 딜레이시간(밀리초)
    final long PERIOD_MS = 3000;    //연속 작업 실행 사이의 시간(밀리초)

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highlight_layout);

        //xml연결
        photoLayout = findViewById(R.id.photoLayout);
        photo_big = findViewById(R.id.imgView);
        photo_fore = findViewById(R.id.photo_fore);
        photo_text = findViewById(R.id.photo_text);

        //xml연결
        btnGetImage = (ImageButton) findViewById(R.id.btnGetImage);
        tvPageDate = (TextView) findViewById(R.id.tvPageDate);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);

        //어댑터 연결(뷰페이저)
        //뷰페이저 세팅
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new HighlightAdapter(this, ReqServer.highlightList); //어댑터 생성, 아이템리스트를 파라미터로 사용
        viewPager.setAdapter(pagerAdapter);  //뷰페이저에 어댑터 등록!
        //viewPager.setOffscreenPageLimit(1);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                cIndicator.animatePageSelected(position%num_pager);
            }
        });

        //indicator
        cIndicator = findViewById(R.id.indicator);
        cIndicator.setViewPager(viewPager);
        cIndicator.createIndicators(num_pager, 0);

        tvPageDate.setText(ReqServer.highlightTitle);



    }
}
