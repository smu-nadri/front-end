package com.example.nadri4_edit1;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlbumPageActivity extends AppCompatActivity {

    static RecyclerView recyclerView;  //이미지를 보여주는 뷰
    static MultiImageAdapter adapter;

    Button btnGetImage, btnSave;
    TextView tvPageDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_page_layout);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        //xml연결
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btnGetImage = (Button) findViewById(R.id.btnGetImage);
        tvPageDate = (TextView) findViewById(R.id.tvPageDate);
        btnSave = (Button) findViewById(R.id.btnSave);

        Log.d("GOO", "well come");
        //서버로 사진 정보 전송하기
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReqServer.reqPostPages(AlbumPageActivity.this);
            }
        });

        //인텐트
        Intent getDateIntent = getIntent();
        int iDay = getDateIntent.getIntExtra("SelectedDATE",-1);
        //화면 설정
        String title;
        if (iDay == -1){
            title = getDateIntent.getStringExtra("title");
            ReqServer.stitle = title;
            tvPageDate.setText(title);
        }
        else {
            setView(iDay);
            String tMonth, tDay;
            if(CalendarUtil.selectedDate.get(Calendar.MONTH) + 1 < 10) tMonth = "0" + (CalendarUtil.selectedDate.get(Calendar.MONTH) + 1);
            else tMonth = String.valueOf(CalendarUtil.selectedDate.get(Calendar.MONTH) + 1);
            if(iDay < 10) tDay = "0" + iDay;
            else tDay = String.valueOf(iDay);

            ReqServer.stitle = CalendarUtil.selectedDate.get(Calendar.YEAR) + "-" + tMonth + "-" + tDay;
        }

        //레이아웃 설정(열 = 2)
        RecyclerView.LayoutManager manager = new GridLayoutManager(AlbumPageActivity.recyclerView.getContext(), 2);
        //recyclerView.LayoutManager(new GridLayoutManager(this, 2));

        //레이아웃 적용
        recyclerView.setLayoutManager(manager);


        //기존에 저장된 사진들 불러오기
        ReqServer.reqGetPages(AlbumPageActivity.this);


        /*Intent getImageIntent = getIntent();
        uriList = (ArrayList<Uri>)getImageIntent.getSerializableExtra("Photo");
        //uriList.add((Uri) intent.getSerializableExtra("Photo"));
        adapter = new MultiImageAdapter(uriList, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        */


        //이미지 가져오기 버튼
        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);  //이거 수정했음!
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
    }

    //페이지 화면을 나갈 때 데이터 비워주기
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReqServer.photoList.clear();
        ReqServer.album.remove("title");
        ReqServer.album.remove("thumbnail");
        ReqServer.album.remove("type");
    }

    //기존에 저장된 앨범 레이아웃이 있다면 불러와서 보여줘야 함...
    /*..{내용}..*/

    //앨범에서 액티비티로 돌아온 후 실행되는 메서드 = 앨범 레이아웃 생성!
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        ContentResolver resolver = this.getContentResolver();

        if(data == null){
            Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }//선택된 이미지 없음
        else{
            if(data.getClipData() == null){
                Log.d("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                resolver.takePersistableUriPermission(imageUri, takeFlags);

                setUriTags(imageUri);
            }//이미지를 하나만 선택
            else{
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                for(int i=0; i< clipData.getItemCount(); i++){
                    Uri imageUri = clipData.getItemAt(i).getUri();  //선택한 이미지들의 uri를 가져온다.
                    resolver.takePersistableUriPermission(imageUri, takeFlags); //uri 권한 부여

                    setUriTags(imageUri);
                }
            }//이미지를 여러장 선택

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

    //수정해야됨
    protected void setAdapterUpdated(){
        adapter = new MultiImageAdapter(ReqServer.photoList, AlbumPageActivity.recyclerView.getContext());

        //어댑터 적용\
        recyclerView.setAdapter(AlbumPageActivity.adapter);
    }

    protected void setUriTags(Uri imageUri){
        JSONObject imageInfo = new JSONObject();    //사진 한 장의 uri와 태그들을 담을 변수
        ArrayList<Integer> tagsIndex = new ArrayList<Integer>();    //사진 한 장의 태그들
        try {
            imageInfo.put("uri", imageUri); //uri 데이터 넣기

            //태그 불러오기
            InputImage image;
            image = InputImage.fromFilePath(this, imageUri);    //uri -> InputImage 변환
            ImageLabelerOptions options = new ImageLabelerOptions.Builder() //옵션 설정
                    .setConfidenceThreshold(0.6f)   //정확도 최솟값
                    .build();
            ImageLabeler labeler = ImageLabeling.getClient(options);
            labeler.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            try {
                                if (!labels.isEmpty()) {
                                    float confidence = labels.get(0).getConfidence();
                                    for (ImageLabel label : labels) {   //차이가 15% 이하인 태그 5개까지만 넣기
                                        Log.d("HWA", "label: " + label.getIndex() + " " + label.getText());
                                        if (tagsIndex.size() > 5) break;
                                        else if (confidence - label.getConfidence() > 0.15f)
                                            break;
                                        else tagsIndex.add(label.getIndex());
                                    }
                                    imageInfo.put("tags", tagsIndex);   //태그들 넣기
                                }
                                ReqServer.photoList.add(imageInfo); //어뎁터에 들어갈 리스트에 사진 정보 넣기
                                setAdapterUpdated(); //어뎁터 설정
                            } catch(Exception e) {
                                ReqServer.photoList.add(imageInfo);
                                setAdapterUpdated();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() { //중간에 에러나면 uri만 넣기
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ReqServer.photoList.add(imageInfo);
                            setAdapterUpdated();
                        }
                    });
        } catch (Exception e) {
            Log.e("HWA", "setUriTags Exception : " + e);
        }
    }
}