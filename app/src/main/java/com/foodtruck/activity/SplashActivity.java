package com.foodtruck.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.foodtruck.R;
import com.foodtruck.network.NetworkResponse;
import com.foodtruck.network.RequestApi;
import com.foodtruck.utils.Constants;
import com.foodtruck.utils.LogUtil;
import com.foodtruck.vo.StoreListVo;

import java.util.Calendar;

import retrofit2.Call;

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
        RequestApi.getInstance().getTruckList(new NetworkResponse<StoreListVo>() {
            @Override
            public void onSuccess(Call call, StoreListVo clazz) {
                Intent intent = new Intent(SplashActivity.this, StoreListActivity.class);
                intent.putExtra("data", clazz.getData());
                startActivity(intent);
            }

            @Override
            public void onFail(Call call, String msg) {
                Toast.makeText(SplashActivity.this, " Error : " + msg, Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(SplashActivity.this)
                        .setMessage("서버와 연결이 불안합니다. 잠시후 다시 시도해주세요.")
                        .setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }







}
