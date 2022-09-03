package com.example.nadri4_edit1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

public class SearchResultActivity extends AppCompatActivity {

    SearchView searchView;
    static GridView gvResultAlbum, gvResultPhoto;
    LinearLayout resultAlbum, resultPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_view);

        searchView = (SearchView) findViewById(R.id.searchView);
        gvResultAlbum = (GridView) findViewById(R.id.gvResultAlubm);
        gvResultPhoto = (GridView) findViewById(R.id.gvResultPhoto);
        resultAlbum = (LinearLayout) findViewById(R.id.resultAlbum);
        resultPhoto = (LinearLayout) findViewById(R.id.resultPhoto);

        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        searchView.setQuery(query, false);  //넘어온 검색어 셋팅

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //서버로 검색 전송
                ReqServer.reqGetQuery(getApplicationContext(), query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if(ReqServer.sAlbumList.isEmpty())
            resultAlbum.setVisibility(View.GONE);
        else
            resultAlbum.setVisibility(View.VISIBLE);

        if(ReqServer.sPhotoList.isEmpty())
            resultPhoto.setVisibility(View.GONE);
        else
            resultPhoto.setVisibility(View.VISIBLE);

        //화면 설정
        TagGvAdapter gAdapter = new TagGvAdapter(this);
        gAdapter.setItem(ReqServer.sPhotoList);
        gvResultPhoto.setAdapter(gAdapter);
    }
}
