package com.example.nadri4_edit1;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;

public class PageList_deprecated extends Activity {
    TextView tvPageDate;
    Button btnBack, btnCreate;
    GridView gridView;

    //이미지의 uri를 담는 리스트
    ArrayList<Uri> uriList = new ArrayList<>();
    
    //day값
    int iDay;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_of_the_day);

        //xml변수 연결
        tvPageDate = (TextView) findViewById(R.id.tvPageDate);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        gridView = (GridView) findViewById(R.id.gridView);
        MyGridAdapter gridAdapter = new MyGridAdapter(this);

        gridView.setAdapter(gridAdapter);

        //인텐트
        Intent intent = getIntent();
        iDay = intent.getIntExtra("SelectedDATE",-1);

        //화면 설정
        setView(iDay);


        //화면전환
        /*btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PhotoList.class);

                //day값만 넘겨주기
                intent.putExtra("SelectedDATE", iDay);
                //전송
                startActivity(intent);
                finish();
            }
        });*/

        //이미지 가져오기 버튼
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //다중 이미지 선택 세팅
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(Intent.createChooser(intent,"다중 선택은 '포토'를 선택하세요."), 1);*/
                //intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(Intent.createChooser(intent, "다중선택"), 1);

                /*startActivityForResult(Intent.createChooser(intent, "Add images"), SELECT_MULTIPLE_IMAGES);*/
            }
        });




        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //앨범에서 액티비티로 돌아온 후 실행되는 메서드
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //선택된 이미지 없음
        if(data == null){
            Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }//선택된 이미지 없음
        //이미지를 하나라도 선택할 경우
        else{

            Intent intent = new Intent(getApplicationContext(), AlbumPageActivity.class);
            //이미지 하나만 선택
            if(data.getClipData() == null){
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);
                intent.putExtra("Photo",uriList);
            }//이미지를 하나만 선택
            //이미지 하나 이상 선택
            else{
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                for(int i=0; i< clipData.getItemCount(); i++){
                    Uri imageUri = clipData.getItemAt(i).getUri();  //선택한 이미지들의 uri를 가져온다.
                    try {
                        uriList.add(imageUri);  //uri를 리스트에 담는다
                        intent.putExtra("Photo",uriList);
                    }catch (Exception e){
                        Log.d("ERR", "file select error");
                    }
                }
            }//이미지를 여러장 선택
            intent.putExtra("Date", iDay);  //날짜 넘겨주기
            startActivity(intent);
        }//이미지 선택함
    }

    //파일경로 Uri를 String으로 바꿔주는 함수
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }

        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        }
        return null;
    }


    //날짜 포맷
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String dateFormat(Calendar calendar, int day){  //2022년 5월

        //년월 포맷
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy   MM");
        return date.format(formatter);*/
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;

        String date = year + "년  " + month + "월  " + day + "일 ";

        return date;
    }

    //화면 설정
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setView(int day){
        tvPageDate.setText(dateFormat(CalendarUtil.selectedDate, day));
    }

    //그리드뷰 어댑터
    public class MyGridAdapter extends BaseAdapter {
        Context context;

        Integer[] pageID = {R.drawable.gomurea1, R.drawable.gomurea2, R.drawable.gomurea3, R.drawable.gomurea4, R.drawable.gomurea5};


        public MyGridAdapter(Context c) {
            context = c;
        }

        //이미지 개수 받기 (그 개수만큼 반복시키기 위해)
        @Override
        public int getCount() {
            return pageID.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        //getCount에서 받은 값만큼 반복된다.
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imgView = new ImageView(context);

            imgView.setLayoutParams(new GridView.LayoutParams(200, 300));
            imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgView.setPadding(5,5,5,5);

            imgView.setImageResource(pageID[i]);

            return imgView;
        }
    }
}
