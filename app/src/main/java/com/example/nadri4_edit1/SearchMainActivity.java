package com.example.nadri4_edit1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

public class SearchMainActivity extends AppCompatActivity {

    SearchView searchView;
    static GridView gridView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main_view);

        searchView = (SearchView) findViewById(R.id.searchView);
        gridView = (GridView) findViewById(R.id.gvTagList);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ReqServer.reqGetQuery(getApplicationContext(), query);  //서버로 검색 전송

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
        TagGvAdapter gAdapter = new TagGvAdapter(this);
        gAdapter.setItem(ReqServer.tagList);
        gridView.setAdapter(gAdapter);
    }
}
