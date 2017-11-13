package com.foodtruck.network;

import com.foodtruck.vo.ResponseVoBase;
import retrofit2.Call;

/**
 * Created by Administrator on 2017-04-27.
 */

public interface NetworkResponse<T> {
    void onSuccess(Call<ResponseVoBase> call, T clazz);
    void onFail(Call<ResponseVoBase> call, String msg);
}
