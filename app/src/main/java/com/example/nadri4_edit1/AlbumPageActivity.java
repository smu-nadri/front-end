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
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlbumPageActivity extends AppCompatActivity {

    static RecyclerView recyclerView;  //이미지를 보여주는 뷰
    static MultiImageAdapter adapter;

    ImageButton btnGetImage, btnSave;
    TextView tvPageDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_page_layout);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        //xml연결
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btnGetImage = (ImageButton) findViewById(R.id.btnGetImage);
        tvPageDate = (TextView) findViewById(R.id.tvPageDate);
        btnSave = (ImageButton) findViewById(R.id.btnSave);

        //서버로 사진 정보 전송하기
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvPageDate.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        ReqServer.album.put("title", tvPageDate.getText().toString());
                        ReqServer.album.put("type", "customAlbum");
                        ReqServer.reqPostPages(AlbumPageActivity.this);
                    } catch (JSONException e) {
                        Log.e("AlbumPageActivity", "btnSave JSONException: " + e);
                    }
                }
            }
        });

        //인텐트
        Intent getDateIntent = getIntent();
        int iDay = getDateIntent.getIntExtra("SelectedDATE",-1);

        //화면 설정
        String title;   //제목 설정
        if (iDay == -1){    //마이앨범이거나 월별앨범일 경우
            if(getDateIntent.getBooleanExtra("customAlbum", false)){    //마이앨범
                title = getDateIntent.getStringExtra("title");
                ReqServer.stitle = title;
                tvPageDate.setText(title);
                tvPageDate.setEnabled(true);
            }
            else {  //월별 앨범
                title = getDateIntent.getStringExtra("title");
                ReqServer.stitle = title;
                tvPageDate.setText(title);
                tvPageDate.setEnabled(false);
            }
        }
        else {  //캘린더에서 날짜를 선택한 경우
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

        //레이아웃 적용
        recyclerView.setLayoutManager(manager);

        //기존에 저장된 사진들 불러오기
        ReqServer.reqGetPages(AlbumPageActivity.this);

        //이미지 가져오기 버튼
        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }

    //페이지 화면을 나갈 때 데이터 비워주기
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("AlbumGvAdapter", "파괴중: " + ReqServer.customAlbumList);
        ReqServer.photoList.clear();
        ReqServer.album.remove("title");
        ReqServer.album.remove("thumbnail");
        ReqServer.album.remove("type");
    }

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
                Log.d("clipData", String.valueOf(clipData.getItemCount()));

                for(int i=0; i< clipData.getItemCount(); i++){
                    Uri imageUri = clipData.getItemAt(i).getUri();  //선택한 이미지들의 uri를 가져온다.
                    resolver.takePersistableUriPermission(imageUri, takeFlags); //uri 권한 부여

                    setUriTags(imageUri);
                }
            }//이미지를 여러장 선택

        }//이미지 선택함
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String dateFormat(Calendar calendar, int day){  //2022년 5월

        //년월 포맷
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;

        String date = year + "년  " + month + "월  " + day + "일 ";

        return date;
    }

    //제목 설정
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setView(int day){
        tvPageDate.setText(dateFormat(CalendarUtil.selectedDate, day));
    }

    //어댑터 업데이트 해주기
    protected static void setAdapterUpdated(){
        //수정할 수 있을 듯
        adapter = new MultiImageAdapter(ReqServer.photoList, AlbumPageActivity.recyclerView.getContext());

        recyclerView.setAdapter(AlbumPageActivity.adapter);
    }

    //선택한 사진의 태그 가져오기
    protected void setUriTags(Uri imageUri){
        JSONObject imageInfo = new JSONObject();    //사진 한 장의 uri와 태그들을 담을 변수
        ArrayList<JSONObject> tagsIndex = new ArrayList<>();    //사진 한 장의 태그들
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
                                    //tagmap.json 파일 가져와서 변수에 저장
                                    InputStream inputS = getResources().openRawResource(R.raw.tagmap);
                                    byte[] buffer = new byte[inputS.available()];
                                    inputS.read(buffer);
                                    inputS.close();
                                    String json = new String(buffer, "UTF-8");
                                    JSONArray tagMap = new JSONArray(json);

                                    float confidence = labels.get(0).getConfidence();
                                    for (ImageLabel label : labels) {   //차이가 15% 이하인 태그 3개까지만 넣기
                                        Log.d("HWA", "label: " + label.getIndex() + " " + label.getText());
                                        if (tagsIndex.size() > 3) break;
                                        else if (confidence - label.getConfidence() > 0.15f)
                                            break;
                                        else tagsIndex.add(tagMap.getJSONObject(label.getIndex()));
                                    }
                                    imageInfo.put("tags", tagsIndex);   //태그들 넣기
                                }
                                ReqServer.photoList.add(imageInfo); //어뎁터에 들어갈 리스트에 사진 정보 넣기
                                setAdapterUpdated(); //어댑터 설정
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
            Log.e("AlbumPageActivity", "setUriTags Exception : " + e);
        }
    }
}