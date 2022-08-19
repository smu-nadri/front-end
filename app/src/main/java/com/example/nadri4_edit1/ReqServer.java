package com.example.nadri4_edit1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

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
    public static String sDate;
    Context c;

    public ReqServer(Context context) {
        c = context;
    }

    //페이지 정보 요청하기
    public static void reqGetPages(Context context, ArrayList<JSONObject> photoList){
        //android_id 가져와서 ip 주소랑 합치기
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.testIpAddress) + android_id + "/" + sDate;
        Log.d("HWA", "GET Url: " + url);

        //요청 만들기
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("HWA", "GET Response" + response);
                for(int i = 0; i< response.length(); i++){
                    try {
                        Log.d("HWA", "GET Response Uri: " + String.valueOf(response.getJSONObject(i)));
                        photoList.add(response.getJSONObject(i));
                    } catch (JSONException e) {
                        Log.e("HWA", "GET onResponse JSONException : " + e);
                    }
                }

                //AlbumLayout.adapter = new MultiImageAdapter(uriList, AlbumLayout.recyclerView.getContext());
                AlbumLayout.adapter = new MultiImageAdapter(photoList, AlbumLayout.recyclerView.getContext());

                //레이아웃 설정(열 = 2)
                RecyclerView.LayoutManager manager = new GridLayoutManager(AlbumLayout.recyclerView.getContext(), 2);
                //recyclerView.LayoutManager(new GridLayoutManager(this, 2));

                //레이아웃 적용
                AlbumLayout.recyclerView.setLayoutManager(manager);

                //어댑터 적용
                AlbumLayout.recyclerView.setAdapter(AlbumLayout.adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HWA", "GET Response 에러: " + error);
            }
        });

        //큐에 넣어 서버로 응답 전송
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    //작성한 페이지 보내기
    @SuppressLint("RestrictedApi")
    public static void reqPostPages(Context context, ArrayList<JSONObject> photoList, ArrayList<JSONObject> deletedList){
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = context.getString(R.string.testIpAddress) + android_id;
        Log.d("HWA", "POST Url: " + url);

        //서버로 보낼 Json
        JSONObject reqJson = new JSONObject();
        JSONArray reqJsonArr = new JSONArray();

        try{
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
                    if(photoList.get(i).has("comment"))
                        photoJson.put("comment", photoList.get(i).get("comment"));
                }

                //페이지 정보
                JSONObject pages = new JSONObject();
                pages.put("page", sDate);
                pages.put("pageOrder", 1); //여기 수정해야함
                pages.put("layoutOrder", i);
                photoJson.put("pages", pages);

                //리스트 reqJsonArr에 사진 photoJson 넣기
                Log.d("HWA", "photoJson " + i + ": " + photoJson);
                reqJsonArr.put(photoJson);
                /*
                String timeData = exif.getAttribute(ExifInterface.TAG_DATETIME);
                String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                Log.d("HWA", "timeData: "+ timeData + " lat:" + lat + " lng: " + lng);
                */
            }

            //추가 및 수정할 리스트 reqJson에 넣기
            reqJson.put("photos", reqJsonArr);
            Log.d("HWA", "reqJson: " + reqJson);
        } catch (Exception e) {
            Log.e("HWA", "POST 에러: " + e);
            Toast.makeText(context.getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, reqJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("HWA", "POST 응답: " + response);
                photoList.clear();
                deletedList.clear();
                try {
                    JSONArray resJsonArr = response.getJSONArray("resJson");
                    for(int i = 0; i < resJsonArr.length(); i++){
                        photoList.add(resJsonArr.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    Log.e("HWA", "POST onResponse JSONException :" + e);
                }
                Log.d("HWA", "POST 응답: " + photoList);
                Toast.makeText(context.getApplicationContext(), "전송 성공!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HWA", "POST Response 에러: " + error);
                Toast.makeText(context.getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}