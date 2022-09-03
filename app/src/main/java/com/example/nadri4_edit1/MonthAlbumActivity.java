package com.example.nadri4_edit1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

public class MonthAlbumActivity extends AppCompatActivity {


    ImageButton imgbtn_calendar;
    GridLayout glNadriAlbum;
    GridView gridView;

    String src;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthalbum_view);

        //xml변수 연결
        gridView = (GridView) findViewById(R.id.gvMonthAlbum);
        imgbtn_calendar = (ImageButton) findViewById(R.id.calendar_button);
        glNadriAlbum = (GridLayout) findViewById(R.id.glNadriAlbum);

        //이미지버튼(앨범버튼) 이벤트 -> 앨범뷰로 이동
        imgbtn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), CalendarMainActivity.class);
                startActivity(viewIntent);
            }
        });

        //선택한 년도 저장
        Intent intent = getIntent();
        String year = intent.getStringExtra("year");

        //선택한 년도의 월별 앨범을 찾아서 어댑터에 추가해 화면 설정
        AlbumGvAdapter gAdapter = new AlbumGvAdapter(this);
        ReqServer.monthAlbumList.forEach(item -> {
            try {
                if(item.getString("title").contains(year)){
                    gAdapter.addItem(item);
                };
            } catch (JSONException e) {
                Log.e("MonthActivity", "Error: " + e);
            }
        });
        gridView.setAdapter(gAdapter);
    }
}
