package com.foodtruck.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by evilstorm on 2017. 10. 30..
 */

public class SearchResultActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String msg = getIntent().getStringExtra("word");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
