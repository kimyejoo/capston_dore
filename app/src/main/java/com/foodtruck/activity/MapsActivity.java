package com.foodtruck.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.foodtruck.R;
import com.foodtruck.utils.LogUtil;
import com.foodtruck.vo.StoreReplyVo;
import com.foodtruck.vo.StoreVo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ArrayList<StoreVo> storeList;

    private LocationManager locationManager;
    private Location location;
    private double lat; // 위도
    private double lon; // 경도

    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    private RelativeLayout lay_truck_info;
    private ImageView img_thumbnail;
    private TextView tv_name;
    private TextView tv_location;
    private TextView tv_phone;
    private ImageView[] img_star = new ImageView[5];

    private Calendar cal;
    private boolean isLaunchAddTruck = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.img_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isLaunchAddTruck) {
                    Intent intent = new Intent(MapsActivity.this, AddStoreListActivity.class);
                    startActivity(intent);
                    isLaunchAddTruck = false;
                }

                isLaunchAddTruck = true;
                handler.sendEmptyMessageDelayed(0, 500);

            }
        });

        storeList = (ArrayList<StoreVo>) getIntent().getSerializableExtra("data");

        lay_truck_info = (RelativeLayout) findViewById(R.id.lay_truck_info);
        img_thumbnail = (ImageView) findViewById(R.id.img_thumbnail);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        img_star[0] = (ImageView) findViewById(R.id.img_star1);
        img_star[1] = (ImageView) findViewById(R.id.img_star2);
        img_star[2] = (ImageView) findViewById(R.id.img_star3);
        img_star[3] = (ImageView) findViewById(R.id.img_star4);
        img_star[4] = (ImageView) findViewById(R.id.img_star5);

        findViewById(R.id.img_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, StoreListActivity.class);
                intent.putExtra("data", storeList);
                startActivity(intent);
            }
        });

        findViewById(R.id.img_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
                intent.putExtra("data", storeList);
                startActivity(intent);
            }
        });

        findViewById(R.id.lay_truck_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDetail((StoreVo) view.getTag());

            }
        });
        findViewById(R.id.tv_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDetail((StoreVo) view.getTag());
            }
        });

        getLocation();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isLaunchAddTruck = false;
        }
    };


    private void launchDetail(StoreVo data) {
        Intent intent = new Intent(MapsActivity.this, TruckInfoActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    public Location getLocation() {
        try {

            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;


            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
                showSettingsAlert();
            } else {

                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    }

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        }

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
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
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private Marker prevMarker;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(prevMarker != null) {
                    prevMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bluepin));
                }

                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_yelpin));
                showSelectedTruck((StoreVo) marker.getTag());
                prevMarker = marker;

                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                clearSelectedMarker();
            }
        });
        // Add a marker in Sydney and move the camera
        LatLng currPos = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currPos, 15));
        setMyPosition();
        setTruckOnMap();
    }
    private void clearSelectedMarker() {
        lay_truck_info.setVisibility(View.GONE);
        if(prevMarker == null) {
            return;
        }
        prevMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bluepin));
        prevMarker = null;
    }

    private void showSelectedTruck(StoreVo truck) {

        lay_truck_info.setVisibility(View.VISIBLE);
        lay_truck_info.setTag(truck);
        if(truck == null || truck.getName() == null) {
            return;

        }
        tv_name.setText(truck.getName());
        Glide.with(MapsActivity.this)
                .load(truck.getImg())
                .into(img_thumbnail);
        tv_phone.setText(truck.getPhone());
        tv_location.setText(truck.getLocation());

        int totalRate = 0;

        for (StoreReplyVo data : truck.getReplys() ) {
            totalRate += data.getRate();
        }
        if(totalRate == 0) {
            return;
        }

        setStars(img_star, totalRate/truck.getReplys().size());

    }
    private void setStars(ImageView[] img, double rate) {
        LogUtil.e(" rate:  " + rate);
        for (int i=0; i<img.length; i++) {
            if( i < rate) {
                img[i].setSelected(true);
            } else {
                img[i].setSelected(false);
            }
        }

    }
    private void setMyPosition() {
        mMap.addMarker(
                new MarkerOptions()
                .position(new LatLng(lat, lon))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_red_point))

        );
    }

    private void setTruckOnMap() {
        int size = storeList.size();
        for (int i=0; i<size; i++){
            getTruckMarker(storeList.get(i));
        }
    }

    private void getTruckMarker(StoreVo truck) {
        mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(truck.getLat(), truck.getLon()))
                        .title(truck.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bluepin))
        )
        .setTag(truck);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        this.location = location;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        LogUtil.e("onLocationChanged  ");

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
