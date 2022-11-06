package com.example.nadri4_edit1;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ReqServer {
    public static String android_id;

    //모든 사진
    static ArrayList<JSONObject> allList = new ArrayList<>();

    //모두보기, 하이라이트 썸네일
    static String allAlbumThumb = null;
    static String highAlbumThumb = null;

    //앨범 정보를 담는 리스트
    static ArrayList<JSONObject> dateAlbumList = new ArrayList<>();
    static ArrayList<JSONObject> customAlbumList = new ArrayList<>();
    static ArrayList<JSONObject> yearAlbumList = new ArrayList<>();
    static ArrayList<JSONObject> monthAlbumList = new ArrayList<>();

    //사진 정보를 담는 리스트
    static ArrayList<JSONObject> photoList = new ArrayList<>();
    static ArrayList<JSONObject> deletedList = new ArrayList<>();

    static
    JSONObject updateFace = new JSONObject();

    //태그 정보를 담는 리스트
    static ArrayList<JSONObject> tagList = new ArrayList<JSONObject>();
    static ArrayList<JSONObject> faceList = new ArrayList<JSONObject>();

    //검색 결과를 담는 리스트
    static ArrayList<JSONObject> sAlbumList = new ArrayList<>();
    static ArrayList<JSONObject> sPhotoList = new ArrayList<>();

    //해당 앨범 제목의 페이지 가져오기 위한 변수
    public static String stitle;

    //앨범 정보(앨범제목, 타입, 썸네일) 보내기 위한 변수
    public static JSONObject album = new JSONObject();

    //하이라이트 사진을 담는 리스트
    public static String highlightTitle = new String();
    public  static ArrayList<JSONObject> highlightList = new ArrayList<>();

    Context c;

    public ReqServer(Context context) {
        c = context;
    }

    //앨범 리스트 불러오기
    public static void reqGetAll(Context context){
        String url = context.getString(R.string.testIpAddress) + "/album/all/" + android_id;
        Log.d("GET", "reqGetAll Url: " + url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                Log.d("GET", "reqGetAlbums onResponse: " + response);
                try {
                    //앨범 리스트 초기화
                    allList.clear();

                    //각 앨범 정보를 배열리스트에 넣기
                    JSONArray resArr = response.getJSONArray("all");
                    for(int i = 0; i < resArr.length(); i++){
                        allList.add(resArr.getJSONObject(i));
                    }

                    AllPhotoActivity.aAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("GET", "reqGetAll onResponse 에러: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET", "reqGetAll Response 에러: " + error);
                Toast.makeText(context.getApplicationContext(), "응답 실패", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    //앨범 리스트 불러오기
    public static void reqGetAlbums(Context context, Integer mainCode){
        String url = context.getString(R.string.testIpAddress) + "/album/" + android_id;
        Log.d("GET", "reqGetAlbums Url: " + url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                Log.d("GET", "reqGetAlbums onResponse: " + response);
                try {
                    //앨범 리스트 초기화
                    dateAlbumList.clear();
                    customAlbumList.clear();
                    yearAlbumList.clear();
                    monthAlbumList.clear();
                    allAlbumThumb = null;

                    //각 앨범 정보를 배열리스트에 넣기
                    JSONArray resArr = response.getJSONArray("thumb");
                    for(int i = 0; i < resArr.length(); i++){
                        allAlbumThumb = resArr.getJSONObject(i).getString("uri");
                    }

                    resArr = response.getJSONArray("dateAlbums");
                    for(int i = 0; i < resArr.length(); i++){
                        dateAlbumList.add(resArr.getJSONObject(i).getJSONObject("_id"));
                    }

                    resArr = response.getJSONArray("customAlbums");
                    for(int i = 0; i < resArr.length(); i++){
                        customAlbumList.add(resArr.getJSONObject(i).getJSONObject("_id"));
                    }

                    resArr = response.getJSONArray("yearAlbums");
                    for(int i = 0; i < resArr.length(); i++){
                        yearAlbumList.add(resArr.getJSONObject(i));
                    }

                    resArr = response.getJSONArray("monthAlbums");
                    for(int i = 0; i < resArr.length(); i++){
                        monthAlbumList.add(resArr.getJSONObject(i));
                    }

                    //화면 설정
                    if(mainCode == 0){
                        if(CalendarMainActivity.adapter != null){
                            CalendarMainActivity.adapter.notifyDataSetChanged();
                        }
                    }
                    else if(mainCode == 1){
                        AlbumMainActivity.cAdapter.notifyDataSetChanged();
                        AlbumMainActivity.yAdapter.notifyDataSetChanged();
                        AlbumMainActivity.setAlbumMainViewVisibility();
                        if(allAlbumThumb != null) AlbumMainActivity.all_img.setImageURI(Uri.parse(allAlbumThumb));
                    }

                } catch (JSONException | SecurityException | NullPointerException e) {
                    Log.e("GET", "reqGetAlbums onResponse 에러: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET", "reqGetAlbums Response 에러: " + error);
                Toast.makeText(context.getApplicationContext(), "응답 실패", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    //페이지 정보 요청하기
    public static void reqGetPages(Context context){
        //android_id 가져와서 ip 주소랑 합치기
        String url = null;
        try {
            url = context.getString(R.string.testIpAddress) + "/album/"  + URLEncoder.encode(album.getString("title"),"utf-8") + "/" + album.getString("type") + "/" + android_id;
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("GET", "reqGetPages Url: " + url);

        //요청 만들기
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                photoList.clear();  //기존의 데이터 지우기
                for(int i = 0; i< response.length(); i++){
                    try {
                        Log.d("GET", "reqGetPages Response Uri: " + String.valueOf(response.getJSONObject(i)));
                        photoList.add(response.getJSONObject(i).put("isChecked", false));
                    } catch (JSONException e) {
                        Log.e("GET", "reqGetPages onResponse JSONException : " + e);
                    }
                }

                if(ReqServer.photoList.size() == 0){
                    AlbumPageActivity.btnEdit.setVisibility(View.GONE);
                    Log.d("size", "아이템사이즈" + ReqServer.photoList.size());
                }
                else {
                    AlbumPageActivity.btnGetImage.setVisibility(View.GONE);
                    AlbumPageActivity.btnEdit.setVisibility(View.VISIBLE);
                }

                //레이아웃 적용
                RecyclerView.LayoutManager manager = new GridLayoutManager(AlbumPageActivity.recyclerView.getContext(), 2);
                AlbumPageActivity.recyclerView.setLayoutManager(manager);

                //어댑터 적용
                AlbumPageActivity.adapter = new MultiImageAdapter(photoList, AlbumPageActivity.recyclerView.getContext());
                AlbumPageActivity.recyclerView.setAdapter(AlbumPageActivity.adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET", "reqGetPages Response 에러: " + error);
            }
        });

        //큐에 넣어 서버로 응답 전송
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    //작성한 페이지 보내기
    @SuppressLint("RestrictedApi")
    public static void reqPostPages(Context context, int code){
        String url = context.getString(R.string.testIpAddress) + "/album/" + android_id;
        Log.d("POST", "reqPostPages Url: " + url);

        //서버로 보낼 Json
        JSONObject reqJson = new JSONObject();
        JSONArray reqJsonArr = new JSONArray();

        try{
            //앨범 정보 reqJson에 넣기
            reqJson.put("album", album);

            //삭제할 리스트 reqJson에 넣기
            JSONArray reqDeleteArr = new JSONArray();
            for(int i = 0; i < deletedList.size(); i++) {
                reqDeleteArr.put(deletedList.get(i));
            }
            reqJson.put("deletedList", reqDeleteArr);

            //추가 및 수정할 사진 정보를 담은 photoJson 만들고 리스트 reqJsonArr에 넣기
            for(int i = 0; i < photoList.size(); i++){
                JSONObject photoJson = new JSONObject();
                photoJson.put("userId", android_id);

                if(photoList.get(i).has("_id"))
                    photoJson.put("_id", photoList.get(i).get("_id"));

                photoJson.put("uri", photoList.get(i).get("uri"));

                //날짜 정보
                if (photoList.get(i).has("datetime"))
                    photoJson.put("datetime", photoList.get(i).get("datetime"));


                //위치 정보
                if (photoList.get(i).has("location"))
                    photoJson.put("location", photoList.get(i).get("location"));


                //코멘트
                if(photoList.get(i).has("comment"))
                    photoJson.put("comment", photoList.get(i).get("comment"));

                //태그
                if(photoList.get(i).has("tags"))
                    photoJson.put("tags", photoList.get(i).get("tags"));

                //얼굴
                if(photoList.get(i).has("faces"))
                    photoJson.put("faces", photoList.get(i).get("faces"));


                //페이지 정보
                photoJson.put("layoutOrder", i);

                //리스트 reqJsonArr에 사진 photoJson 넣기
                Log.d("POST", "reqPostPages photoJson " + i + ": " + photoJson);
                reqJsonArr.put(photoJson);
            }

            //추가 및 수정할 리스트 reqJson에 넣기
            reqJson.put("photos", reqJsonArr);

        } catch (Exception e) {
            Log.e("POST", "reqPostPages Set reqJson 에러: " + e);
            Toast.makeText(context.getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, reqJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray resJsonArr = response.getJSONArray("resJson");
                    //기존의 데이터 지우기
                    photoList.clear();
                    deletedList.clear();

                    Log.d("POST", "reqPostPages onResponse 응답: " + resJsonArr);
                    Toast.makeText(context.getApplicationContext(), "전송 성공!", Toast.LENGTH_SHORT).show();

                    ReqServer.reqGetAlbums(context, code);//여기여기 달력앨범일때도 셋팅해줘..

                } catch (JSONException e) {
                    Log.e("POST", "POST onResponse JSONException :" + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("POST", "reqPostPages Response 에러: " + error);
                Toast.makeText(context.getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


    //검색화면 셋팅을 위한 태그 목록 가져오기
    public static void reqGetTagList(Context context){
        String url = context.getString(R.string.testIpAddress) + "/search/taglist/" + android_id;
        Log.d("GET", "reqGetTagList Url: " + url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                Log.d("GET", "reqGetTagList Response: " + response);
                try {
                    JSONArray resArr = response.getJSONArray("tagList");

                    tagList.clear();
                    for(int i = 0; i < resArr.length(); i++){
                        tagList.add(resArr.getJSONObject(i).getJSONObject("tag"));
                    }
                    TagGvAdapter gAdapter = new TagGvAdapter(context);
                    gAdapter.setItem(ReqServer.tagList);
                    SearchMainActivity.gvTagList.setAdapter(gAdapter);

                    resArr = response.getJSONArray("faceList");
                    faceList.clear();
                    for(int i = 0; i < resArr.length(); i++){
                        faceList.add(resArr.getJSONObject(i).getJSONObject("face"));
                    }
                    TagGvAdapter fAdapter = new TagGvAdapter(context);
                    fAdapter.setItem(ReqServer.faceList);
                    SearchMainActivity.gvFaceList.setAdapter(fAdapter);

                } catch (JSONException e) {
                    Log.e("GET", "reqGetTagList onResponse 에러: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET", "reqGetTagList Response 에러: " + error);
                Toast.makeText(context.getApplicationContext(), "응답 실패", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    //태그 페이지 정보 요청하기
    public static void reqGetTagPage(Context context, String type, String target){
        //android_id 가져와서 ip 주소랑 합치기
        String url = context.getString(R.string.testIpAddress) + "/search/" + type + "/" + target + "/" + android_id;
        Log.d("GET", "reqGetTagPage Url: " + url);

        //요청 만들기
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("GET", "reqGetTagPage Response" + response);
                photoList.clear();
                for(int i = 0; i< response.length(); i++){
                    try {
                        Log.d("GET", "reqGetTagPage Response Uri: " + String.valueOf(response.getJSONObject(i)));
                        photoList.add(response.getJSONObject(i));
                    } catch (JSONException e) {
                        Log.e("GET", "reqGetTagPage onResponse JSONException : " + e);
                    }
                }

                SearchPageActivity.adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET", "reqGetTagPage Response 에러: " + error);
            }
        });

        //큐에 넣어 서버로 응답 전송
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    //검색하기
    public static void reqGetQuery(Context context, String query){
        String url = context.getString(R.string.testIpAddress) + "/search/" + android_id + "?query=" + query;
        Log.d("GET", "reqGetQuery Url: " + url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                Log.d("GET", "reqGetQuery Response: " + response);
                try {
                    JSONArray resArr = response.getJSONArray("albumResult");
                    sAlbumList.clear();
                    SearchResultActivity.resultAlbum.setVisibility(View.GONE);  //없으면 표시 안하기
                    for(int i = 0; i < resArr.length(); i++){
                        sAlbumList.add(resArr.getJSONObject(i));
                        SearchResultActivity.resultAlbum.setVisibility(View.VISIBLE);   //있으면 표시
                    }

                    resArr = response.getJSONArray("photoResult");
                    sPhotoList.clear();
                    SearchResultActivity.resultPhoto.setVisibility(View.GONE);  //없으면 표시 안하기
                    for(int i = 0; i < resArr.length(); i++){
                        sPhotoList.add(resArr.getJSONObject(i));
                        SearchResultActivity.resultPhoto.setVisibility(View.VISIBLE);   //있으면 표시
                    }

                    //화면 설정
                    AlbumGvAdapter aAdapter = new AlbumGvAdapter(context);
                    aAdapter.setItem(ReqServer.sAlbumList);
                    SearchResultActivity.gvResultAlbum.setAdapter(aAdapter);

                    PhotoGvAdapter gAdapter = new PhotoGvAdapter(context);
                    gAdapter.setItem(ReqServer.sPhotoList);
                    SearchResultActivity.gvResultPhoto.setAdapter(gAdapter);
                } catch (JSONException e) {
                    Log.e("GET", "reqGetQuery onResponse 에러: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET", "reqGetQuery Response 에러: " + error);
                Toast.makeText(context.getApplicationContext(), "응답 실패", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    //하이라이트 가져오기
    public static void reqGetHighlight(Context context){
        //android_id 가져와서 ip 주소랑 합치기
        String url = context.getString(R.string.testIpAddress) + "/highlight/" + android_id;
        Log.d("GET", "reqGetHighlight Url: " + url);

        //요청 만들기
        final JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                highlightTitle = "";
                highlightList.clear();  //기존의 데이터 지우기

                try {
                    highlightTitle = response.getString("title");
                    JSONArray resArr = response.getJSONArray("resArr");

                    Log.d("GET", "reqGetHighlight Response: title - " + highlightTitle);
                    for(int i = 0; i< resArr.length(); i++){
                        try {
                            Log.d("GET", "reqGetHighlight Response: " + String.valueOf(resArr.getJSONObject(i)));
                            highlightList.add(resArr.getJSONObject(i));
                        } catch (JSONException e) {
                            Log.e("GET", "reqGetHighlight onResponse JSONException : " + e);
                        }
                    }
                    if(!highlightList.isEmpty()){   //하이라이트가 있으면
                        //AlbumMainActivity.highlight_tv.setText(highlightTitle);
                        AlbumMainActivity.highlight_img.setImageURI(Uri.parse(highlightList.get(0).getString("uri")));

                        //알림 설정
                        Intent intent = new Intent(context, CalendarMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, InitApplication.HIGHLIGHT_NOTIFICATION_CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_nadri_notice)
                                .setContentTitle("오늘의 하이라이트!")
                                .setContentText("하이라이트가 도착했어요! 확인해보실래요?")
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        Notification notification = builder.build();

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(InitApplication.HIGHLIGHT_NOTIFICATION_TAG, InitApplication.HIGHLIGHT, notification);

                        //포그라운드일 때
                        AlbumMainActivity.highlight_album.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET", "reqGetHighlight Response 에러: " + error);
            }
        });

        //큐에 넣어 서버로 응답 전송
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    //작성한 페이지 보내기
    @SuppressLint("RestrictedApi")
    public static void reqUpdateFace(Context context){
        String url = context.getString(R.string.testIpAddress) + "/album/face/" + android_id;
        Log.d("POST", "reqPostPages Url: " + url);

        //서버로 보낼 Json
        JSONObject reqJson = new JSONObject();

        try{
            reqJson.put("face", updateFace);

        } catch (Exception e) {
            Log.e("POST", "reqUpdateFace Set reqJson 에러: " + e);
            Toast.makeText(context.getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, reqJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray resJsonArr = response.getJSONArray("resJson");
                    //기존의 데이터 지우기
                    updateFace.remove("face");

                    Toast.makeText(context.getApplicationContext(), "전송 성공!", Toast.LENGTH_SHORT).show();

                    ReqServer.reqGetPages(context.getApplicationContext());
                } catch (JSONException e) {
                    Log.e("POST", "POST onResponse JSONException :" + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("POST", "reqUpdateFace Response 에러: " + error);
                Toast.makeText(context.getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}