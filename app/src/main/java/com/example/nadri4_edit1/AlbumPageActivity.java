package com.example.nadri4_edit1;

import static androidx.exifinterface.media.ExifInterface.TAG_DATETIME;

import static com.example.nadri4_edit1.InitApplication.faceMap;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DeleteCollectionResult;
import com.amazonaws.services.rekognition.model.DeleteFacesRequest;
import com.amazonaws.services.rekognition.model.DeleteFacesResult;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;
import com.amazonaws.services.rekognition.model.QualityFilter;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.rekognition.model.UnindexedFace;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumPageActivity extends AppCompatActivity {

    static RecyclerView recyclerView;  //???????????? ???????????? ???
    static MultiImageAdapter adapter;
    ItemTouchHelper helper;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    static FrameLayout menuBtnLayout;
    static LinearLayout basicBtnLayout, editBtnLayout;
    static Boolean isCheck = false;

    static ImageButton btnMenu, btnEdit, btnGetImage, btnSave, btnRemove, btnMarked, btnBlank;
    TextView tvPageDate;

    ImageView photo_big;
    View photo_fore;
    TextView photo_text;

    ImageLabelerOptions imageLabelerOptions;
    ImageLabeler labeler;

    FaceDetectorOptions faceDetectorOptions;
    FaceDetector detector;
    FaceRecognize recognizer;

    String access_id = "AKIASLE2NOY6FVICSDHF";
    String access_pw = "G5M6/OqSYVX4xp8agkl6gCETMrWrchbr1vSSn3CT";
    AmazonRekognition rekognition = new AmazonRekognitionClient(new BasicAWSCredentials(access_id, access_pw));

    private static final int REQUEST_CODE = 0;
    private static final int JUST_UPLOAD = 1;
    private static final int TO_SEARCH = 2;

    Uri uri;
    String namePerson;

    private static final String collectionId = "ndr";
    private static final String bucket = "capnadeuri";
    Bitmap bitmap;

    FileOutputStream fos;
    private final String localFaceList = "LocalListofFace.tmp";


    public static Intent getDateIntent;
    public int iDay;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_page_layout);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        //xml??????
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvPageDate = (TextView) findViewById(R.id.tvPageDate);

        //menuBtnLayout = (FrameLayout) findViewById(R.id.menuBtnLayout);
        basicBtnLayout = (LinearLayout) findViewById(R.id.basicBtnLayout);
        editBtnLayout = (LinearLayout) findViewById(R.id.editBtnLayout);

        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        btnEdit = (ImageButton) findViewById(R.id.btnEdit);
        btnGetImage = (ImageButton) findViewById(R.id.btnGetImage);
        btnSave = (ImageButton) findViewById(R.id.btnSave);
        btnBlank = (ImageButton) findViewById(R.id.btnBlank);   //???????????? ?????? ??????
        btnMarked = (ImageButton) findViewById(R.id.btnMarked); //???????????? ?????? ??????
        btnRemove = (ImageButton) findViewById(R.id.btnRemove);

        //xml??????
        photo_big = findViewById(R.id.imgView);
        photo_fore = findViewById(R.id.photo_fore);
        photo_text = findViewById(R.id.photo_text);


        //?????????
        getDateIntent = getIntent();
        iDay = getDateIntent.getIntExtra("SelectedDATE",-1);


        //?????? ??????
        String title;   //?????? ??????
        try {
            if (iDay == -1){    //????????????????????? ??????????????? ??????

                if(getDateIntent.getBooleanExtra("customAlbum", false)){    //????????????
                    title = getDateIntent.getStringExtra("title");
                    ReqServer.album.put("title", title);
                    ReqServer.album.put("type", "customAlbum");
                    tvPageDate.setText(title);
                    tvPageDate.setEnabled(true);

                    if(getDateIntent.getBooleanExtra("getImage", false)){   //???????????? ???????????? ????????? ??????
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    } else {
                        //????????? ????????? ????????? ????????????
                        ReqServer.reqGetPages(AlbumPageActivity.this);
                    }
                }
                else {  //?????? ??????
                    title = getDateIntent.getStringExtra("title");
                    ReqServer.album.put("title", title);
                    ReqServer.album.put("type", "dateAlbum");

                    String[] split_title = title.split("-");
                    tvPageDate.setText(split_title[0]+"??? "+split_title[1]+"???");
                    tvPageDate.setEnabled(false);

                    //????????? ????????? ????????? ????????????
                    ReqServer.reqGetPages(AlbumPageActivity.this);

                }
            }
            else {  //??????????????? ????????? ????????? ??????
                setView(iDay);
                String tMonth, tDay;
                if(CalendarUtil.selectedDate.get(Calendar.MONTH) + 1 < 10) tMonth = "0" + (CalendarUtil.selectedDate.get(Calendar.MONTH) + 1);
                else tMonth = String.valueOf(CalendarUtil.selectedDate.get(Calendar.MONTH) + 1);
                if(iDay < 10) tDay = "0" + iDay;
                else tDay = String.valueOf(iDay);

                ReqServer.album.put("title", CalendarUtil.selectedDate.get(Calendar.YEAR) + "-" + tMonth + "-" + tDay);
                ReqServer.album.put("type", "dateAlbum");

                //????????? ????????? ????????? ????????????
                ReqServer.reqGetPages(AlbumPageActivity.this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //???????????? ??????(??? = 2)
        RecyclerView.LayoutManager manager = new GridLayoutManager(AlbumPageActivity.recyclerView.getContext(), 2);

        //???????????? ??????
        recyclerView.setLayoutManager(manager);

        //????????? ??????
        setAdapterUpdated();

        //???????????? ?????? - ??????????????? ?????? ??????(?????????????????? ??????????????? ??? ??????)
        if(getDateIntent.getBooleanExtra("customAlbum", false)){
            helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
            helper.attachToRecyclerView(recyclerView);
        }


        //????????? ???????????? ??????
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

        /*
        //????????? ?????? ?????? ????????????
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter.getmData().size() == 0){
                    Toast.makeText(getApplicationContext(), "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
                else if(tvPageDate.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        if(getDateIntent.getBooleanExtra("customAlbum", false)) {
                            ReqServer.album.put("title", tvPageDate.getText().toString());
                            ReqServer.album.put("type", "customAlbum");
                        }
                        ReqServer.reqPostPages(AlbumPageActivity.this);
                    } catch (JSONException e) {
                        Log.e("AlbumPageActivity", "btnSave JSONException: " + e);
                    }
                }
            }
        });

         */


        //?????? ??????
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ArrayList<JSONObject> mData = adapter.getmData();
                    int i = 0;
                    while (mData.size() != 0 && i < mData.size()) {
                        Log.d("HWA", "???????????? " + i + " : "+ mData.size() + " :" + mData.get(i));
                        if (mData.get(i).getBoolean("isChecked")) {
                            if (mData.get(i).has("_id")) {
                                ReqServer.deletedList.add(mData.get(i));
                            }
                            mData.remove(i);
                            adapter.notifyDataSetChanged();
                        } else {
                            i++;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        /*
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMenu.setVisibility(View.GONE);
                btnEdit.setVisibility(View.VISIBLE);
                basicBtnLayout.setVisibility(View.VISIBLE);
            }
        });

         */

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editBtnLayout.getVisibility() == View.GONE){
                    btnGetImage.setVisibility(View.VISIBLE);
                    editBtnLayout.setVisibility(View.VISIBLE);
                    btnEdit.setImageResource(R.drawable.edit_click);

                    MultiImageAdapter.isEdit = true;
                    Log.d("CHECKBOX", "yes??? : " + MultiImageAdapter.isEdit);
                }
                else{
                    btnGetImage.setVisibility(View.GONE);
                    editBtnLayout.setVisibility(View.GONE);
                    btnEdit.setImageResource(R.drawable.edit_unclick);

                    MultiImageAdapter.isEdit = false;
                    Log.d("CHECKBOX", "no??? : " + MultiImageAdapter.isEdit);
                }

                if(btnMarked.getVisibility() == View.VISIBLE){
                    btnBlank.setVisibility(View.VISIBLE);
                    btnMarked.setVisibility(View.GONE);

                    //???????????? ?????? ??????
                    MultiImageAdapter.isCheck = false;
                }
                adapter.notifyDataSetChanged();

            }
        });

        btnBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMarked.setVisibility(View.VISIBLE);
                btnBlank.setVisibility(View.GONE);

                //???????????? ?????? ??????
                MultiImageAdapter.isCheck = true;
                adapter.notifyDataSetChanged();

            }
        });
        btnMarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMarked.setVisibility(View.GONE);
                btnBlank.setVisibility(View.VISIBLE);

                //???????????? ?????? ??????
                MultiImageAdapter.isCheck = false;
                adapter.notifyDataSetChanged();
            }
        });



        tvPageDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    ReqServer.album.put("title", editable.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });




        imageLabelerOptions = new ImageLabelerOptions.Builder() //?????? ??????
                .setConfidenceThreshold(0.6f)   //????????? ?????????
                .build();
        labeler = ImageLabeling.getClient(imageLabelerOptions);

        faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .enableTracking()
                .setMinFaceSize(0.15f)
                .setContourMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .build();
        detector = FaceDetection.getClient(faceDetectorOptions);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        rekognition.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        listCollections();
        //deleteCollection(); //????????? ??????
        //deleteFile(localFaceList);  //MAP ????????? ??? ??????
    }


    //back button ?????????
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if(0<=intervalTime && FINISH_INTERVAL_TIME>=intervalTime){
            finish();
            Log.d("GOO", "?????????,,");
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(this, "?????? ??? ????????? ???????????????!", Toast.LENGTH_SHORT).show();
        }
    }



    //????????? ????????? ?????? ??? ????????? ????????????
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tvPageDate.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                if(getDateIntent.getBooleanExtra("customAlbum", false)) {
                    ReqServer.album.put("title", tvPageDate.getText().toString());
                    ReqServer.album.put("type", "customAlbum");
                    ReqServer.reqPostPages(AlbumPageActivity.this, 1);
                }
                else {
                    if(iDay != -1) ReqServer.reqPostPages(AlbumPageActivity.this, 0);
                }
            } catch (JSONException e) {
                Log.e("AlbumPageActivity", "btnSave JSONException: " + e);
            }
        }

        ReqServer.photoList.clear();
        ReqServer.album.remove("title");
        ReqServer.album.remove("thumbnail");
        ReqServer.album.remove("type");
        MultiImageAdapter.isEdit = false;
        MultiImageAdapter.isCheck = false;

        //adapter.notifyDataSetChanged();
    }

    //???????????? ??????????????? ????????? ??? ???????????? ????????? = ?????? ???????????? ??????!
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        ContentResolver resolver = this.getContentResolver();

        if(data == null){
            Toast.makeText(this, "???????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
        }//????????? ????????? ??????
        else{
            if(data.getClipData() == null){
                Log.d("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                resolver.takePersistableUriPermission(imageUri, takeFlags);

                JSONObject imageInfo = new JSONObject();    //?????? ??? ?????? uri??? ???????????? ?????? ??????
                setUriExif(imageInfo, imageUri);
                setUriTags(imageInfo, imageUri);
                setUriFaces(imageInfo, imageUri);
            }//???????????? ????????? ??????
            else{
                ClipData clipData = data.getClipData();
                Log.d("clipData", String.valueOf(clipData.getItemCount()));

                for(int i=0; i< clipData.getItemCount(); i++){
                    Uri imageUri = clipData.getItemAt(i).getUri();  //????????? ??????????????? uri??? ????????????.
                    resolver.takePersistableUriPermission(imageUri, takeFlags); //uri ?????? ??????

                    JSONObject imageInfo = new JSONObject();    //?????? ??? ?????? uri??? ???????????? ?????? ??????
                    setUriExif(imageInfo, imageUri);
                    setUriTags(imageInfo, imageUri);
                    setUriFaces(imageInfo, imageUri);
                }
            }//???????????? ????????? ??????

        }//????????? ?????????
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String dateFormat(Calendar calendar, int day){  //2022??? 5???

        //?????? ??????
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;

        String date = year + "???  " + month + "???  " + day + "??? ";

        return date;
    }

    //?????? ??????
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setView(int day){
        tvPageDate.setText(dateFormat(CalendarUtil.selectedDate, day));
    }

    //????????? ???????????? ?????????
    protected static void setAdapterUpdated(){
        //????????? ??? ?????? ???
        adapter = new MultiImageAdapter(ReqServer.photoList, AlbumPageActivity.recyclerView.getContext());

        recyclerView.setAdapter(AlbumPageActivity.adapter);
    }

    @SuppressLint("RestrictedApi")
    private void setUriExif(JSONObject imageInfo, Uri imageUri){
        try {
            imageInfo.put("uri", imageUri); //uri ????????? ??????
            imageInfo.put("isChecked", false);

            InputStream inputStream = AlbumPageActivity.this.getContentResolver().openInputStream(imageUri);
            ExifInterface exif = new ExifInterface(inputStream);

            //?????? ??????
            Long datetime = exif.getDateTime();
            if(datetime == null){
                Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
                cursor.moveToFirst();
                int date_taken = cursor.getColumnIndexOrThrow("last_modified");
                datetime = cursor.getLong(date_taken);
            }
            imageInfo.put("datetime", datetime);

            //?????? ??????
            double latLong[] = exif.getLatLong();
            JSONObject location = new JSONObject();
            if (latLong != null) {
                Geocoder gCoder = new Geocoder(getApplicationContext());
                List<Address> addressList = gCoder.getFromLocation(latLong[0], latLong[1], 10);
                location.put("lat", latLong[0]);
                location.put("long", latLong[1]);
                if (!addressList.isEmpty()) {
                    location.put("address", addressList.get(0).getAddressLine(0));
                    location.put("admin", addressList.get(0).getAdminArea());
                    location.put("locality", addressList.get(0).getLocality());
                    location.put("thoroughfare", addressList.get(0).getThoroughfare());
                }
                imageInfo.put("location", location);
            }

            ReqServer.photoList.add(imageInfo); //???????????? ????????? ???????????? ?????? ?????? ??????
            setAdapterUpdated();
        } catch (IOException | JSONException e){
            Log.e("AlbumPageActivity", "Exif Error:" + e);

        }
    }

    //????????? ????????? ?????? ????????????
    protected void setUriTags(JSONObject imageInfo, Uri imageUri){
        JSONArray tagsIndex = new JSONArray();    //?????? ??? ?????? ?????????

        try {
            //?????? ????????????
            InputImage image = InputImage.fromFilePath(this, imageUri);    //uri -> InputImage ??????

            labeler.process(image).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                @Override
                public void onSuccess(List<ImageLabel> labels) {
                    try {
                        if (!labels.isEmpty()) {
                            //tagmap.json ?????? ???????????? ????????? ??????
                            InputStream inputS = getResources().openRawResource(R.raw.tagmap);
                            byte[] buffer = new byte[inputS.available()];
                            inputS.read(buffer);
                            inputS.close();
                            String json = new String(buffer, "UTF-8");
                            JSONArray tagMap = new JSONArray(json);

                            float confidence = labels.get(0).getConfidence();
                            for (ImageLabel label : labels) {   //????????? 15% ????????? ?????? 3???????????? ??????
                                Log.d("HWA", "label: " + label.getIndex() + " " + label.getText());
                                if (tagsIndex.length() >= 3) break;
                                else if (confidence - label.getConfidence() > 0.15f)
                                    break;
                                else tagsIndex.put(tagMap.getJSONObject(label.getIndex()));
                            }
                            imageInfo.put("tags", tagsIndex);   //????????? ??????
                        }
                        adapter.notifyDataSetChanged(); //????????? ??????
                    } catch(Exception e) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() { //????????? ???????????? uri??? ??????
                @Override
                public void onFailure(@NonNull Exception e) {
                    ReqServer.photoList.add(imageInfo);
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
            Log.e("AlbumPageActivity", "setUriTags Exception : " + e);
        }
    }

    protected void setUriFaces(JSONObject imageInfo, Uri imageUri){
        JSONArray facesArr = new JSONArray();   //?????? ??? ?????? ?????????
        try {

            //?????? ????????????
            InputImage image = InputImage.fromFilePath(this, imageUri);    //uri -> InputImage ??????
            Bitmap pickedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onSuccess(List<Face> faces) {
                    Log.d("HWA", "face ?????? : " + faces.size());
                    try {

                        for (Face face : faces) {
                            JSONObject faceInfo = new JSONObject();
                            //????????? ?????????
                            Rect bounds = face.getBoundingBox();
                            int boundsX = bounds.left;
                            int boundsY = bounds.top;
                            int boundsW = bounds.width();
                            int boundsH = bounds.height();
                            Log.d("HWA", boundsX + " " + boundsY + " " + boundsW + " " + boundsH);
                            if (boundsX < 0 || boundsY < 0){
                                continue;
                            }
                            if (boundsX + boundsW > pickedImage.getWidth()) {
                                boundsW = pickedImage.getWidth() - boundsX;
                            }
                            if (boundsY + boundsH > pickedImage.getHeight()){
                                boundsH = pickedImage.getHeight() - boundsY;
                            }

                            Bitmap resized = Bitmap.createBitmap(pickedImage, boundsX, boundsY, boundsW, boundsH);

                            String date = timeStamp();
                            faceInfo.put("left", boundsX);
                            faceInfo.put("top", boundsY);
                            faceInfo.put("width", boundsW);
                            faceInfo.put("height", boundsH);

                            Log.d("HWA", "?????? : 1 Faces for???");
                            compressBitmap(resized, date, faceInfo, facesArr);
                        }

                        imageInfo.put("faces", facesArr);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.d("HWA", "?????? : " + e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("AlbumPageActivity", "setUriTags Exception : " + e);
        }
    }

    synchronized void listCollections() {
        // To list the collections.
        // It won't work if there's any exisiting collection.
        // We need only one collection named ndr.
        rekognition.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));

        System.out.println("Listing collections");
        int limit = 10;
        ListCollectionsResult listCollectionsResult = null;
        String paginationToken = null;
        do {
            if (listCollectionsResult != null) {
                paginationToken = listCollectionsResult.getNextToken();
            }
            ListCollectionsRequest listCollectionsRequest = new ListCollectionsRequest()
                    .withMaxResults(limit)
                    .withNextToken(paginationToken);
            listCollectionsResult = rekognition.listCollections(listCollectionsRequest);

            int cnt = 0;
            List< String > collectionIds = listCollectionsResult.getCollectionIds();
            for (String resultId: collectionIds) {
                System.out.println("collectionId: " + resultId);
                Log.d("JUN", "Result ID : " + resultId);
                Log.d("JUN", "Collection ID : " + collectionId);
                if (resultId.equals(collectionId)) {
                    Log.d("JUN", "Collection is already existing.");
                    cnt += 1;
                }
                Log.d("JUN", "cnt : " + cnt);
            }
            if (cnt != 0) {
                Log.d("JUN", "No need to create a new collection.");
            } else {
                Log.d("JUN", "Wait a minute. I'm trying to create a collection called " + collectionId);
                createCollection();
            }

        } while (listCollectionsResult != null && listCollectionsResult.getNextToken() !=
                null);
    }

    void createCollection () {
        // This creates collection, but if the collection with same name already exists,
        // it would not work.
        rekognition.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));

        System.out.println("Creating Collection : " + collectionId);
        CreateCollectionRequest request = new CreateCollectionRequest().withCollectionId(collectionId);

        CreateCollectionResult result = rekognition.createCollection(request);

        System.out.println("CollectioinArn : " + result.getCollectionArn());
        System.out.println("Status Code : " + result.getStatusCode().toString());
    }

    String timeStamp(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddkkmmss");
        String date = String.valueOf(calendar.getTimeInMillis());
        Log.d("JUN", date);
        return date;
    }

    synchronized void compressBitmap(Bitmap resized, String fileName, JSONObject faceInfo, JSONArray facesArr) {
        // It saves bitmap to .jpeg file.
        Log.d("HWA", "?????? : 2 compressBitmap");

        String MEDIA_PATH = getFilesDir().getPath() + "/.savepoint/";
        File dir = new File(MEDIA_PATH);
        OutputStream out = null;

        try{
            if (!dir.exists()) {
                dir.mkdir();
                Log.d("JUN", "No such folder exists.");
            }
            else {
                Log.d("JUN", "It already exists.");
            }

            if (dir.exists()) Log.d("JUN", "Success!");
            else Log.d("JUN", "Something's wrong.");
        } catch (Exception e) {
            Log.e("HWA", "?????? : compressBitmap " + e);
        }

        try {
            MEDIA_PATH = MEDIA_PATH + fileName + ".jpeg";
            Log.d("JUN", MEDIA_PATH);

            dir = new File(MEDIA_PATH);
            dir.createNewFile();
            out = new FileOutputStream(dir);

            resized.compress(Bitmap.CompressFormat.JPEG, 100 , out);
            uploadObject(MEDIA_PATH, fileName, namePerson, faceInfo, facesArr);

            out.close();
        } catch (Exception e) {
            Log.e("HWA", "compressBitmap Error : " + e);
        }

    }

    synchronized void uploadObject(String filePath, String fileName, String namePerson, JSONObject faceInfo, JSONArray facesArr) {
        // This is for uploading file to bucket, and it only works for adding faces.
        Log.d("HWA", "?????? : 3 uploadObject");
        File toUpload = new File(filePath);
        AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(access_id, access_pw));
        s3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(this.getApplicationContext()).build();
        TransferNetworkLossHandler.getInstance(this.getApplicationContext());

        TransferObserver uploadObserver = transferUtility.upload(bucket, fileName, toUpload);

        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if(state == TransferState.COMPLETED) {
                    Toast.makeText(getApplicationContext(), "Uploaded - " + fileName, Toast.LENGTH_SHORT).show();
                    Log.d("HWA", "?????? : 3 uploadObject upload");

                    isCompared(fileName, faceInfo, facesArr);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int done = (int) (((double) bytesCurrent/bytesTotal) * 100.0);
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d("JUN", "Damnit!" + ex);
            }
        });
    }

    synchronized void addFace2Collection (String fileName, String nameFace, JSONObject faceInfo, JSONArray facesArr) {
        // This works to add face to collection.
        // It will only work when the user tries to add face and name.
        Log.d("HWA", "?????? : 6 addFace2Collection");
        Image image = new Image()
                .withS3Object(new S3Object()
                        .withBucket(bucket)
                        .withName(fileName));
        Log.d("JUN", bucket + " " + fileName);

        IndexFacesRequest request = new IndexFacesRequest()
                .withImage(image)
                .withQualityFilter(QualityFilter.AUTO)
                .withMaxFaces(1)
                .withCollectionId(collectionId)
                .withExternalImageId(fileName)
                .withDetectionAttributes("DEFAULT");

        Log.d("JUN", "After request... continuing.");

        IndexFacesResult result = rekognition.indexFaces(request);

        Log.d("JUN", "Results for " + fileName);
        Log.d("JUN", "Faces indexed: ");

        List<FaceRecord> faceRecords = result.getFaceRecords();

        for(FaceRecord faceRecord: faceRecords) {
            Log.d("JUN", "  Face ID: " + faceRecord.getFace().getFaceId());
            try {
                faceInfo.put("faceId", faceRecord.getFace().getFaceId());
                facesArr.put(faceInfo);
            } catch (JSONException e) {
                Log.e("HWA", "?????? : 6 ??????" + e);
            }
            Log.d("JUN", "  Location:" + faceRecord.getFaceDetail().getBoundingBox().toString());
            enrollMap(faceRecord.getFace().getFaceId(), nameFace);
        }

        List<UnindexedFace> unindexedFaces = result.getUnindexedFaces();
        Log.d("JUN", "Faces not indexed:");

        for (UnindexedFace unindexedFace : unindexedFaces) {
            Log.d("JUN", "  Location:" + unindexedFace.getFaceDetail().getBoundingBox().toString());
            Log.d("JUN", "  Reasons:");

            for (String reason : unindexedFace.getReasons()) {
                Log.d("JUN", "   " + reason);
            }
        }
//        listFace();
    }

    synchronized void isCompared(String fileName, JSONObject faceInfo, JSONArray facesArr) {
        try {
            Log.d("HWA", "?????? : 4 isCompared");
            // This is to search faces.
            ObjectMapper objectMapper = new ObjectMapper();

            Image image = new Image()
                    .withS3Object(new S3Object()
                            .withBucket(bucket)
                            .withName(fileName));


            SearchFacesByImageRequest searchRequest = new SearchFacesByImageRequest()
                    .withCollectionId(collectionId)
                    .withImage(image)
                    .withFaceMatchThreshold(70F)
                    .withMaxFaces(2);

            SearchFacesByImageResult searchResult = rekognition.searchFacesByImage(searchRequest);
            List<FaceMatch> faceImageMatches = searchResult.getFaceMatches();
            if (!faceImageMatches.isEmpty()) {
                Log.d("HWA", "?????? : 5 ?????? ??????!");
                for (FaceMatch face : faceImageMatches) {
                    Log.d("SEARCH", face.getFace().getFaceId());
                    try {
                        faceInfo.put("faceId", face.getFace().getFaceId());

                        String name = faceMap.get(face.getFace().getFaceId());
                        Log.e("HWA", "MAP?????? ?????? ?????? : " + name);
                        if (name != null) {
                            faceInfo.put("name", name);
                            facesArr.put(faceInfo);
                        }
                    } catch (JSONException e) {
                        Log.e("HWA", "?????? : 5 ?????? ?????? ?????? " + e);
                    }
                    //searchMap(face.getFace().getFaceId(), faceInfo);
                }
            } else {
                Log.d("HWA", "?????? : 5 ?????? ??????!");
                Toast.makeText(this, "No result to show!", Toast.LENGTH_SHORT).show();
                Log.d("SEARCH", "No result to show!");
                try {
                    faceInfo.put("name", "Unknown");
                } catch (JSONException e) {
                    Log.e("JUN", "?????? : 5 ?????? ?????? ?????? " + e);
                }
                addFace2Collection(fileName, "Unknown", faceInfo, facesArr);
            }
        } catch(Exception e){
            Log.e("HWA", "?????? ?????? ?????????.. " + e);
        }
    }

    synchronized void enrollMap(String uploadDate, String namePerson){
        // This app has list saved in local storage so every picture should be saved
        // with key (uploadDate, and it will be the file's name)
        // and name (namePerson).
        Log.d("HWA", "?????? : 7 enrollMap!");
        faceMap.put(uploadDate, namePerson);
        Log.d("FACEMAP", uploadDate +" "+ namePerson);
        try {
            fos = openFileOutput(localFaceList, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(faceMap);
            oos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String key: faceMap.keySet()) {
            Log.d("FORMOON", key + " " + faceMap.get(key));

        }
    }

    void searchMap(String imageID, JSONObject imageInfo) {
        Log.d("KEY", "???????");
        // This code will search if there's any person enrolled to the faceMap.
        // If so, it will show the name. Unless, it will show nothing.
        for (String key: faceMap.keySet()) {
            Log.d("KEYTMP", key + " " + imageID);
            if (key.equals(imageID)) {
                Log.d("Found", faceMap.get(key));
                try {
                    imageInfo.put("name", faceMap.get(key));
                } catch (JSONException e) {
                    Log.e("JUN", "?????????..");
                }
                //textView.setText(faceMap.get(key)); ??????
            }
            else {
                Log.d("Found", "No result to show!");
            }
        }

    }


    void deleteCollection() {
        Log.e("HWA", "?????????");
        System.out.println("deleting...");
        DeleteCollectionRequest request = new DeleteCollectionRequest().withCollectionId(collectionId);
        DeleteCollectionResult result = rekognition.deleteCollection(request);
        System.out.println(collectionId + " : " + result.getStatusCode().toString());
    }
}