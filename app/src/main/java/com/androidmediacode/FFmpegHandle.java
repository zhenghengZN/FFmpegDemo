package com.androidmediacode;

import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import VideoHandle.OnEditorListener;

/**
 * Created by zhengheng on 18/3/31.
 */
//public class FFmpegHandle implements FFmpegExecuteResponseHandler {
public class FFmpegHandle implements OnEditorListener {

    @Override
    public void onSuccess() {
        Log.e("zhengheng", "onSuccess ");

    }

    @Override
    public void onFailure() {
        Log.e("zhengheng", "onFailure ");
    }

    @Override
    public void onProgress(float v) {
        Log.e("zhengheng", "onProgress " + v);
    }

//    @Override
//    public void onSuccess(String s) {
//        Log.e("zhengheng", "onSuccess " + s);
//    }
//
//    @Override
//    public void onProgress(String s) {
//        Log.e("zhengheng", "onProgress " + s);
//    }
//
//    @Override
//    public void onFailure(String s) {
//        Log.e("zhengheng", "onFailure " + s);
//    }
//
//    @Override
//    public void onStart() {
//        Log.e("zhengheng", "onStart ");
//    }
//
//    @Override
//    public void onFinish() {
//        Log.e("zhengheng", "onFinish ");
//    }
}
