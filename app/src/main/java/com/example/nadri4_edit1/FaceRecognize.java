/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.nadri4_edit1;

import android.graphics.Bitmap;
import android.graphics.Rect;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public interface FaceRecognize {

    //인식 결과 정보를 담고있는 클래스
    public class Recognition implements Serializable {

        private static final long serialVersionUID = 1L;    //파일 저장을 위한 직렬화 시리얼 번호

        private final Integer id;        //인식된 항목의 고유 식별자. 인스턴스가 아닌 클래스가 대상

        private String label;     //인식 이름

        private final Float distance;   //인식 점수. 낮을 수록 좋음

        private float[] embeeding;       //추가 정보. 임베딩

        private Integer left, top, right, bottom;         //인식된 개체의 위치

        //private String uri;            //얼굴이 포함된 사진 uri

        //생성자
        public Recognition(Integer id, String title, Float distance, float[] embeeding, Integer left, Integer top, Integer right, Integer bottom) {
            this.id = id;
            this.label = title;
            this.distance = distance;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.embeeding = embeeding;
            //this.uri = null;
        }

        public Integer getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label){ this.label = label; };

        public Float getDistance() {
            return distance;
        }

        public float[] getEmbeeding() {
            return embeeding;
        }

        public void setEmbeeding(float[] embeeding) {
            this.embeeding = embeeding;
        }

        public Rect getLocation() {
            return new Rect(this.left, this.top, this.right, this.bottom);
        }

        public void setLocation(Rect location) {
            this.left = location.left;
            this.top = location.top;
            this.right = location.right;
            this.bottom = location.bottom;
        }

        /*
        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }*/
    }

    void register(Recognition recognition);

    Recognition recognizeImage(Bitmap bitmap, Integer id, Rect location);

    JSONObject toJsonObject(Recognition rec);
}
