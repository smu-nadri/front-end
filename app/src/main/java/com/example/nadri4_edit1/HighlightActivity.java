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

public class HighlightActivity extends AppCompatActivity {
    static MultiImageAdapter adapter;

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
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        HighlightAdapter adapter = new HighlightAdapter(setTextList()); //어댑터 생성, 아이템리스트를 파라미터로 사용
        viewPager.setAdapter(adapter);  //뷰페이저에 어댑터 등록!



        //인텐트 - 사진메타정보 통으로 가져옴
        /*Intent intent = getIntent();
        String photo_data = intent.getStringExtra("photo_data");

        String title = null;
        String uri = null;
        String location = null;
        JSONArray tags_arr;
        String tags_str = "";
        String comment = null;

        Log.d("PHOTO ", photo_data);

        //사진 정보 가져와서 setText&append
        //+) UI 정리하고싶은것 : 사진사이즈조절.......
        try {
            JSONObject photo_data_json = new JSONObject(photo_data);
            photo_text.setText("사진 정보\n\n");

            //달력앨범 타이틀 포맷 맞춰야함!!
            title = photo_data_json.getJSONObject("album").getString("title");
            tvPageDate.setText(title);

            uri = photo_data_json.getString("uri");
            //photo_big.setImageResource(Uri.parseUri(uri));
            Glide.with(this).load(Uri.parse(uri)).into(photo_big);

            //한글(location-address)만 가져오도록 함
            //location = photo_data_json.getString("location"); //-> 좌표까지 가져옴
            location = photo_data_json.getJSONObject("location").getString("address");
            photo_text.append(" - 위치 : "+location+"\n");

            //array로 저장돼있어서 어케 불러와야할지 모르겠음
            tags_arr = photo_data_json.getJSONArray("tags");
            //{ _id, tag_en, tag_ko1, tag_ko2 }
            for(int i=0; i<tags_arr.length(); i++){
                tags_str = tags_str + "#" + tags_arr.getJSONObject(i).getString("tag_ko1") + " ";
                //인덱스 진짜 어케..
            } //-> location처럼 해볼라했는데 안 되네..
            photo_text.append(" - 태그 : " + tags_str + "\n");

            //DB의 comment에 저장된 내용이 하나도 없을 땐 오류나는듯?
            //DB comment에 "#태그"는 저장하지 않도록 할 수 있나?(태그까지 comment로 저장돼서 출력할 때 중복됨) 아님 append할 때 처리해야하는디 어케하지
            //comment의 개행을 잘 처리해야 깔끔할듯
            comment = photo_data_json.getString("comment");
            photo_text.append(" - 내용 : "+comment+"\n");


        } catch (JSONException e) {
            Log.d("검사 ", title + ", ");
        }*/




        //보여줄 사진 선택


        //사진 정보 가져오기



        // 클릭하면 사진 정보 보여주기
        /*photo_big.setOnClickListener(new View.OnClickListener() {
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
        });*/


    }

    private List<String> setTextList() {

        ArrayList<String> itemList = new ArrayList();
        itemList.add("Page 1");
        itemList.add("Page 2");
        itemList.add("Page 3");
        itemList.add("Page 4");
        itemList.add("Page 5");
        itemList.add("Page 6");
        itemList.add("Page 7");
        itemList.add("Page 8");
        itemList.add("Page 9");
        itemList.add("Page 10");

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
