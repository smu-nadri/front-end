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
    TextView tvPageDate;

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





    }
    protected ArrayList<String> setPhotoList() {
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.add("https://cdn.pixabay.com/photo/2019/12/26/10/44/horse-4720178_1280.jpg");
        itemList.add("https://cdn.pixabay.com/photo/2020/11/04/15/29/coffee-beans-5712780_1280.jpg");
        itemList.add("https://cdn.pixabay.com/photo/2020/11/10/01/34/pet-5728249_1280.jpg");
        itemList.add("https://cdn.pixabay.com/photo/2020/12/21/19/05/window-5850628_1280.png");
        itemList.add("https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg");

        /*ArrayList<String> itemList = new ArrayList<String>();
        itemList.add(String.valueOf(R.drawable.renoir03));
        itemList.add(String.valueOf(R.drawable.renoir07));
        itemList.add(String.valueOf(R.drawable.renoir09));
        itemList.add(String.valueOf(R.drawable.gomurea1));
        itemList.add(String.valueOf(R.drawable.gomurea5));*/

        /*ArrayList<String> itemList = new ArrayList<String>();
        itemList.add(String.valueOf(Glide.with(this).load(this.getResources().getIdentifier("renoir03", "drawable", this.getPackageName()))));
        itemList.add(String.valueOf(Glide.with(this).load(this.getResources().getIdentifier("renoir07", "drawable", this.getPackageName()))));
        itemList.add(String.valueOf(Glide.with(this).load(this.getResources().getIdentifier("renoir09", "drawable", this.getPackageName()))));
        itemList.add(String.valueOf(Glide.with(this).load(this.getResources().getIdentifier("renoir03", "drawable", this.getPackageName()))));
        itemList.add(String.valueOf(Glide.with(this).load(this.getResources().getIdentifier("renoir09", "drawable", this.getPackageName()))));*/

        /*String[] itemList = new String[5];
        itemList[0] = "drawable://"+R.drawable.renoir03;
        itemList[1] = "drawable://"+R.drawable.renoir07;
        itemList[2] = "drawable://"+R.drawable.renoir09;
        itemList[3] = "drawable://"+R.drawable.gomurea5;
        itemList[4] = "drawable://"+R.drawable.gomurea1;*/

        return itemList;
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
