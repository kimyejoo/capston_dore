package com.foodtruck.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.foodtruck.R;
import com.foodtruck.network.NetworkResponse;
import com.foodtruck.network.RequestApi;
import com.foodtruck.utils.LogUtil;
import com.foodtruck.utils.StringUtil;
import com.foodtruck.vo.StoreMenuVo;
import com.foodtruck.vo.StoreVo;
import com.foodtruck.vo.UpdateVo;
import com.google.gson.JsonObject;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;

/**
 * Created by evilstorm on 2017. 11. 15..
 */

public class AddMenuActivity extends Activity {
    private final int TAKE_PICTURE = 324;

    private String truck_id;
    private String imageUrl;

    private ImageView img_thumbnail;
    private EditText et_name;
    private EditText et_price;
    private EditText et_brief;
    private LinearLayout lay_row;
    private StoreVo storeVo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        truck_id  = getIntent().getStringExtra("truck_id");
        storeVo  = (StoreVo) getIntent().getSerializableExtra("data");

        lay_row = (LinearLayout) findViewById(R.id.lay_row);

        img_thumbnail = (ImageView) findViewById(R.id.img_thumbnail);
        et_name = (EditText) findViewById(R.id.et_name);
        et_price = (EditText) findViewById(R.id.et_price);
        et_brief = (EditText) findViewById(R.id.et_brief);

        img_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoto();
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMenu();
            }
        });
        findViewById(R.id.btn_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        prevMenuAdd(storeVo);
    }

    private void addMenu() {
        if(StringUtil.isNull(et_name)) {
            Toast.makeText(this, "메뉴 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isNull(et_price)) {
            Toast.makeText(this, "가격 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isNull(et_brief)) {
            Toast.makeText(this, "메뉴 설명을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isNull(imageUrl)) {
            Toast.makeText(this, "사진을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }


        JsonObject params = new JsonObject();
        params.addProperty("img", imageUrl);
        params.addProperty("price", StringUtil.getString(et_price));
        params.addProperty("name", StringUtil.getString(et_name));
        params.addProperty("desc", StringUtil.getString(et_brief));

        RequestApi.getInstance().postTruckMenu(truck_id, params, new NetworkResponse<UpdateVo>() {
            @Override
            public void onSuccess(Call call, UpdateVo clazz) {
                addmenuList(clazz.get_id());
            }

            @Override
            public void onFail(Call call, String msg) {

            }
        });
    }

    private void prevMenuAdd(StoreVo data) {
        if(data == null || data.getMenus() == null) {
            return;
        }
        ArrayList<StoreMenuVo> list = data.getMenus();
        for (StoreMenuVo item: list) {
            addPrevMenuList(item);
        }

    }
    private void addPrevMenuList(StoreMenuVo data) {
        View menuView = getLayoutInflater().inflate(R.layout.row_add_menu, null);
        ImageView img = (ImageView) menuView.findViewById(R.id.img_thumbnail);
        TextView tv_name = (TextView) menuView.findViewById(R.id.tv_name);
        TextView tvt_price = (TextView) menuView.findViewById(R.id.tv_price);
        TextView tv_brief = (TextView) menuView.findViewById(R.id.tv_brief);


        Glide.with(AddMenuActivity.this)
                .load(data.getImg())
                .into(img);
        tv_name.setText(data.getName());
        tvt_price.setText(data.getPrice() + "");
        tv_brief.setText(data.getDesc());

        menuView.setTag(data.get_id());

        menuView.setOnClickListener(deleteMenu);
        lay_row.addView(menuView);

    }

    private View.OnClickListener deleteMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            deleteReplyAlert((String) view.getTag(), view);
        }
    };

    private void deleteReplyAlert(final String reply_id, final View view) {
        new AlertDialog.Builder(AddMenuActivity.this)
                .setMessage("삭제 하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(AddMenuActivity.this, "삭제 되었습니다..", Toast.LENGTH_SHORT).show();
                        deleteMenu((String)view.getTag(), view);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }
    private void deleteMenu(final String menu_id, final View view) {
        RequestApi.getInstance().deleteTruckMenu(menu_id, new NetworkResponse() {
            @Override
            public void onSuccess(Call call, Object clazz) {
                lay_row.removeView(view);
            }

            @Override
            public void onFail(Call call, String msg) {
                Toast.makeText(AddMenuActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addmenuList(String menuId) {
        View menuView = getLayoutInflater().inflate(R.layout.row_add_menu, null);
        ImageView img = (ImageView) menuView.findViewById(R.id.img_thumbnail);
        TextView tv_name = (TextView) menuView.findViewById(R.id.tv_name);
        TextView tvt_price = (TextView) menuView.findViewById(R.id.tv_price);
        TextView tv_brief = (TextView) menuView.findViewById(R.id.tv_brief);


        Glide.with(AddMenuActivity.this)
                .load(imageUrl)
                .into(img);
        tv_name.setText(StringUtil.getString(this.et_name));
        tvt_price.setText(StringUtil.getString(this.et_price));
        tv_brief.setText(StringUtil.getString(this.et_brief));

        menuView.setTag(menuId);
        menuView.setOnClickListener(deleteMenu);

        lay_row.addView(menuView);

        this.et_name.setText("");
        this.et_price.setText("");
        this.et_brief.setText("");
        this.img_thumbnail.setImageResource(R.drawable.camera);
        imageUrl = null;

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
        private static final String storageURL = "https://capfood.blob.core.windows.net/";
        private static final String storageContainer = "capfood";
        // Define the connection-string with your values
        public static final String storageConnectionString =
                "DefaultEndpointsProtocol=http;" +
                        "AccountName=capfood;" +
                        "AccountKey=hpwKySibe4JgNNTe59aDUCGtsaKIBwTnmrfH260lUfrBo5H1LHulXKWhS6Q7TNGTuRxNvcpX95OVKCAiCwiGww==";

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

                imageUrl = "https://capfood.blob.core.windows.net/capfood/" +times+ ".jpg";

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
        Glide.with(AddMenuActivity.this)
                .load(url)
                .into(img_thumbnail);
    }



}
