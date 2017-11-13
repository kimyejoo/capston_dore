package com.foodtruck.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;

import com.foodtruck.R;
import com.foodtruck.utils.Constants;

import java.util.Calendar;

//로고 화면
public class SplashActivity extends Activity {

    //데이터 수신 시작 시간
    private Calendar startCal;
    //데이터 수신 종료 시간
    private Calendar endCal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestData();
    }

    //푸드트럭 정보를 가져온다.
    private void requestData() {
        startCal = Calendar.getInstance();
        //TODO 데이터 받으러 가기

        endCal = Calendar.getInstance();

        //1. 남은 시간만큼 기다린다.
        //2. 시간이 남은지 지속 적으로 확인한다.
        launchMainActivity();
    }

    private void launchMainActivity() {
        Log.d("log", "time : " + getViewChangeTime());

        if(getViewChangeTime() <= 0) {
            Intent intent = new Intent(SplashActivity.this, TruckInfoActivity.class);
            startActivity(intent);

            return;
        }

        //기다렸다가 실행
        //FIXME 받아온 데이터를 같이 넘겨 줘야 한다.
        handler.sendEmptyMessageDelayed(100, getViewChangeTime());
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(SplashActivity.this, TruckInfoActivity.class);
            startActivity(intent);

        }
    };


    //기다려야 되는 시간을 리턴
    private int getViewChangeTime() {
        long runtime = endCal.getTimeInMillis() - startCal.getTimeInMillis();
        Log.e("log" , " runtime : " + runtime);
        return Constants.HOLODING_TIME - (int) runtime;

    }









}
