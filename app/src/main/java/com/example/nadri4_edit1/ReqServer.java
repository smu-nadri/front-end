package com.example.nadri4_edit1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReqServer {

    //앨범 정보를 담는 리스트
    static ArrayList<JSONObject> dateAlbumList = new ArrayList<>();
    static ArrayList<JSONObject> customAlbumList = new ArrayList<>();
    static ArrayList<JSONObject> yearAlbumList = new ArrayList<>();
    static ArrayList<JSONObject> monthAlbumList = new ArrayList<>();

    //사진 정보를 담는 리스트
    static ArrayList<JSONObject> photoList = new ArrayList<>();
    static ArrayList<JSONObject> deletedList = new ArrayList<>();

    //태그 정보를 담는 리스트
    static ArrayList<JSONObject> tagList = new ArrayList<JSONObject>();

    //검색 결과를 담는 리스트
    static ArrayList<JSONObject> sAlbumList = new ArrayList<>();
    static ArrayList<JSONObject> sPhotoList = new ArrayList<>();

    //해당 앨범 제목의 페이지 가져올 때 쓰는 변수
    public static String stitle;

    //앨범 정보(앨범제목, 타입, 썸네일)
    public static JSONObject album = new JSONObject();


    Context c;

    public ReqServer(Context context) {
        c = context;
    }

    //앨범 리스트 불러오기
    public static void reqGetAlbums(Context context){
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.testIpAddress) + android_id;
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

                    //각 앨범 정보를 배열리스트에 넣기
                    JSONArray resArr = response.getJSONArray("dateAlbums");
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

                    //달력 화면 설정
                    CalendarMainActivity.setMonthView();

                } catch (JSONException e) {
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
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.testIpAddress) + android_id + "/" + stitle;
        Log.d("GET", "reqGetPages Url: " + url);

        //요청 만들기
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                photoList.clear();  //기존의 데이터 지우기
                for(int i = 0; i< response.length(); i++){
                    try {
                        Log.d("GET", "reqGetPages Response Uri: " + String.valueOf(response.getJSONObject(i)));
                        photoList.add(response.getJSONObject(i));
                    } catch (JSONException e) {
                        Log.e("GET", "reqGetPages onResponse JSONException : " + e);
                    }
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
    public static void reqPostPages(Context context){
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.testIpAddress) + android_id  + "/" + stitle;
        Log.d("POST", "reqPostPages Url: " + url);

        //서버로 보낼 Json
        JSONObject reqJson = new JSONObject();
        JSONArray reqJsonArr = new JSONArray();

        try{
            //앨범 정보 reqJson에 넣기
            reqJson.put("album", album);

            //삭제할 리스트 reqJson에 넣기
            reqJson.put("deletedList", deletedList);

            //추가 및 수정할 사진 정보를 담은 photoJson 만들고 리스트 reqJsonArr에 넣기
            for(int i = 0; i < photoList.size(); i++){
                JSONObject photoJson = new JSONObject();
                //수정할 경우
                if(photoList.get(i).has("_id")) {
                    photoJson.put("_id", photoList.get(i).get("_id"));
                    photoJson.put("uri", photoList.get(i).get("uri"));
                    photoJson.put("datetime", photoList.get(i).get("datetime"));
                    if(photoList.get(i).has("location"))
                        photoJson.put("location", photoList.get(i).get("location"));
                    if(photoList.get(i).has("comment"))
                        photoJson.put("comment", photoList.get(i).get("comment"));
                    if(photoList.get(i).has("tag"))
                        photoJson.put("tag", photoList.get(i).get("tag"));
                }
                else{ //추가할 경우
                    photoJson.put("uri", photoList.get(i).get("uri"));

                    //사진 정보 가져오기
                    InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(photoList.get(i).getString("uri")));
                    ExifInterface exif = new ExifInterface(inputStream);

                    //날짜 정보
                    Long datetime = exif.getDateTime();
                    photoJson.put("datetime", datetime);

                    //위치 정보
                    double latLong[] = exif.getLatLong();
                    //경도, 위도, 주소를 담을 Json
                    JSONObject location = new JSONObject();
                    if (latLong != null) {
                        Geocoder gCoder = new Geocoder(context);
                        List<Address> addressList = gCoder.getFromLocation(latLong[0], latLong[1], 10);
                        location.put("lat", latLong[0]);
                        location.put("long", latLong[1]);
                        if (!addressList.isEmpty())
                            location.put("address", addressList.get(0).getAddressLine(0));

                        photoJson.put("location", location);
                    }

                    //코멘트
                    if(photoList.get(i).has("comment"))
                        photoJson.put("comment", photoList.get(i).get("comment"));

                    //태그
                    if(photoList.get(i).has("tags"))
                        photoJson.put("tags", new JSONArray(photoList.get(i).getString("tags")));
                }

                //페이지 정보
                JSONObject pages = new JSONObject();
                pages.put("page", stitle);
                pages.put("pageOrder", 1); //여기 수정해야함
                pages.put("layoutOrder", i);
                photoJson.put("pages", pages);

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

                    for(int i = 0; i < resJsonArr.length(); i++){
                        photoList.add(resJsonArr.getJSONObject(i));
                    }

                    Log.d("HWA", "POST 응답: " + photoList);
                    Toast.makeText(context.getApplicationContext(), "전송 성공!", Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


    //검색화면 셋팅을 위한 태그 목록 가져오기
    public static void reqGetTagList(Context context){
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.testIpAddress) + android_id  + "/search/taglist";
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
                    SearchMainActivity.gridView.setAdapter(gAdapter);

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
    public static void reqGetTagPage(Context context, Integer tagIndex){
        //android_id 가져와서 ip 주소랑 합치기
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.testIpAddress) + android_id + "/search/" + tagIndex;
        Log.d("GETA", "reqGetTagPage Url: " + url);

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

                SearchPageActivity.adapter = new MultiImageAdapter(photoList, SearchPageActivity.recyclerView.getContext());
                SearchPageActivity.recyclerView.setAdapter(SearchPageActivity.adapter);
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
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.testIpAddress) + android_id  + "/search?query=" + query;
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
                        sPhotoList.add(resArr.getJSONObject(i));
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
}