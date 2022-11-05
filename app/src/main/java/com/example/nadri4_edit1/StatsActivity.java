package com.example.nadri4_edit1;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class StatsActivity extends AppCompatActivity {

    public static BarChart yChart, mChart, dChart, tChart, lChart, tfChart, fChart;
    public static BarDataSet yDataSet, mDataSet, dDataSet, tDataSet, lDataSet, tfDataSet, fDataSet;
    public static BarData yData, mData, dData, tData, lData, tfData, fData;

    //데이터셋에 넣을 리스트
    public static ArrayList<BarEntry> yList = new ArrayList<>(), mList = new ArrayList<>(), dList = new ArrayList<>(),
            tList = new ArrayList<>(), lList = new ArrayList<>(), tfList = new ArrayList<>(), fList = new ArrayList<>();

    //서버에서 받아온 값
    public static ArrayList<JSONObject> yearCnt = new ArrayList<>();
    public static ArrayList<JSONObject> monthCnt = new ArrayList<>();
    public static ArrayList<JSONObject> dayCnt = new ArrayList<>();
    public static ArrayList<JSONObject> tagCnt = new ArrayList<>();
    public static ArrayList<JSONObject> localityCnt = new ArrayList<>();
    public static ArrayList<JSONObject> thoroughfareCnt = new ArrayList<>();
    public static ArrayList<JSONObject> faceCnt = new ArrayList<>();

    TextView mTextView, tfTextView; //dTextView,
    LinearLayout tfTvBtn, mTvBtn;
    int mIdx = 0, dIdx = 0, tfIdx = 0;

    final int YEAR = 0;
    final int MONTH = 1;
    final int DAY = 2;
    final int TAG = 3;
    final int LOCALITY = 4;
    final int THOROUGHFARE = 5;
    final int FACE = 6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        yChart = findViewById(R.id.yearChart);
        mChart = findViewById(R.id.monthChart);
        dChart = findViewById(R.id.dayChart);
        tChart = findViewById(R.id.tagChart);
        lChart = findViewById(R.id.localChart);
        tfChart = findViewById(R.id.tfChart);
        fChart = findViewById(R.id.faceChart);

        yDataSet = new BarDataSet(yList, "_year");
        yData = new BarData();

        mDataSet = new BarDataSet(mList, "_month");
        mData = new BarData();

        dDataSet = new BarDataSet(dList, "_dayOfWeek");
        dData = new BarData();

        tDataSet = new BarDataSet(tList, "_tag");
        tData = new BarData();

        lDataSet = new BarDataSet(lList, "_locality");
        lData = new BarData();

        tfDataSet = new BarDataSet(tfList, "_thoroughfare");
        tfData = new BarData();

        fDataSet = new BarDataSet(fList, "_face");
        fData = new BarData();



        mTextView = findViewById(R.id.mTextView);
        mTvBtn = findViewById(R.id.mTvBtn);
        mTvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mIdx = (mIdx+1) % monthCnt.size();
                    String y = monthCnt.get(mIdx).getString("year") + "년";
                    mTextView.setText(y);

                    mList.clear();
                    JSONObject m = monthCnt.get(mIdx).getJSONObject("month");
                    for(int i = 1; i < 13; i++){
                        String k = String.valueOf(i);
                        mList.add(new BarEntry(i, m.getInt(k)));
                    }

                    updateChart(mDataSet, mData, mChart);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //dTextView = findViewById(R.id.dTextView);
        /*dTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dIdx = (dIdx+1) % dayCnt.size();
                    String y = dayCnt.get(dIdx).getString("year") + "년";
                    dTextView.setText(y);

                    dList.clear();
                    JSONObject d = dayCnt.get(dIdx).getJSONObject("dayofweek");
                    for(int i = 1; i < 8; i++){
                        String k = String.valueOf(i);
                        dList.add(new BarEntry(i, d.getInt(k)));
                    }

                    updateChart(dDataSet, dData, dChart);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

         */

        tfTextView = findViewById(R.id.tfTextView);
        tfTvBtn = findViewById(R.id.tfTvBtn);
        tfTvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    tfIdx = (tfIdx + 1) % thoroughfareCnt.size();
                    String l = thoroughfareCnt.get(tfIdx).getString("locality");
                    tfTextView.setText(l);

                    tfList.clear();
                    JSONArray tf = thoroughfareCnt.get(tfIdx).getJSONArray("thoroughfares");
                    for(int i = 0; i < tf.length(); i++){
                        JSONObject t = tf.getJSONObject(i);
                        tfList.add(new BarEntry(i, t.getInt("count")));
                    }

                    updateChart(tfDataSet, tfData, tfChart);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        //차트 데이터 클릭 이벤트
        /*lChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

         */


        reqGetStats(this);


        
    }

    synchronized void initChart(int code, BarChart bChart, BarDataSet bDataSet, BarData bData, ArrayList<BarEntry> bList){
        bChart.getDescription().setEnabled(false);    //"Description Chart" 지우기
        bChart.getLegend().setEnabled(false); //레이블 설명 지우기

        bData.addDataSet(bDataSet);
        bData.setBarWidth(0.2f);
        bData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String sValue = (int)value + "개";
                return sValue;
            }
        });

        bChart.setData(bData);

        XAxis xAxis = bChart.getXAxis();  //x축 가져오기
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  //위치
        xAxis.setDrawGridLines(false);  //격자무늬 없애기
        xAxis.setLabelCount(bList.size());  //x축 개수
        xAxis.setGranularity(1f);
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
        else if(code == TAG){
            bChart.setVisibleXRangeMaximum(10f);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    try {
                        if(tagCnt.size() != 0) {
                            String sValue = tagCnt.get((int) value).getString("tag");
                            return sValue;
                        }
                    } catch (Exception e) {
                        Log.e("HWA", e + "");
                    }
                    return "";
                }
            });
        }
        else if(code == LOCALITY){
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    try {
                        if(localityCnt.size() != 0) {
                            String sValue = localityCnt.get((int) value).getString("locality");
                            return sValue;
                        }
                    } catch (Exception e) {
                        Log.e("HWA", e + "");
                    }
                    return "";
                }
            });
        }
        else if(code == THOROUGHFARE){
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    try {
                        if(thoroughfareCnt.size() != 0) {
                            String sValue = thoroughfareCnt.get(tfIdx).getJSONArray("thoroughfares").getJSONObject((int) value).getString("name");
                            return sValue;
                        }
                    } catch (Exception e) {
                        Log.e("HWA", e + "");
                    }
                    return "";
                }
            });
        }
        else if(code == FACE){
            bChart.setVisibleXRangeMaximum(10f);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    String sValue = "";
                    try {
                        if(faceCnt.size() != 0) {
                            sValue = faceCnt.get((int) value).getString("label");
                            Integer.parseInt(sValue);
                            return "?";
                        }
                    } catch (NumberFormatException e) { //초기화된 얼굴일 경우
                        return sValue;
                    } catch (Exception e) {
                        Log.e("HWA", e + "");
                    }
                    return "";
                }
            });
        }

        //왼쪽 축 값 설정
        YAxis yAxis = bChart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setGranularity(1f);   //간격
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String sValue = String.valueOf((int)value);
                return sValue;
            }
        });

        //오른쪽 축 값 삭제
        YAxis rAxis = bChart.getAxisRight();
        rAxis.setDrawLabels(false);
        rAxis.setDrawAxisLine(false);
        rAxis.setDrawGridLines(false);
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
                    mTextView.setText(monthCnt.get(0).getString("year") + "년");
                    JSONObject m = monthCnt.get(0).getJSONObject("month");
                    for(int i = 1; i < 13; i++){
                        String k = String.valueOf(i);
                        mList.add(new BarEntry(i, m.getInt(k)));
                    }


                    resArr = response.getJSONArray("dayCnt");
                    for(int i = 0; i< resArr.length(); i++){
                        dayCnt.add(resArr.getJSONObject(i));
                    }
                    //dTextView.setText(dayCnt.get(0).getString("year") + "년");
                    JSONObject d = dayCnt.get(0).getJSONObject("dayofweek");
                    for(int i = 1; i < 8; i++){
                        String k = String.valueOf(i);
                        dList.add(new BarEntry(i, d.getInt(k)));
                    }


                    resArr = response.getJSONArray("tagCnt");
                    for(int i = 0; i< resArr.length(); i++){
                        int count = resArr.getJSONObject(i).getInt("count");
                        tagCnt.add(resArr.getJSONObject(i));
                        tList.add(new BarEntry(i, count));
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
                    tfTextView.setText(thoroughfareCnt.get(0).getString("locality"));
                    JSONArray tf = thoroughfareCnt.get(0).getJSONArray("thoroughfares");
                    for(int i = 0; i < tf.length(); i++){
                        JSONObject t = tf.getJSONObject(i);
                        tfList.add(new BarEntry(i, t.getInt("count")));
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
                    updateChart(tDataSet, tData, tChart);
                    updateChart(lDataSet, lData, lChart);
                    updateChart(tfDataSet, tfData, tfChart);
                    updateChart(fDataSet, fData, fChart);

                    initChart(YEAR, yChart, yDataSet, yData, yList);
                    initChart(MONTH, mChart, mDataSet, mData, mList);
                    initChart(DAY, dChart, dDataSet, dData, dList);
                    initChart(TAG, tChart, tDataSet, tData, tList);
                    initChart(LOCALITY, lChart, lDataSet, lData, lList);
                    initChart(THOROUGHFARE, tfChart, tfDataSet, tfData, tfList);
                    initChart(FACE, fChart, fDataSet, fData, fList);

                } catch (JSONException e) {
                    Log.e("GET", "reqGetStats onResponse JSONException : " + e);
                }  catch (Exception e) {
                    Log.e("GET", "reqGetStats onResponse Exception : " + e);
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

    private void updateChart(BarDataSet bDataSet, BarData bData, BarChart bChart) {
        bDataSet.notifyDataSetChanged();
        bData.notifyDataChanged();
        bChart.notifyDataSetChanged();
        bChart.invalidate();
    }


}
