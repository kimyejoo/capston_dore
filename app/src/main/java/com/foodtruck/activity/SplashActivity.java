package com.foodtruck.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

    private final int CHECK_PERMISSION = 3423;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }


    @Override
    protected void onResume() {
        super.onResume();

        checkLocationPermission();
    }

    private void checkLocationPermission() {

            int perLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if(perLocation == PackageManager.PERMISSION_DENIED ){
                //권한 없음
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CHECK_PERMISSION);
            } else {
                requestData();
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CHECK_PERMISSION: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            alertTerminate();
                            return;
                        }
                    }
                }

                requestData();
                return;
            }
        }
    }

    private void alertTerminate() {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage("위치 정보를 동의 하셔야 이용이 가능합니다")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    //푸드트럭 정보를 가져온다.
    private void requestData() {

        if(!checkUseLocation()) {
            return;
        }


        RequestApi.getInstance().getTruckList(new NetworkResponse<StoreListVo>() {
            @Override
            public void onSuccess(Call call, StoreListVo clazz) {
                Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
                intent.putExtra("data", clazz.getData());
                startActivity(intent);
                finish();
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


    /**
     * GPS 정보를 가져오지 못했을때
     * 설정값으로 갈지 물어보는 alert 창
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("위치 사용");
        alertDialog.setMessage("위치 정보 사용을 허락 해주세요.");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("설정",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        alertDialog.show();
    }


    private boolean checkUseLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // GPS 정보 가져오기
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // 현재 네트워크 상태 값 알아오기
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSEnabled && !isNetworkEnabled) {
            showSettingsAlert();
            return false;
        }

        return true;

    }





}
