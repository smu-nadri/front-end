package com.example.nadri4_edit1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class PhotoCustomDialog extends Activity {

    FrameLayout photoLayout;
    ImageView photo_big;
    View photo_fore;
    TextView photo_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_data_dialog);

        //xml연결
        photoLayout = findViewById(R.id.photoLayout);
        photo_big = findViewById(R.id.imgView);
        photo_fore = findViewById(R.id.photo_fore);
        photo_text = findViewById(R.id.photo_text);

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
}
