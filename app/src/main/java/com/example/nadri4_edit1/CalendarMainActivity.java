package com.example.nadri4_edit1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CalendarMainActivity extends AppCompatActivity {
    static TextView currentYear;
    static TextView currentMonth;   //년, 월 텍스트뷰
    TextView btnPrev, btnNext, btnSettings;

    ImageButton imgbtn_album, imgbtn_search_c;

    static RecyclerView recyclerView;

    public static Context context;

    //Calendar calendar;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        context = this; //컨텍스트 지정

        //초기화
        currentYear = (TextView) findViewById(R.id.tvCurrentYear);
        currentMonth = (TextView) findViewById(R.id.tvCurrentMonth);
        btnPrev = (TextView) findViewById(R.id.tvPrev);
        btnNext = (TextView) findViewById(R.id.tvNext);
        btnSettings = (TextView) findViewById(R.id.btnSettings);

        imgbtn_album = (ImageButton) findViewById(R.id.album_button);
        imgbtn_search_c = (ImageButton) findViewById(R.id.c_search_button);

        recyclerView = (RecyclerView) findViewById(R.id.rvCalender);


        Log.d("GOO", "well come");
        //서버테스트 - 구버전 - deprecated
        /*btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GOO", "hwa");
                ReqServer.request(MainActivity.this, 0);

            }
        });*/
        ReqServer.reqGetAlbums(CalendarMainActivity.this);


        //현재날짜
        CalendarUtil.selectedDate = Calendar.getInstance();
        //CalendarUtil.selectedDate = LocalDate.now();

        //달력 초기화(오늘 날짜)
        //calendar = Calendar.getInstance();


        //화면설정
        setMonthView();

        //이전 버튼 이벤트
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //(현재월)-1
                //CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1);
                CalendarUtil.selectedDate.add(Calendar.MONTH, -1);   //-1
                setMonthView();
            }
        });

        //이후 버튼 이벤트
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //(현재월)+1
                //CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1);
                CalendarUtil.selectedDate.add(Calendar.MONTH, +1);   //+1
                setMonthView();
            }
        });

        //이미지버튼(앨범버튼) 이벤트 -> 앨범뷰로 이동
        imgbtn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), AlbumMainActivity.class);
                startActivity(viewIntent);
            }
        });

        //이미지버튼(검색버튼) 이벤트 -> 검색뷰로 이동
        imgbtn_search_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(viewIntent);
            }
        });

    }   //onCreate()

    @Override
    protected void onRestart() {
        super.onRestart();
        ReqServer.reqGetAlbums(this);
    }

    //날짜 타입 설정(2022   05)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String dateFormat(Calendar calendar){  //2022년 5월

        //년월 포맷
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy   MM");
        return date.format(formatter);*/
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;

        String yearMonth = year + "년  " + month + "월  ";

        return yearMonth;
    }

    //년도
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String selectedYear(Calendar calendar){  //2022년 5월

        //년월 포맷
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy   MM");
        return date.format(formatter);*/
        int year = calendar.get(Calendar.YEAR);

        String selectedY = String.valueOf(year);

        return selectedY;
    }

    //월
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String selectedMonth(Calendar calendar){  //2022년 5월

        //년월 포맷
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy   MM");
        return date.format(formatter);*/
        int month = calendar.get(Calendar.MONTH)+1;

        String selectedM = month + "월";

        return selectedM;
    }

    //날짜 타입 설정(2022년 05월 23일)
    /*@RequiresApi(api = Build.VERSION_CODES.O)
    private String dateFormatTextView(LocalDate date){  //2022년 5월
        //년월 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년  MM월");
        return date.format(formatter);
    }*/


    //화면 설정
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static void setMonthView(){
        //텍스트뷰 셋팅
        currentYear.setText(selectedYear(CalendarUtil.selectedDate));
        currentMonth.setText(selectedMonth(CalendarUtil.selectedDate));

        //해당 월 날짜 가져오기
        ArrayList<Date> dateList = daysInMonthArray();

        //어댑터 데이터 적용
        CalendarAdapter adapter = new CalendarAdapter(dateList, context);

        //레이아웃 설정(열 = 7)
        RecyclerView.LayoutManager manager = new GridLayoutManager(context, 7);

        //레이아웃 적용
        recyclerView.setLayoutManager(manager);

        //어댑터 적용
        recyclerView.setAdapter(adapter);

    }

    //날짜 생성
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList<Date>daysInMonthArray(){

        ArrayList<Date> dateList = new ArrayList<>();

        //날짜 복사해서 변수 생성
        Calendar monthCalendar = (Calendar) CalendarUtil.selectedDate.clone();

        //1일로 셋팅 (5월 1일)
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        //요일 가져와서 -1 (일요일=1, 월요일=2)
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;

        //날짜 셋팅(-5일 전)
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        //42전까지 반복
        while(dateList.size() < 42){
            //리스트에 날짜 등록
            dateList.add(monthCalendar.getTime());

            //1일씩 늘린 날짜로 변경한다. (5월 1일->5월 2일->5월 3일)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        /*
        YearMonth yearMonth = YearMonth.from(date);

        //해당 월 마지막 날짜 가져오기
        int lastDay = yearMonth.lengthOfMonth();

        //해당 월 첫 번째 날짜 가져오기
        LocalDate firstDay = CalendarUtil.selectedDate.withDayOfMonth(1);

        //첫 번째 날 요일 가져오기
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        //날짜 생성
        for(int i = 1; i < 42; i++){
            if(i <= dayOfWeek || i > lastDay+dayOfWeek){
                dateList.add(null);
            }
            else {
                dateList.add(LocalDate.of(CalendarUtil.selectedDate.getYear(), CalendarUtil.selectedDate.getMonth(), i - dayOfWeek));
            }
        }*/

        return dateList;
    }

    //인터페이스 구현!(날짜 어댑터에서 넘겨준 날짜를 받는다.)
    /*@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(String dateText) {
        String date = dateFormatTextView(selectedDate) + "  " + dateText + "일";
        Toast.makeText(context, "클릭: "+ date, Toast.LENGTH_SHORT).show();
    }*/
}