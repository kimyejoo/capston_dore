package com.foodtruck.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.foodtruck.R;

/**
 * Created by evilstorm on 2017. 11. 6..
 */

public class TruckInfoActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.truck_info_activity);
    }
}
