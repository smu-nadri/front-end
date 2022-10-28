package com.example.nadri4_edit1;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FaceRecognitionAPI implements FaceRecognize{

    private FaceRecognitionAPI() {}

    private static final int OUTPUT_SIZE = 192; //MobileFaceNet은 192byte를 출력함

    private int inputSize;

    private int[] intValues;

    private float[][] embeedings;

    private ByteBuffer imgData;

    private Interpreter tfLite; //TFLite 인터프리터

    private static Integer faceId;
    private static ArrayList<Recognition> registered = new ArrayList<>();  //등록된 얼굴들

    public static Integer getFaceId() {
        return faceId;
    }

    public static void setFaceId(Integer faceId) {
        FaceRecognitionAPI.faceId = faceId;
    }

    public static ArrayList<Recognition> getRegistered() {
        return registered;
    }

    public static void setRegistered(ArrayList<Recognition> register) {
        registered = new ArrayList<>(register);
    }

    @Override
    public JSONObject toJsonObject(Recognition rec) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", rec.getId());
            json.put("label", rec.getLabel());
            json.put("distance", rec.getDistance());
            //json.put("uri", rec.getUri());
            json.put("embeeding", rec.getEmbeeding());
            Rect rect = rec.getLocation();
            json.put("left", rect.left);
            json.put("top", rect.top);
            json.put("right", rect.right);
            json.put("bottom", rect.bottom);

            return json;

        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public void register(Recognition recognition) {    //얼굴 정보 등록하기
        registered.add(recognition);
    }

    //FaceRecognitionAPI 만들기
    public static FaceRecognize create(final AssetManager assetManager, final String modelFilename, final int inputSize) throws IOException {

        final FaceRecognitionAPI faceRecAPI = new FaceRecognitionAPI();

        faceRecAPI.inputSize = inputSize;

        int numBytesPerChannel = 4; // Floating point

        faceRecAPI.imgData = ByteBuffer.allocateDirect(1 * faceRecAPI.inputSize * faceRecAPI.inputSize * 3 * numBytesPerChannel);
        faceRecAPI.imgData.order(ByteOrder.nativeOrder());

        faceRecAPI.intValues = new int[faceRecAPI.inputSize * faceRecAPI.inputSize];

        faceRecAPI.tfLite = new Interpreter(loadModelFile(assetManager, modelFilename));

        faceRecAPI.tfLite.setNumThreads(4);

        return faceRecAPI;

    }

    private static ByteBuffer loadModelFile(AssetManager assetManager, String modelFilename) throws IOException {

        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private Pair<String, Float> findNearest(float[] emb){

        Pair<String, Float> ret = null; //반환할 값 (이름, 거리)

        //registered에 들어있는 얼굴만큼 반복문
        for(Recognition entry : registered){
            final float[] knownEmb = entry.getEmbeeding();

            //유클리드 거리 계산
            float distance = 0;
            for(int i = 0; i < emb.length; i++){
                //Log.d("HWA", i + " : " + emb[i]);
                float diff = emb[i] - knownEmb[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            Log.d("HWA", "API distance : " + entry.getId() + " - " +  entry.getLabel() + " - " + distance);
            if(ret == null || distance < ret.second){   //첫번째 이거나 더 가까운 얼굴이 있다면
                ret = new Pair<>(entry.getLabel(), distance);   //반환값 갱신
            }
        }

        return ret; //가장 가까운 얼굴(이름, 거리) 반환
    }

    @Override
    public Recognition recognizeImage(Bitmap bitmap, Integer id, Rect location) {
        //비트맵 전처리
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imgData.rewind();
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                imgData.putFloat((((pixelValue >> 16) & 0xFF) - 128.0f) / 128.0f);
                imgData.putFloat((((pixelValue >> 8) & 0xFF) - 128.0f) / 128.0f);
                imgData.putFloat(((pixelValue & 0xFF) - 128.0f) / 128.0f);
            }
        }

        //모델 인풋, 아웃풋
        Object[] inputArray = {imgData};
        Map<Integer, Object> outputMap = new HashMap<>();
        embeedings = new float[1][OUTPUT_SIZE];
        outputMap.put(0, embeedings);

        //모델 수행(임베딩 값 계산)
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);

        String label = id.toString();
        float distance = Float.MAX_VALUE;

        if (registered.size() > 0) {    //등록된 얼굴이 있으면
            final Pair<String, Float> nearest = findNearest(embeedings[0]); //가장 비슷한 얼굴이 뭔지 찾기
            if (nearest != null && nearest.second < 0.7f) {    //비슷한 얼굴이 있고 0.8보다 작으면 비슷한 얼굴로 채택
                Log.d("HWA", "API nearest 작다 - id : " + id + " label : " + nearest.first + " distance : " + nearest.second);
                label = nearest.first;
                distance = nearest.second;
            }
            else{   //없으면 아이디값 바꿔주기
                Log.d("HWA", "API nearest 크다 - id : " + id + " label : " + nearest.first + " distance : " + nearest.second);
                label = id.toString();
            }
        }

        Recognition rec = new Recognition(id, label, distance, embeedings[0], location.left, location.top, location.right, location.bottom);

        return rec;
    }

}
