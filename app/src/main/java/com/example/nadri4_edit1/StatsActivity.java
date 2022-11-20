package com.example.nadri4_edit1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.nadri4_edit1.StatsFragment1.*;
import static com.example.nadri4_edit1.StatsFragment2.*;
import static com.example.nadri4_edit1.StatsFragment3.*;

public class StatsActivity extends AppCompatActivity {

    final int YEAR = 0;
    final int MONTH = 1;
    final int DAY = 2;

    ViewPager2 pager;
    FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        pager = findViewById(R.id.statsPager);
        pagerAdapter = new StatsPagerAdapter(this);
        pager.setAdapter(pagerAdapter);


        reqGetStats(this);
        
    }

    synchronized void initChart(int code, BarChart bChart, BarDataSet bDataSet, BarData bData, ArrayList<BarEntry> bList){
        bChart.getDescription().setEnabled(false);    //"Description Chart" 지우기
        bChart.getLegend().setEnabled(false); //레이블 설명 지우기
        bChart.setScaleEnabled(false);

        bDataSet.setColor(getResources().getColor(R.color.chart_bar));
        bDataSet.setValueTextSize(10f);

        bData.setValueTextSize(10f);
        bData.addDataSet(bDataSet);
        bData.setBarWidth(0.4f);
        bData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String sValue = String.valueOf((int)value);
                return sValue;
            }
        });

        bChart.setData(bData);

        XAxis xAxis = bChart.getXAxis();  //x축 가져오기
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  //위치
        xAxis.setDrawGridLines(false);  //격자무늬 없애기
        xAxis.setLabelCount(bList.size());  //x축 개수
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        if(code == YEAR) {
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    String sValue = (int) value + "년";
                    return sValue;
                }
            });
        }
        else if(code == MONTH){
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    String sValue = (int) value + "월";
                    return sValue;
                }
            });
        }
        else if(code == DAY){
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    String sValue = "";
                    switch ((int) value){
                        case 1: sValue = "일"; break;
                        case 2: sValue = "월"; break;
                        case 3: sValue = "화"; break;
                        case 4: sValue = "수"; break;
                        case 5: sValue = "목"; break;
                        case 6: sValue = "금"; break;
                        case 7: sValue = "토"; break;
                    }
                    return sValue;
                }
            });
        }
        YAxis yAxis = bChart.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setGranularity(1f);   //간격
        yAxis.setAxisMinimum(0f);

        //오른쪽 축 값 삭제
        YAxis rAxis = bChart.getAxisRight();
        rAxis.setDrawLabels(false);
        rAxis.setDrawAxisLine(false);
        rAxis.setDrawGridLines(false);
    }


    private void updateChart(BarDataSet bDataSet, BarData bData, BarChart bChart) {
        bDataSet.notifyDataSetChanged();
        bData.notifyDataChanged();
        bChart.notifyDataSetChanged();
        bChart.invalidate();
    }

    synchronized public void reqGetStats(Context context){
        //android_id 가져와서 ip 주소랑 합치기
        String url = context.getString(R.string.testIpAddress) + "/stats/" + ReqServer.android_id;
        Log.d("GET", "reqGetStats Url: " + url);

        //요청 만들기
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //기존의 데이터 지우기
                yearCnt.clear(); monthCnt.clear(); dayCnt.clear(); tagCnt.clear(); localityCnt.clear(); thoroughfareCnt.clear(); faceCnt.clear();
                yList.clear(); mList.clear(); dList.clear(); tList.clear(); lList.clear(); tfList.clear(); fList.clear();

                try {
                    JSONArray resArr = response.getJSONArray("yearCnt");
                    for(int i = 0; i< resArr.length(); i++){
                        int year = resArr.getJSONObject(i).getInt("year");
                        int count = resArr.getJSONObject(i).getInt("count");
                        yearCnt.add(resArr.getJSONObject(i));
                        yList.add(new BarEntry(year, count));
                    }


                    resArr = response.getJSONArray("monthCnt");
                    for(int i = 0; i< resArr.length(); i++){
                        monthCnt.add(resArr.getJSONObject(i));
                    }
                    if(monthCnt.size() != 0) {
                        mTextView.setText(monthCnt.get(0).getString("year") + "년");
                        JSONObject m = monthCnt.get(0).getJSONObject("month");
                        for (int i = 1; i < 13; i++) {
                            String k = String.valueOf(i);
                            mList.add(new BarEntry(i, m.getInt(k)));
                        }
                    }


                    resArr = response.getJSONArray("dayCnt");
                    for(int i = 0; i< resArr.length(); i++){
                        dayCnt.add(resArr.getJSONObject(i));
                    }
                    if(dayCnt.size() != 0) {
                        //dTextView.setText(dayCnt.get(0).getString("year") + "년");
                        JSONObject d = dayCnt.get(0).getJSONObject("dayofweek");
                        for (int i = 1; i < 8; i++) {
                            String k = String.valueOf(i);
                            dList.add(new BarEntry(i, d.getInt(k)));
                        }
                    }


                    resArr = response.getJSONArray("localityCnt");
                    for(int i = 0; i< resArr.length(); i++){
                        int count = resArr.getJSONObject(i).getInt("count");
                        localityCnt.add(resArr.getJSONObject(i));
                        lList.add(new BarEntry(i, count));
                    }


                    resArr = response.getJSONArray("thoroughfareCnt");
                    for(int i = 0; i< resArr.length(); i++){
                        thoroughfareCnt.add(resArr.getJSONObject(i));
                    }


                    resArr = response.getJSONArray("tagCnt");
                    for(int i = 0; i< resArr.length(); i++){
                        int count = resArr.getJSONObject(i).getInt("count");
                        tagCnt.add(resArr.getJSONObject(i));
                        tList.add(new BarEntry(i, count));
                    }



                    resArr = response.getJSONArray("faceCnt");
                    for(int i = 0; i< resArr.length(); i++){
                        int count = resArr.getJSONObject(i).getInt("count");
                        faceCnt.add(resArr.getJSONObject(i));
                        fList.add(new BarEntry(i, count));
                    }

                    //업데이트 함수
                    updateChart(yDataSet, yData, yChart);
                    updateChart(mDataSet, mData, mChart);
                    updateChart(dDataSet, dData, dChart);

                    initChart(YEAR, yChart, yDataSet, yData, yList);
                    initChart(MONTH, mChart, mDataSet, mData, mList);
                    initChart(DAY, dChart, dDataSet, dData, dList);

                } catch (JSONException e) {
                    Log.e("GET", "reqGetStats onResponse JSONException : " + e);
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
        requestQueue.add(jsonObjectRequest);
    }

}
