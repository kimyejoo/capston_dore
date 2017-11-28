package com.foodtruck.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.foodtruck.R;
import com.foodtruck.network.NetworkResponse;
import com.foodtruck.network.RequestApi;
import com.foodtruck.utils.LogUtil;
import com.foodtruck.utils.StringUtil;
import com.foodtruck.vo.ResponseVoBase;
import com.foodtruck.vo.UpdateVo;
import com.google.gson.JsonObject;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

/**
 * Created by evilstorm on 2017. 11. 13..
 */

public class AddTruckActivity extends Activity {

    private final int TAKE_PICTURE = 324;

    private ImageView img_thumbnail;
    private EditText et_name;
    private EditText et_local;
    private EditText et_phone;

    private String imageUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_truck);

        img_thumbnail = (ImageView) findViewById(R.id.img_thumbnail);
        et_name = (EditText) findViewById(R.id.et_name);
        et_local = (EditText) findViewById(R.id.et_local);
        et_phone = (EditText) findViewById(R.id.et_phone);

        img_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoto();
            }
        });
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTruck();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 주소를 위경도로 변환
     * @param local 주소
     * @return [0] = lat, [1] = lon
     */
    private double[] changeGeoCoder(String local) {
        Geocoder mCoder = new Geocoder(this);
        double[] result = new double[2];

        try {
            //주소값을 통하여 로케일을 받아온다
            List<Address> addr = mCoder.getFromLocationName(local, 1);
            //해당 로케일로 좌표를 구성한다
            result[0] = addr.get(0).getLatitude();
            result[1] =  addr.get(0).getLongitude();
        } catch (Exception e) {
            return null;
        }

        return result;
    }

    private void addTruck() {
        if(StringUtil.isNull(et_name)) {
            Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isNull(et_local)) {
            Toast.makeText(this, "주소를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isNull(et_phone)) {
            Toast.makeText(this, "연락처를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isNull(imageUrl)) {
            Toast.makeText(this, "이미지를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }



        double[] geoCode = changeGeoCoder(StringUtil.getString(et_local));

        JsonObject params = new JsonObject();
        params.addProperty("name", StringUtil.getString(et_name));
        params.addProperty("location", StringUtil.getString(et_local));
        params.addProperty("phone", StringUtil.getString(et_phone));
        params.addProperty("img", imageUrl);
        params.addProperty("lat", geoCode[0]);
        params.addProperty("lon", geoCode[1]);

        RequestApi.getInstance().postTruck(params, new NetworkResponse<UpdateVo>() {
            @Override
            public void onSuccess(Call call, UpdateVo clazz) {
                LogUtil.e(" clazz.get_id(): " + clazz.get_id());
                launchTruckInfo(clazz.get_id());
            }

            @Override
            public void onFail(Call call, String msg) {
                Toast.makeText(AddTruckActivity.this, " Error : " + msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void launchTruckInfo(String truck_id) {
        Intent intent = new Intent(this, AddTruckInfoActivity.class);
        intent.putExtra("truck_id", truck_id);
        startActivity(intent);
        finish();
    }

    private void addPhoto() {
        Intent intent = new Intent(this, AddPictureActivity.class);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == TAKE_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("uri");
                if(data.getStringExtra("tempPath") != null) {
                    LogUtil.e(" tempPath " + data.getStringExtra("tempPath"));

                    changeUserPicture(data.getStringExtra("tempPath"));
                    new AzureFileUpload(data.getStringExtra("tempPath")).execute();

                }
            }
        }
        return;
    }

    private class AzureFileUpload extends AsyncTask<Void, Void, Void> {

        private String filePath;
        private static final String storageURL = "https://capstonfood.blob.core.windows.net/";
        private static final String storageContainer = "capfood";
        // Define the connection-string with your values
        public static final String storageConnectionString =
                "DefaultEndpointsProtocol=http;" +
                        "AccountName=capstonfood;" +
                        "AccountKey=qzhix2vqFtDnoTgYf7gh6OcUid0y5VEmSih+pqrSakzfVJLsLLJpbX2tdYOYWTlqhyNf2r8tGJZ21JSH7EBu7A==";

        public AzureFileUpload(String filePath) {
            this.filePath = filePath;
        }


        protected void storeImageInBlobStorage(String imgPath){
            try
            {

                CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                CloudBlobContainer container = blobClient.getContainerReference(storageContainer);

                long times = Calendar.getInstance().getTimeInMillis();
                CloudBlockBlob blob = container.getBlockBlobReference(times + ".jpg");
                File source = new File(imgPath);
                blob.upload(new FileInputStream(source), source.length());

//                imageUrl = "https://capfood.blob.core.windows.net/capfood/" +times+ ".jpg";
                imageUrl = "https://capstonfood.blob.core.windows.net/capfood/" +times+ ".jpg";


            }
            catch (Exception e)
            {
                e.printStackTrace();
                LogUtil.e(" Exception e : " + e.toString());
            }

        }


        @Override
        protected Void doInBackground(Void... voids) {
            storeImageInBlobStorage(filePath);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }



    private void changeUserPicture(String url) {
        Glide.with(AddTruckActivity.this)
                .load(url)
                .into(img_thumbnail);
    }

























}
