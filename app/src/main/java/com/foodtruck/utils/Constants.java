package com.foodtruck.utils;

import android.os.Environment;

/**
 * Created by evilstorm on 2017. 11. 6..
 */

public class Constants {
    //최소 화면 보여주는 시간  4초
    public static final int HOLODING_TIME = 1;

    public static final String API_BASE_URL = "http://capfoodtruck.azurewebsites.net";
    public static final String PICTURE_TEMO_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/capfoodtruck/picture/";

}
