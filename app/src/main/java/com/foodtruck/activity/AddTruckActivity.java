package com.foodtruck.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.foodtruck.R;
import com.foodtruck.utils.LogUtil;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by evilstorm on 2017. 11. 13..
 */

public class AddTruckActivity extends Activity {

    private final int TAKE_PICTURE = 324;
    private String photoUrl;

    private ImageView img_thubmnail;
    private EditText et_name;
    private EditText et_local;
    private EditText et_phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_truck);

        img_thubmnail = (ImageView) findViewById(R.id.img_thumbnail);
        et_name = (EditText) findViewById(R.id.et_name);
        et_local = (EditText) findViewById(R.id.et_local);
        et_phone = (EditText) findViewById(R.id.et_phone);

        img_thubmnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoto();
            }
        });
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
                    new AzureFileUpload(data.getStringExtra("tempPath")).execute();

                }


//                getUploadUrl(data.getStringExtra("tempPath"), uri);
//                storeImageInBlobStorage(uri.getPath());
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
                        "AccountName=gevilstorm@gmail.com;" +
                        "AccountKey=hpwKySibe4JgNNTe59aDUCGtsaKIBwTnmrfH260lUfrBo5H1LHulXKWhS6Q7TNGTuRxNvcpX95OVKCAiCwiGww==";

        public AzureFileUpload(String filePath) {
            this.filePath = filePath;
        }


        protected void storeImageInBlobStorage(String imgPath){
            try
            {
                // Retrieve storage account from connection-string.
                CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                // Create the blob client.
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                // Retrieve reference to a previously created container.
                CloudBlobContainer container = blobClient.getContainerReference(storageContainer);


                // Create a permissions object
                BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

// Include public access in the permissions object
                containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

// Set the permissions on the container
                container.uploadPermissions(containerPermissions);


                // Create or overwrite the "myimage.jpg" blob with contents from a local file.
                CloudBlockBlob blob = container.getBlockBlobReference("myimage.jpg");
                File source = new File(imgPath);
                blob.upload(new FileInputStream(source), source.length());
                LogUtil.e(" upload ....... 22222 ");



            }
            catch (Exception e)
            {
                // Output the stack trace.
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

            LogUtil.e(" Upload eND !!!!!!!!! ");
        }
    }



    private void changeUserPicture(String url) {
        Glide.with(AddTruckActivity.this)
                .load(url)
                .into(img_thubmnail);
    }


































}
