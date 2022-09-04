package com.example.nadri4_edit1;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumMainActivity extends AppCompatActivity {

    ImageView folder1, folder2, folder3;
    ImageButton imgbtn_calendar, imgbtn_search_a, btnGetImage;
    GridLayout glNadriAlbum;
    GridView  gvCustomAlbum, gvYearAlbum;
    LinearLayout nadriAlbum, customAlbum, dateAlbum;

    String src;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_main_layout);

        //xml변수 연결
        folder1 = (ImageView) findViewById(R.id.imageView1);
        folder2 = (ImageView) findViewById(R.id.imageView2);
        folder3 = (ImageView) findViewById(R.id.imageView3);
        gvCustomAlbum = (GridView) findViewById(R.id.gvCustomAlbum);
        gvYearAlbum = (GridView) findViewById(R.id.gvYearAlbum);
        imgbtn_calendar = (ImageButton) findViewById(R.id.calendar_button); //캘린더로 이동하는 버튼
        imgbtn_search_a = (ImageButton) findViewById(R.id.a_search_button); //검색으로 이동하는 버튼
        btnGetImage = (ImageButton) findViewById(R.id.btnGetImage);
        glNadriAlbum = (GridLayout) findViewById(R.id.glNadriAlbum);
        nadriAlbum = (LinearLayout) findViewById(R.id.nadriAlbum);
        customAlbum = (LinearLayout) findViewById(R.id.customAlbum);
        dateAlbum = (LinearLayout) findViewById(R.id.dateAlbum);

        //사진 라운드 처리
        folder1.setClipToOutline(true);
        folder2.setClipToOutline(true);
        folder3.setClipToOutline(true);

        //앨범리스트가 없으면 GONE으로 변경
        if(ReqServer.customAlbumList.isEmpty()) {
            customAlbum.setVisibility(View.GONE);
            if(ReqServer.dateAlbumList.isEmpty()){
                nadriAlbum.setVisibility(View.GONE);
                dateAlbum.setVisibility(View.GONE);
            }
            else {
                nadriAlbum.setVisibility(View.VISIBLE);
                dateAlbum.setVisibility(View.VISIBLE);
            }
        }
        else {
            customAlbum.setVisibility(View.VISIBLE);
        }

        //이미지버튼(앨범버튼) 이벤트 -> 앨범뷰로 이동
        imgbtn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), CalendarMainActivity.class);
                startActivity(viewIntent);
            }
        });

        //이미지버튼(검색버튼) 이벤트 -> 검색뷰로 이동
        imgbtn_search_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getApplicationContext(), SearchMainActivity.class);
                startActivity(viewIntent);
            }
        });

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

        //처음 화면 셋팅
        AlbumGvAdapter cAdapter = new AlbumGvAdapter(this);
        cAdapter.setItem(ReqServer.customAlbumList);
        gvCustomAlbum.setAdapter(cAdapter);

        AlbumGvAdapter yAdapter = new AlbumGvAdapter(this);
        yAdapter.setItem(ReqServer.yearAlbumList);
        gvYearAlbum.setAdapter(yAdapter);

    }

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

            Intent viewIntent= new Intent(getApplicationContext(), AlbumPageActivity.class);
            viewIntent.putExtra("customAlbum", true);
            startActivity(viewIntent);
        }//이미지 선택함
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
                                AlbumPageActivity.setAdapterUpdated();
                            } catch(Exception e) {
                                ReqServer.photoList.add(imageInfo);
                                AlbumPageActivity.setAdapterUpdated();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() { //중간에 에러나면 uri만 넣기
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ReqServer.photoList.add(imageInfo);
                            AlbumPageActivity.setAdapterUpdated();
                        }
                    });
        } catch (Exception e) {
            Log.e("AlbumPageActivity", "setUriTags Exception : " + e);
        }
    }
}
