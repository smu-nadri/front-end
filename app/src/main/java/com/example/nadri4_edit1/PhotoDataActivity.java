package com.example.nadri4_edit1;

import static com.example.nadri4_edit1.InitApplication.faceMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PhotoDataActivity extends AppCompatActivity {
    static MultiImageAdapter adapter;

    FrameLayout photoLayout;
    ImageView photo_big;
    View photo_fore;
    TextView photo_text;

    ImageButton btnGetImage, btnSave;
    TextView tvPageDate;

    private final String localFaceList = "LocalListofFace.tmp";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_data_layout);

        //xml연결
        photoLayout = findViewById(R.id.photoLayout);
        photo_big = findViewById(R.id.imgView);
        photo_fore = findViewById(R.id.photo_fore);
        photo_text = findViewById(R.id.photo_text);

        //xml연결
        btnGetImage = (ImageButton) findViewById(R.id.btnGetImage);
        tvPageDate = (TextView) findViewById(R.id.tvPageDate);
        btnSave = (ImageButton) findViewById(R.id.btnSave);


        //인텐트 - 사진메타정보 통으로 가져옴
        Intent intent = getIntent();
        Integer photo_data = intent.getIntExtra("photo_data", -1);

        String title = null;
        String uri = null;
        Object datetime = null;
        String location = null;
        JSONArray tags_arr;
        String tags_str = "";
        String comment = null;

        Log.d("PHOTO ", String.valueOf(photo_data));

        //사진 정보 가져와서 setText&append
        //+) UI 정리하고싶은것 : 사진사이즈조절.......
        try {
            JSONObject photo_data_json = ReqServer.photoList.get(photo_data);
            photo_text.setText("사진 정보\n\n");

            //달력앨범 타이틀 포맷 맞춰야함!!
            if(photo_data_json.has("album")) {
                title = photo_data_json.getJSONObject("album").getString("title");
                tvPageDate.setText(title);
            }

            uri = photo_data_json.getString("uri");
            //photo_big.setImageResource(Uri.parseUri(uri));
            Glide.with(this).load(Uri.parse(uri)).into(photo_big);

            if(photo_data_json.has("datetime")) {
                Object time = photo_data_json.get("datetime");

                if(time.getClass() == String.class) {
                    datetime = ((String) time).substring(0, 10) + " " + ((String) time).substring(11, 19);
                    photo_text.append(" - 날짜 : " + datetime + "\n");
                }
                else if(time.getClass() == Long.class) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
                    calendar.setTimeInMillis((Long) time);
                    datetime = dateFormat.format(calendar.getTime());
                    photo_text.append(" - 날짜 : " + datetime + "\n");
                }
            }

            //한글(location-address)만 가져오도록 함
            //location = photo_data_json.getString("location"); //-> 좌표까지 가져옴
            if(photo_data_json.has("location")) {
                location = photo_data_json.getJSONObject("location").getString("address");
                photo_text.append(" - 위치 : " + location + "\n");
            }

            //array로 저장돼있어서 어케 불러와야할지 모르겠음
            tags_arr = photo_data_json.getJSONArray("tags");
                    //{ _id, tag_en, tag_ko1, tag_ko2 }
            for(int i=0; i<tags_arr.length(); i++){
                tags_str = tags_str + "#" + tags_arr.getJSONObject(i).getString("tag_ko1") + " ";
                //인덱스 진짜 어케..
            } //-> location처럼 해볼라했는데 안 되네..
            photo_text.append(" - 태그 : " + tags_str + "\n");

            //DB의 comment에 저장된 내용이 하나도 없을 땐 오류나는듯?
            //DB comment에 "#태그"는 저장하지 않도록 할 수 있나?(태그까지 comment로 저장돼서 출력할 때 중복됨) 아님 append할 때 처리해야하는디 어케하지
            //comment의 개행을 잘 처리해야 깔끔할듯
            comment = photo_data_json.getString("comment");
            photo_text.append(" - 내용 : "+comment+"\n");

            if(photo_data_json.has("faces")) {
                JSONArray faces = photo_data_json.getJSONArray("faces");

                for(int i = 0; i < faces.length(); i++){
                    JSONObject face = faces.getJSONObject(i);
                    String name = face.getString("name");
                    final int idx = i;
                    String finalUri = uri;

                    TextView tv = new TextView(this);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 50);
                    params.setMargins(0, i*50, 0, 0);
                    tv.setBackgroundColor(Color.BLACK);
                    tv.setTextColor(Color.WHITE);
                    tv.setLayoutParams(params);

                    tv.setText(name);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.P)
                        @Override
                        public void onClick(View view) {    //이름태그를 클릭하면
                            View dialogView = (View) View.inflate(PhotoDataActivity.this, R.layout.face_edit_dialog, null);
                            AlertDialog.Builder dlg = new AlertDialog.Builder(PhotoDataActivity.this);
                            ImageView faceImg = (ImageView) dialogView.findViewById(R.id.faceImg);
                            EditText faceName = (EditText) dialogView.findViewById(R.id.faceName);

                            try {   //얼굴사진 잘라서 셋팅
                                Bitmap cropImg = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), Uri.parse(finalUri)));
                                cropImg = Bitmap.createBitmap(cropImg, face.getInt("left"), face.getInt("top"), face.getInt("width"), face.getInt("height"));
                                faceImg.setImageBitmap(cropImg);
                            } catch (JSONException | IOException e) {
                                Log.e("HWA", "어라?" + e);
                            }

                            //대화상자 설정
                            dlg.setTitle("이름 설정");
                            dlg.setView(dialogView);
                            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() { //확인 버튼 누르면
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        //이름 변경
                                        String name = faceName.getText().toString();
                                        face.put("name", name);
                                        tv.setText(name);

                                        String faceId = faces.getJSONObject(idx).getString("faceId");

                                        enrollMap(faceId, name);

                                        //다른 사진들도 변경하기 위해 서버로 변경할 이름 전송 (8, 이름)
                                        ReqServer.updateFace = face;
                                        ReqServer.reqUpdateFace(getApplicationContext());
                                    } catch (JSONException e) {
                                        Log.e("HWA", "아악 " + idx + " " + e);
                                    }
                                }
                            });
                            dlg.setNegativeButton("취소", null);
                            dlg.show();
                        }
                    });

                    //화면에 이름태그 추가
                    photoLayout.addView(tv);
                }
            }
        } catch (JSONException e) {
            Log.e("검사 ", title + ", " + e.toString());
        }




        //보여줄 사진 선택


        //사진 정보 가져오기



        // 클릭하면 사진 정보 보여주기
        photo_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(photo_fore.getVisibility() == View.INVISIBLE){
                    photo_fore.setVisibility(View.VISIBLE);
                    photo_text.setVisibility(View.VISIBLE);
                }
                else{
                    photo_fore.setVisibility(View.INVISIBLE);
                    photo_text.setVisibility(View.INVISIBLE);
                }
            }
        });


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

    void enrollMap(String uploadDate, String namePerson){
        // This app has list saved in local storage so every picture should be saved
        // with key (uploadDate, and it will be the file's name)
        // and name (namePerson).
        faceMap.put(uploadDate, namePerson);
        Log.d("FACEMAP", uploadDate +" "+ namePerson);
        try {
            FileOutputStream fos = openFileOutput(localFaceList, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(faceMap);
            oos.close();

        } catch (FileNotFoundException e) {
            Log.e("HWA", e.toString());
        } catch (IOException e) {
            Log.e("HWA", e.toString());
        }
        for (String key: faceMap.keySet()) {
            Log.d("FORMOON", key + " " + faceMap.get(key));

        }
    }
}
