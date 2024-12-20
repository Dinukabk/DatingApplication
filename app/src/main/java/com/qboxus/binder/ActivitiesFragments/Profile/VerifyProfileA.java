package com.qboxus.binder.ActivitiesFragments.Profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.ApiClasses.FileUploader;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.interfaces.FragmentCallback;
import com.qboxus.binder.SimpleClasses.PermissionUtils;
import com.qboxus.binder.SimpleClasses.Variables;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VerifyProfileA extends AppCompatLocaleActivity implements View.OnClickListener {

    SimpleDraweeView userImage;

    TextView takePhotoBtn,whyneedbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);
        setContentView(R.layout.activity_verify_profile);
        findViewById(R.id.back_btn).setOnClickListener(this);


        takePhotoBtn= findViewById(R.id.takePhotoBtn);
        takePhotoBtn.setOnClickListener(this);

        whyneedbtn=findViewById(R.id.whyneedbtn);
        whyneedbtn.setOnClickListener(this);

        userImage=findViewById(R.id.userImage);


      }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;

            case R.id.takePhotoBtn:
                if(selectedImage!=null){
                    uploadProfileVideo(imageFilePath);
                }
                else {
                    takePhotoFromCamera();
                }
                break;

            case R.id.whyneedbtn:
                if(selectedImage!=null) {
                    takePhotoFromCamera();
                }
                else {
                    VerifyMessageBottomSheet verifyMessageBottomSheet = VerifyMessageBottomSheet.newInstance(new FragmentCallback() {
                        @Override
                        public void responce(Bundle bundle) {
                            if (bundle != null) {
                                takePhotoFromCamera();
                            }
                        }
                    });
                    verifyMessageBottomSheet.show(getSupportFragmentManager(), "VerifyMessageBottomSheet");

                }
                break;
        }
    }

    private void takePhotoFromCamera() {
        takePermissionUtils=new PermissionUtils(VerifyProfileA.this,mPermissionResult);
        if (takePermissionUtils.isStorageCameraPermissionGranted()) {
            openCameraIntent();
        }
        else
        {
            takePermissionUtils.showStorageCameraPermissionDailog(getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
        }
    }

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(VerifyProfileA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(VerifyProfileA.this,getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                    }
                    else
                    if (allPermissionClear)
                    {
                        openCameraIntent();
                    }

                }
            });


    // below three method is related with taking the picture from camera
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        //Create a file to store the image
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this.getApplicationContext(), getPackageName() + ".fileprovider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraResultCallback.launch(pictureIntent);
        }
    }

    String imageFilePath;
    Uri selectedImage;
    PermissionUtils takePermissionUtils;
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }


    ActivityResultLauncher<Intent> cameraResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Matrix matrix = new Matrix();
                        try {
                            ExifInterface exif = new ExifInterface(imageFilePath);
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.postRotate(90);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.postRotate(180);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    matrix.postRotate(270);
                                    break;
                            }

                            selectedImage = (Uri.fromFile(new File(imageFilePath)));

                            userImage.setVisibility(View.VISIBLE);
                            userImage.setImageURI(selectedImage);
                            findViewById(R.id.beforeSelectedLayout).setVisibility(View.GONE);
                            findViewById(R.id.afterSelectedLayout).setVisibility(View.VISIBLE);
                            takePhotoBtn.setText(R.string.agree_and_submit);
                            whyneedbtn.setText(R.string.retake);

                           // beginCrop(selectedImage);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        catch (Exception e){}
                    }
                }
            });

    private void beginCrop(Uri source) {
        Intent intent= CropImage.activity(source).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1,1).getIntent(VerifyProfileA.this);
        cropResultCallback.launch(intent);
    }

    ActivityResultLauncher<Intent> cropResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                        Intent data = result.getData();
                        CropImage.ActivityResult result1 = CropImage.getActivityResult(data);
                        handleCrop(result1.getUri());
                    }
                }
            });

    private void handleCrop( Uri userImageUri) {
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(userImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);

        String path = userImageUri.getPath();
        Matrix matrix = new Matrix();
        android.media.ExifInterface exif = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                exif = new android.media.ExifInterface(path);
                int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

       String imageBas64 = Functions.bitmapToBase64(this, rotatedBitmap);

       uploadProfileVideo(path);
    }



    private void uploadProfileVideo(String filepath) {
        Functions.showLoader(this,false,false);
        String userId=Functions.getSharedPreference(this).getString(Variables.uid, "");
        FileUploader fileUploader = new FileUploader(new File(filepath),getApplicationContext(),userId);
        fileUploader.SetCallBack(new FileUploader.FileUploaderCallback() {
            @Override
            public void onError() {
                Functions.cancelLoader();

            }

            @Override
            public void onFinish(String responses) {
                Functions.cancelLoader();
                Functions.printLog(responses);
                try {
                    JSONObject jsonObject = new JSONObject(responses);
                    int code = jsonObject.optInt("code",0);
                    if (code!=200) {
                        Functions.showAlert(VerifyProfileA.this,"Verify Profile",jsonObject.optString("msg"),null);
                    }
                    else {
                        Functions.showAlert(VerifyProfileA.this,"Verify Profile","Profile verified successfully",null);
                    }

                } catch (Exception e) {
                    Functions.printLog("Exception: "+e);
                }
            }

            @Override
            public void onProgressUpdate(int currentpercent, int totalpercent,String msg) {
                //send progress broadcast
                if (currentpercent>0)
                {

                }
            }
        });


    }



}