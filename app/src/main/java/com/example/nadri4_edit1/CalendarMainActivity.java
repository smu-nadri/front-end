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
    TextView btnPrev, btnNext;

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

        imgbtn_album = (ImageButton) findViewById(R.id.album_button);
        imgbtn_search_c = (ImageButton) findViewById(R.id.c_search_button);

        recyclerView = (RecyclerView) findViewById(R.id.rvCalender);


        ReqServer.reqGetAlbums(CalendarMainActivity.this, 0);


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
                Intent viewIntent = new Intent(getApplicationContext(), SearchMainActivity.class);
                startActivity(viewIntent);
            }
        });

    }   //onCreate()

    @Override
    protected void onRestart() {
        super.onRestart();
        ReqServer.reqGetAlbums(this, 0);
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

        int monthTest = monthCalendar.get(Calendar.MONTH);

        //1일로 셋팅 (n월 1일)
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        //요일 가져와서 -1 (일요일=0, 월요일=1, ~, 토요일=6)
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;

        //날짜 셋팅(매달 첫 주의 일요일 날짜, 매달 1일이 몇요일인지(몇칸 떨어져 있는지 n=1~n))
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        //날짜 화면에 출력
        //setCalendarDate(monthCalendar.get(Calendar.MONTH) + 1);


        //함수사용
        int lastDay = getLastDayOfMonth(monthTest);

        //int size = firstDayOfMonth + monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int size = firstDayOfMonth + lastDay;
        int size2 = (size+size/7);
        int monthT = monthCalendar.get(Calendar.MONTH);
        Log.d("WEEK: ", "결과" + firstDayOfMonth+", " + lastDay + ", " + size + ", " + size2);

        if(size<=35) {
            while (dateList.size() < 35) {
                //리스트에 날짜 등록
                dateList.add(monthCalendar.getTime());

                //1일씩 늘린 날짜로 변경한다. (5월 1일->5월 2일->5월 3일)
                monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        else{
            while (dateList.size() < 42){
                //리스트에 날짜 등록
                dateList.add(monthCalendar.getTime());

                //1일씩 늘린 날짜로 변경한다. (5월 1일->5월 2일->5월 3일)
                monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return dateList;
    }

    private static int getLastDayOfMonth(int month){
        ArrayList<Date> dateList = new ArrayList<>();

        //날짜 복사해서 변수 생성
        Calendar monthCalendar = (Calendar) CalendarUtil.selectedDate.clone();

        monthCalendar.set(Calendar.MONTH, month);
        Log.d("WEEK", "함수"+month + ", " + monthCalendar.get(Calendar.MONTH));

        int lastDay = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        return lastDay;
    }
}