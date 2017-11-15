package com.foodtruck.network;

import com.foodtruck.vo.ResponseVoBase;
import com.foodtruck.vo.StoreInfoVo;
import com.foodtruck.vo.StoreListVo;
import com.foodtruck.vo.StoreMenuListVo;
import com.foodtruck.vo.StoreMenuVo;
import com.foodtruck.vo.StoreReplyListVo;
import com.foodtruck.vo.StoreVo;
import com.foodtruck.vo.UpdateVo;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by evilstorm on 2017. 10. 29..
 */

interface RequestInterface {

    //트럭정보 가져오기
    @GET("api/truck")
    Call<StoreListVo> getTruckList();

    //트럭정보 가져오기
    @GET("api/truck/{truck_id}")
    Call<StoreVo> getTruck(@Path(value="truck_id") String id);
    //트럭 정보 올리기
    @POST("api/truck")
    Call<UpdateVo> postTruck(@Body JsonObject params);
    @PATCH("api/truck/{truck_id}")
    Call<ResponseVoBase> patchTruck(@Path(value="truck_id") String id, @Body JsonObject params);
    @DELETE("api/truck/{truck_id}")
    Call<ResponseVoBase> deleteTruck(@Path(value="truck_id") String id);

    //트럭 메뉴 정보 가져오기
    @GET("api/menu/{truck_id}")
    Call<StoreMenuListVo> getTruckMenu(@Path(value="truck_id") String id);
    //트럭 메뉴 정보 올리기
    @POST("api/menu/{truck_id}")
    Call<UpdateVo> postTruckMenu(@Path(value="truck_id") String id, @Body JsonObject params);
    //트럭 메뉴 정보 수정
    @PATCH("api/menu/{menu_id}")
    Call<ResponseVoBase> patchTruckMenu(@Path(value="menu_id") String id, @Body JsonObject params);
    @DELETE("api/menu/{menu_id}")
    Call<ResponseVoBase> deleteTruckMenu(@Path(value="menu_id") String id);

    //트럭 상세 정보 가져오기
    @GET("api/info/{truck_id}")
    Call<StoreInfoVo> getTruckInfo(@Path(value="truck_id") String id);
    //트럭 상세 정보 올리기
    @POST("api/info/{truck_id}")
    Call<UpdateVo> postTruckInfo(@Path(value="truck_id") String id, @Body JsonObject params);
    //트럭 상세 정보 수정
    @PATCH("api/info/{info_id}")
    Call<ResponseVoBase> patchTruckInfo(@Path(value="info_id") String id, @Body JsonObject params);
    //트럭 상세 정보 수정
    @DELETE("api/info/{info_id}")
    Call<ResponseVoBase> deleteTruckInfo(@Path(value="info_id") String id);

    //트럭 뎃글 정보 가져오기
    @GET("api/reply/{truck_id}")
    Call<StoreReplyListVo> getReply(@Path(value="truck_id") String id);
    //트럭 뎃글 올리기
    @POST("api/reply/{truck_id}")
    Call<UpdateVo> postReply(@Path(value="truck_id") String id, @Body JsonObject params);
    //트럭 상세 정보 수정
    @PATCH("api/reply/{reply_id}")
    Call<ResponseVoBase> patchReply(@Path(value="reply_id") String id, @Body JsonObject params);
    //트럭 상세 정보 수정
    @DELETE("api/reply/{reply_id}")
    Call<ResponseVoBase> deleteReply(@Path(value="reply_id") String id);




}
