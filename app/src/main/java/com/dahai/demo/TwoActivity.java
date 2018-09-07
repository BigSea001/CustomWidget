package com.dahai.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dahai.demo.widget.CharIndexView;
import com.dahai.demo.widget.CircleProgressView;
import com.dahai.demo.widget.CountdownProgressBar;
import com.dahai.demo.widget.LoadingView;

/**
 * File: TwoActivity.java
 * 作者: 大海
 * 创建日期: 2018/8/29 0029 16:10
 * 描述：
 */
public class TwoActivity extends AppCompatActivity {

    private CircleProgressView progressView;

    private int progress;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    progressView.setCurrentPresent(progress++);

                    if (progress<100) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        },1000);
                    }

                    break;
            }
        }
    };
    private String TAG = "HHH";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        LoadingView loadingView = findViewById(R.id.loadingView);
        loadingView.start();

        progressView = findViewById(R.id.progressView);
        progressView.setStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        },1000);

        CharIndexView charIndexView = findViewById(R.id.charIndexView);
        charIndexView.setOnCharacterTouchedListener(new CharIndexView.OnCharacterTouchedListener() {
            @Override
            public void onSelect(char c) {
                Log.e(TAG, "onSelect: " + c );
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel: ");
            }

            @Override
            public void onDown() {
                Log.e(TAG, "onDown: ");
            }
        });

        CountdownProgressBar countDown = findViewById(R.id.countDown);
        countDown.setDuration(3000);
        countDown.start();
        countDown.setOnEndListener(new CountdownProgressBar.OnEndListener() {
            @Override
            public void onEnd() {
                Log.e(TAG, "onEnd: " + "完" );
            }
        });
    }
}
