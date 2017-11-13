package com.foodtruck.network;

import com.foodtruck.vo.StoreListVo;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by evilstorm on 2017. 10. 29..
 */

interface RequestInterface {

    //업데이트 정보 가져오기
    @GET("api/truck")
    Call<StoreListVo> getTruckList();



}
