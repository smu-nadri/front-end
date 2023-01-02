package com.example.nadri4_edit1;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class HighlightWorker extends Worker {

    Context mContext;

    public HighlightWorker(Context context, WorkerParameters params){
        super(context, params);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            //서버 호출
            ReqServer.reqGetHighlight(mContext);
            Log.d("HWA", "work test success : " + Result.success());
            return Result.success();
        } catch (Exception e){
            Log.d("HWA", "work test failed : " + Result.failure());
            return Result.failure();
        }
    }
}

