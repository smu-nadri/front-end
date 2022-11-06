package com.example.nadri4_edit1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchResultActivity extends AppCompatActivity {

    SearchView searchView;
    static GridView gvResultPhoto;
    static LinearLayout resultPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_view);

        searchView = (SearchView) findViewById(R.id.searchView);
        gvResultPhoto = (GridView) findViewById(R.id.gvResultPhoto);
        resultPhoto = (LinearLayout) findViewById(R.id.resultPhoto);

        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        searchView.setQuery(query, false);  //넘어온 검색어 셋팅

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //서버로 검색 전송
                try {
                    ReqServer.reqGetQuery(getApplicationContext(), URLEncoder.encode(query,"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if(ReqServer.sPhotoList.isEmpty())
            resultPhoto.setVisibility(View.GONE);
        else
            resultPhoto.setVisibility(View.VISIBLE);


        //사진 결과 화면 설정
        PhotoGvAdapter pAdapter = new PhotoGvAdapter(this);
        pAdapter.setItem(ReqServer.sPhotoList);
        gvResultPhoto.setAdapter(pAdapter);
    }
}
