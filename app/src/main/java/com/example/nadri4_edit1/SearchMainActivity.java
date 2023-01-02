package com.example.nadri4_edit1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchMainActivity extends AppCompatActivity {

    SearchView searchView;

    static LinearLayout faceListView, tagListView;
    static GridView gvFaceList, gvTagList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main_view);

        searchView = (SearchView) findViewById(R.id.searchView);
        faceListView = (LinearLayout) findViewById(R.id.faceListView);
        tagListView = (LinearLayout) findViewById(R.id.tagListView);
        gvTagList = (GridView) findViewById(R.id.gvTagList);
        gvFaceList = (GridView) findViewById(R.id.gvFaceList);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    ReqServer.reqGetQuery(getApplicationContext(), URLEncoder.encode(query,"utf-8"));  //서버로 검색 전송
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //인탠트 설정
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("query", query);

                //검색창 초기화
                searchView.setQuery("", false);
                searchView.clearFocus();

                //검색 결과 화면으로 이동
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //태그 리스트 가져오기
        ReqServer.reqGetTagList(this);

        //화면 설정
        TagGvAdapter tAdapter = new TagGvAdapter(this);
        tAdapter.setItem(ReqServer.tagList);
        gvTagList.setAdapter(tAdapter);

        TagGvAdapter fAdapter = new TagGvAdapter(this);
        fAdapter.setItem(ReqServer.faceList);
        gvFaceList.setAdapter(fAdapter);
    }
}
