package com.foodtruck.network;

import com.foodtruck.utils.Constants;
import com.foodtruck.utils.LogUtil;
import com.foodtruck.vo.ResponseVoBase;
import com.foodtruck.vo.StoreListVo;
import com.foodtruck.vo.UpdateVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by evilstorm on 2017. 10. 27..
 */

public class RequestApi {

    private Gson gson;

    private RequestApi() {
    }

    private static class Singleton {
        private static final RequestApi instance = new RequestApi();
    }

    public static RequestApi getInstance() {
        return Singleton.instance;
    }

    private static HashMap<String, String> HEADERS = new HashMap<String, String>() {
        {
            put("Content-Type", "application/vnd.api+json");
        }
    };

    public static String getBaseURL() {
        return Constants.API_BASE_URL;
    }


    private OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);
        httpClient.networkInterceptors().add(new Interceptor() {
            //        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.method(original.method(), original.body());
                Iterator<String> keys = HEADERS.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    requestBuilder.addHeader(key, HEADERS.get(key));
                }

                return chain.proceed(requestBuilder.build());
            }
        });

        return httpClient.build();
    }

    private Retrofit getClient() {

        if (gson == null) {
            //Tue, 01 Aug 2017 00:54:13 GMT
            gson = new GsonBuilder()
                    .setDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
//                    .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                    .create();

        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseURL())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(getOkHttpClient())
                .build();

        return retrofit;
    }

    public void getTruckList(final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<StoreListVo> result = service.getTruckList();

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }

    public void postTruck(JsonObject params, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<UpdateVo> result = service.postTruck(params);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }

    public void deleteTruck(String truck_id, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<ResponseVoBase> result = service.deleteTruck(truck_id);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }

    public void postTruckInfo(String truck_id, JsonObject params, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<UpdateVo> result = service.postTruckInfo(truck_id, params);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }

    public void patchTruckInfo(String info_id, JsonObject params, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<ResponseVoBase> result = service.patchTruckInfo(info_id, params);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }

    public void deleteTruckInfo(String info_id, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<ResponseVoBase> result = service.deleteTruckInfo(info_id);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }


    public void postTruckMenu(String truck_id, JsonObject params, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<UpdateVo> result = service.postTruckMenu(truck_id, params);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }

    public void patchTruckMenu(String menu_id, JsonObject params, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<ResponseVoBase> result = service.patchTruckMenu(menu_id, params);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }

    public void deleteTruckMenu(String menu_id, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<ResponseVoBase> result = service.deleteTruckMenu(menu_id);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }


    public void postReply(String truck_id, JsonObject params, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<UpdateVo> result = service.postReply(truck_id, params);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }

    public void patchReply(String reply_id, JsonObject params, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<ResponseVoBase> result = service.patchReply(reply_id, params);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }

    public void deleteReply(String reply_id, final NetworkResponse responseListener) {
        RequestInterface service = getClient().create(RequestInterface.class);
        Call<ResponseVoBase> result = service.deleteReply(reply_id);

        Callback callback = new Callback<ResponseVoBase>() {
            @Override
            public void onResponse(Call<ResponseVoBase> call, retrofit2.Response<ResponseVoBase> response) {
                returnResponse(call, response, responseListener);
            }

            @Override
            public void onFailure(Call<ResponseVoBase> call, Throwable t) {
                responseListener.onFail(call, t.toString());
            }
        };
        result.enqueue(callback);
    }


    private synchronized void returnResponse(Call call, retrofit2.Response response, NetworkResponse responseListener) {
        LogUtil.e(call.request().url() + " // response.code() : " + response.code());

        if (response.isSuccessful()) {
            responseListener.onSuccess(call, response.body());
        } else {
            try {
                responseListener.onFail(call, new String(response.errorBody().bytes()));
            } catch (Exception e) {
                responseListener.onFail(call, "데이터 수신에 실패했습니다. 잠시 후 다시 시도해주세요.");
            }
        }
    }


}
