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

public class SearchPageActivity extends AppCompatActivity {

    TextView tvTitle;
    ImageButton btnGetImage, btnSave;
    static RecyclerView recyclerView;
    static MultiImageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_page_layout);

        tvTitle = (TextView) findViewById(R.id.tvPageDate);
        btnGetImage = (ImageButton) findViewById(R.id.btnGetImage);
        btnSave = (ImageButton) findViewById(R.id.btnSave);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        Integer tagIndex = intent.getIntExtra("tagIndex", -1);

        //제목 설정
        tvTitle.setText(title);

        //버튼 지우기
        btnGetImage.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);

        //해당 태그가 달린 사진들 불러오기
        ReqServer.reqGetTagPage(this, tagIndex);

        //화면 설정
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(manager);

        adapter = new MultiImageAdapter(ReqServer.photoList, recyclerView.getContext());
        recyclerView.setAdapter(adapter);
    }
}
