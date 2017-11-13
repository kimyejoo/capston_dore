package com.foodtruck.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.foodtruck.R;
import com.foodtruck.utils.Constants;
import com.foodtruck.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by evilstorm on 2017. 7. 6..
 */

public class AddPictureActivity extends Activity {

    private final int TAKE_PICTURE = 1;
    private final int TAKE_ALBUM = 2;
    private final int CROP_FROM_CAMERA = 3;

    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    private int isSelectedType = 0;
    private Uri photoUri;

    private int cropWidth;
    private int cropHeight;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.picture_select_activity);

        findViewById(R.id.tv_take_picture).setTag(TAKE_PICTURE);
        findViewById(R.id.tv_take_album).setTag(TAKE_ALBUM);
        findViewById(R.id.tv_take_picture).setOnClickListener(getPictureListener);
        findViewById(R.id.tv_take_album).setOnClickListener(getPictureListener);

        cropWidth = getIntent().getIntExtra("cropWidth", 128);
        cropHeight = getIntent().getIntExtra("cropHeight", 128);

        setResult(Activity.RESULT_CANCELED);

    }



    private boolean checkPermissions() {
            int readPerimission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePerimission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(writePerimission == PackageManager.PERMISSION_DENIED || readPerimission  == PackageManager.PERMISSION_DENIED){
                //권한 없음
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MULTIPLE_PERMISSIONS);
                return false;
            }
        return true;
    }

    //아래는 권한 요청 Callback 함수입니다. PERMISSION_GRANTED로 권한을 획득했는지 확인할 수 있습니다. 아래에서는 !=를 사용했기에
    //권한 사용에 동의를 안했을 경우를 if문으로 코딩되었습니다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            showNoPermissionToastAndFinish();
                            return;
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                    return;
                }

                if(isSelectedType == TAKE_PICTURE) {
                    takePhoto();
                } else {
                    goToAlbum();
                }

                return;
            }
        }
    }


    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다.", Toast.LENGTH_SHORT).show();
    }


    private View.OnClickListener getPictureListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isSelectedType = (int)view.getTag();
            if(!checkPermissions()){
                return;
            }
            if(TAKE_PICTURE == (int)view.getTag()) {
                takePhoto();
            } else {
                goToAlbum();
            }
        }
    };

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정합니다.
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(AddPictureActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(AddPictureActivity.this, "com.foodtruck.provider", photoFile); //FileProvider의 경우 이전 포스트를 참고하세요.
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Constants.PICTURE_TEMO_PATH); //test라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK); //ACTION_PICK 즉 사진을 고르겠다!
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, TAKE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(AddPictureActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == TAKE_ALBUM) {
            if(data==null){
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == TAKE_PICTURE) {
            cropImage();
            MediaScannerConnection.scanFile(AddPictureActivity.this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            try { //저는 bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, cropWidth, cropHeight);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                thumbImage.compress(Bitmap.CompressFormat.JPEG, 90, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축

                //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.
//                img_pic.setImageBitmap(bitmap);

//                CooingApplication app = (CooingApplication)getApplication();
//                app.picture = thumbImage;
                Intent intent = new Intent();
                intent.putExtra("uri", photoUri);
                intent.putExtra("tempPath", cropFilePath);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LogUtil.e(" onDestroy ");
    }

    private String cropFilePath = null;

    public void cropImage() {
        this.grantUriPermission("com.android.camera", photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("crop", "true");
            if(cropWidth != 128) {
                intent.putExtra("aspectX", 2);
                intent.putExtra("aspectY", 1);
            } else {
                intent.putExtra("aspectX", 3);
                intent.putExtra("aspectY", 3);
            }

            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Constants.PICTURE_TEMO_PATH);
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            cropFilePath = tempFile.getAbsolutePath();

            photoUri = FileProvider.getUriForFile(AddPictureActivity.this, "com.foodtruck.provider", tempFile);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            grantUriPermission(res.activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);


        }
    }

}
