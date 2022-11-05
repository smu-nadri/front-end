package com.example.nadri4_edit1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

public class SearchPageActivity extends AppCompatActivity {

    TextView tvTitle;
    static RecyclerView recyclerView;
    static SearchPageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page_view);

        tvTitle = (TextView) findViewById(R.id.tvPageDate);
        recyclerView = (RecyclerView) findViewById(R.id.searchRv);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String title = intent.getStringExtra("title");
        String target = intent.getStringExtra("target");

        //제목 설정
        tvTitle.setText(title);
        try {
            ReqServer.album.put("title", title);
            ReqServer.album.put("type", "searchAlbum");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //해당 태그가 달린 사진들 불러오기
        ReqServer.reqGetTagPage(this, type, target);

        //화면 설정
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(manager);

        adapter = new SearchPageAdapter(ReqServer.photoList, recyclerView.getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReqServer.photoList.clear();
        ReqServer.album.remove("title");
        ReqServer.album.remove("thumbnail");
        ReqServer.album.remove("type");
    }
}
