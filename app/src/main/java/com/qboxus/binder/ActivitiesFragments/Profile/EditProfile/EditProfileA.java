package com.qboxus.binder.ActivitiesFragments.Profile.EditProfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.qboxus.binder.ApiClasses.ApiLinks;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import com.qboxus.binder.ActivitiesFragments.Profile.VerifyProfileA;
import com.qboxus.binder.Adapters.ProfilePhotosAdapter;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.ItemMoveCallback;
import com.qboxus.binder.ViewHolders.PhotosviewHolder;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.SimpleClasses.PermissionUtils;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.MainMenu.MainMenuA;
import com.qboxus.binder.Models.UserMultiplePhotoModel;
import com.qboxus.binder.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileA extends AppCompatLocaleActivity implements View.OnClickListener,ItemMoveCallback.ItemTouchHelperContract  {

    ImageButton backBtn;
    EditText aboutEdit, jobTitleEdit, companyEdit;
    TextView counterTv,passionsTv, schoolTv;
    RadioButton maleBtn, femaleBtn;
    SwitchCompat hideAge, hideDistance;

    String imageBas64,selectedSchoolId="";
    RecyclerView profilePhotoList;
    UserMultiplePhotoModel model = new UserMultiplePhotoModel();

    int position=0;
    ProfilePhotosAdapter adapter;

    List<UserMultiplePhotoModel> imagesList;
    TextView doneTxt, profileNameTxt,verifiedStatusTxt;
    StringBuilder sb = new StringBuilder();
    ArrayList<String> passionIds = new ArrayList<>();
    PermissionUtils takePermissionUtils;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);
        setContentView(R.layout.activity_edit_profile);

        profileNameTxt = findViewById(R.id.profile_name_txt);
        profileNameTxt.setText(getResources().getString(R.string.about)+" "+
                Functions.getSharedPreference(this).getString(Variables.fName,""));

        imagesList = new ArrayList<>();

        profilePhotoList = findViewById(R.id.Profile_photos_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        profilePhotoList.setLayoutManager(layoutManager);
        profilePhotoList.setHasFixedSize(false);

        aboutEdit = findViewById(R.id.about_user);
        counterTv = findViewById(R.id.textCounterTv);
        aboutEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    counterTv.setText(""+(500 - s.length()));
                }
            }
        });

        passionsTv = findViewById(R.id.passionsTv);
        passionsTv.setOnClickListener(this);
        jobTitleEdit = findViewById(R.id.jobtitle_edit);
        companyEdit = findViewById(R.id.company_edit);
        schoolTv = findViewById(R.id.school_edit);
        schoolTv.setOnClickListener(this);
        maleBtn = findViewById(R.id.male_btn);
        femaleBtn = findViewById(R.id.female_btn);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        doneTxt = findViewById(R.id.done_txt);
        doneTxt.setOnClickListener(this);
        verifiedStatusTxt=findViewById(R.id.verifiedStatusTxt);
        findViewById(R.id.verifyLayout).setOnClickListener(this);

        /*hideAge = view.findViewById(R.id.hide_age);
        hideAge.setChecked( Functions.getSharedPreference(context).getBoolean(Variables.hideAge,false));
        hideAge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 Functions.getSharedPreference(context).edit().putBoolean(Variables.hideAge,isChecked).commit();
                callApiEditProfile();
            }
        });

        hideDistance =view.findViewById(R.id.hide_distance);
        hideDistance.setChecked( Functions.getSharedPreference(context).getBoolean(Variables.hide_distance,false));
        hideDistance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 Functions.getSharedPreference(context).edit().putBoolean(Variables.hide_distance,isChecked).commit();
                callApiEditProfile();
            }
        });*/


        if(Functions.getSharedPreference(this).getString(Variables.school, "").equals("null")){
            schoolTv.setText(getString(R.string.add_university));
        }else {
            schoolTv.setText(Functions.getSharedPreference(this).getString(Variables.school, ""));
        }

        callApiShowUserDetail();

    }



    // open the gallery when user press button to upload a picture
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultCallback.launch(intent);
    }


    // bottom there function are related to crop the image
    private void beginCrop(Uri source) {
        Intent intent=CropImage.activity(source).setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1,1).getIntent(EditProfileA.this);
        cropResultCallback.launch(intent);
    }


    private void handleCrop( Uri userimageuri) {
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(userimageuri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

        String path=userimageuri.getPath();
        Matrix matrix = new Matrix();
        android.media.ExifInterface exif = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
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

        Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0,
                imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        imageBas64 = Functions.bitmapToBase64(EditProfileA.this,rotatedBitmap);

        callApiForAddUserImage(false);

    }


    // after save the image in firebase we will save the image url in our server
    private void callApiForAddUserImage(boolean isChangeSequence) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(getApplicationContext())
                    .getString(Variables.uid,""));

            if(model.getImage() != null && !isChangeSequence){
                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();
                object.put("id", model.getId());
                object.put("order_sequence", position);
                object.put("image", imageBas64);
                array.put(object);
                parameters.put("sequence",array);
            }else if(model.getImage() == null && !isChangeSequence){
                parameters.put("image", imageBas64);
                parameters.put("order_sequence_single", position);
            } else if(isChangeSequence){
                JSONArray array = new JSONArray();
                for(int i = 0; i<imagesList.size(); i++){
                    UserMultiplePhotoModel model = imagesList.get(i);
                    if(model.getImage() != null){
                        JSONObject object = new JSONObject();
                        object.put("id", model.getId());
                        object.put("order_sequence", i);
                        array.put(object);
                    }
                }
                parameters.put("sequence",array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.generateNoteOnSD(EditProfileA.this, "parameters_uploadImages",parameters.toString());

        Functions.showLoader(EditProfileA.this,false,false);
        ApiRequest.callApi(EditProfileA.this, ApiLinks.addUserImage, parameters, resp -> {
            try {
                JSONObject jsonObject=new JSONObject(resp);
                String code = jsonObject.optString("code");
                if(code.equals("200")){
                    callApiShowUserDetail();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

    }


    // this method will call when we click for delete the profile images
    private void callApiForDeleteLink() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", model.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(EditProfileA.this,false,false);
        ApiRequest.callApi(EditProfileA.this, ApiLinks.deleteUserImage, parameters, resp -> {
           Functions.cancelLoader();
            try {
                JSONObject jsonObject=new JSONObject(resp);
                String code=jsonObject.optString("code");
                if(code.equals("200")){
                    callApiShowUserDetail();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }


    // below two method is used get the user pictures and about text from our server
    private void callApiShowUserDetail() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(EditProfileA.this)
                    .getString(Variables.uid,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(Functions.dialog == null){
            Functions.showLoader(EditProfileA.this,false,false);
        }
        ApiRequest.callApi(EditProfileA.this, ApiLinks.showUserDetail, parameters, resp -> {
            Functions.cancelLoader();
            parseUserInfo(resp);
        });
    }


    @SuppressLint("NonConstantResourceId")
    public void parseUserInfo(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONObject msg = jsonObject.optJSONObject("msg");
                JSONObject userdata = msg.optJSONObject("User");
                JSONObject schoolObject = msg.optJSONObject("School");
                JSONArray userImagesArray = msg.optJSONArray("UserImage");
                JSONArray userPassionsArray = msg.optJSONArray("UserPassion");

                imagesList.clear();
                oldimagesList.clear();

                for(int i = 0; i<6; i++){
                    UserMultiplePhotoModel model = new UserMultiplePhotoModel();
                    if(i<userImagesArray.length()){
                        model.setImage(userImagesArray.optJSONObject(i).optString("image"));
                        model.setId(userImagesArray.optJSONObject(i).getString("id"));
                        model.setOrderSequence(Integer.parseInt(userImagesArray
                                .optJSONObject(i).getString("order_sequence")));
                        imagesList.add(i, model);
                        oldimagesList.add(i,model);
                    }else {
                        model.setOrderSequence(i);
                        imagesList.add(i, model);
                        oldimagesList.add(i,model);
                    }
                }

                Collections.sort(imagesList, (p1, p2) -> {
                    return p1.getOrderSequence() - p2.getOrderSequence(); // Ascending
                });

                Collections.sort(oldimagesList, (p1, p2) -> {
                    return p1.getOrderSequence() - p2.getOrderSequence(); // Ascending
                });


                if(imagesList.size()>0){
                    Functions.getSharedPreference(EditProfileA.this).edit()
                            .putString(Variables.uPic, imagesList.get(0).getImage()).apply();
                    MainMenuA.userPic = Functions
                            .getSharedPreference(EditProfileA.this)
                            .getString(Variables.uPic,"");
                }

                aboutEdit.setText(userdata.optString("bio"));
                jobTitleEdit.setText(userdata.optString("job_title"));
                companyEdit.setText(userdata.optString("company"));

                if(schoolObject.optString("name").equals("null")){
                    schoolTv.setText(getString(R.string.add_university));
                }else {
                    schoolTv.setText(schoolObject.optString("name"));
                }

                if(userdata.optString("gender").toLowerCase().equals("male")){
                    maleBtn.setChecked(true);
                }else if(userdata.optString("gender").toLowerCase().equals("female")){
                    femaleBtn.setChecked(true);
                }
                String verified=userdata.optString("verified","0");
                if(verified.equalsIgnoreCase("1")){
                    verifiedStatusTxt.setText(R.string.verified);
                }else {
                    verifiedStatusTxt.setText(R.string.not_verified);
                }

                sb = new StringBuilder();
                for(int i =0; i < userPassionsArray.length(); i++) {
                    JSONObject passionObject = userPassionsArray.optJSONObject(i).optJSONObject("Passion");

                    sb.append(passionObject.optString("title"));
                    sb.append(", ");
                }
                if(sb != null && sb.length() > 2){
                    sb.deleteCharAt(sb.length()-2);
                    passionsTv.setText(sb);
                }else {
                    passionsTv.setText(getString(R.string.add_passions));
                }

                adapter = new ProfilePhotosAdapter(EditProfileA.this,
                        imagesList, false, (item, postion, view) -> {
                            model = item;
                            position = postion;
                            aboutEdit.clearFocus();
                            companyEdit.clearFocus();
                            jobTitleEdit.clearFocus();
                            Functions.hideSoftKeyboard(EditProfileA.this);
                            switch (view.getId()){
                                case R.id.cross_btn:
                                    showPopup();
                                    break;

                                case R.id.add_btn:
                                    takePermissionUtils=new PermissionUtils(EditProfileA.this,mPermissionResult);
                                    if (takePermissionUtils.isStorageCameraPermissionGranted()) {
                                        selectImage();
                                    }
                                    else
                                    {
                                        takePermissionUtils.showStorageCameraPermissionDailog(getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                                    }
                                    break;
                            }
                        });


                ItemTouchHelper.Callback callback =
                        new ItemMoveCallback(this);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(profilePhotoList);

                profilePhotoList.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                            blockPermissionCheck.add(Functions.getPermissionStatus(EditProfileA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(EditProfileA.this,getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                    }
                    else
                    if (allPermissionClear)
                    {
                        selectImage();
                    }

                }
            });

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        Functions.printLog("fromPosition: "+fromPosition+" ToPosition: "+toPosition);
        if(TextUtils.isEmpty(imagesList.get(fromPosition).getImage())){

        }else {

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(imagesList, i, i + 1);
                }
            }

            else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(imagesList, i, i - 1);
                }
            }

            adapter.notifyItemMoved(fromPosition, toPosition);
        }


    }



    @Override
    public void onRowSelected(PhotosviewHolder myViewHolder) {

    }

    List<UserMultiplePhotoModel> oldimagesList=new ArrayList<>();
    @Override
    public void onRowClear(PhotosviewHolder myViewHolder) {
        if(!oldimagesList.equals(imagesList))
        callApiForAddUserImage(true);
    }


    // on done btn press this method will call
    // below two method is user for save the change in our profile which we have done
    private void callApiEditProfile() {
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("user_id", Functions.getSharedPreference(EditProfileA.this)
                    .getString(Variables.uid,""));
            parameters.put("bio", aboutEdit.getText().toString());
            parameters.put("job_title", jobTitleEdit.getText().toString());
            parameters.put("company", companyEdit.getText().toString());
            if(!selectedSchoolId.equals("")){
                parameters.put("school_id", selectedSchoolId);
            }

            if(maleBtn.isChecked()){
                parameters.put("gender","Male");
            }else if(femaleBtn.isChecked()){
                parameters.put("gender","Female");
            }

            JSONArray passion = new JSONArray();
            if(passionIds.size()>0){
                for (int i=0; i<passionIds.size(); i++){
                    JSONObject object = new JSONObject();
                    object.put("passion_id", passionIds.get(i));
                    passion.put(object);
                }
                parameters.put("user_passion", passion);
            }
            /*if(hideAge.isChecked())
                parameters.put("hide_age","1");
            else
                parameters.put("hide_age","0");

            if(hideDistance.isChecked())
                parameters.put("hide_location","1");
            else
                parameters.put("hide_location","0");*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(EditProfileA.this, false, false);
        ApiRequest.callApi(EditProfileA.this, ApiLinks.editProfile, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                parseEditData(resp);
            }
        });

    }


    public void parseEditData(String loginData){
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");

            if(code.equals("200")){
                JSONObject userdata = jsonObject.optJSONObject("msg").optJSONObject("User");
                SharedPreferences.Editor editor = Functions
                        .getSharedPreference(EditProfileA.this).edit();
                editor.putString(Variables.uid, userdata.optString("id"));
                editor.putString(Variables.fName, userdata.optString("first_name"));
                editor.putString(Variables.lName, userdata.optString("last_name"));
                editor.putString(Variables.gender, userdata.optString("gender"));
                editor.putString(Variables.school, ""+jsonObject.optJSONObject("msg")
                        .optJSONObject("School").optString("name"));
                editor.apply();

                finish();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // first step helper function
    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(EditProfileA.this,R.style.DialogStyle);
        alert.setMessage(getApplicationContext().getString(R.string.are_you_sure_to_delete_this_picture))
                .setPositiveButton("Delete", (dialog, which)
                        -> callApiForDeleteLink()).setNegativeButton(getApplicationContext().getString(R.string.cancel), null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                Functions.hideSoftKeyboard(EditProfileA.this);
                finish();
                break;
            case R.id.verifyLayout:
                Intent verifyIntent = new Intent(EditProfileA.this, VerifyProfileA.class);
                startActivity(verifyIntent);
                break;
            case R.id.done_txt:
                callApiEditProfile();
                break;

            case R.id.passionsTv:
                Intent intent = new Intent(EditProfileA.this, AddPassionsA.class);
                Bundle bundle = new Bundle();
                if(sb.length() > 0){
                    bundle.putString(Variables.uPassions, passionsTv.getText().toString());
                }
                intent.putExtra(Variables.uPassions, bundle);
                passionResultCallback.launch(intent);
                break;

            case R.id.school_edit:
                Intent intent1 = new Intent(EditProfileA.this, AddSchoolA.class);
                Bundle bundle1 = new Bundle();
                if(schoolTv.getText().toString().equals(getString(R.string.add_university))){
                    bundle1.putString(Variables.school, "");
                }else {
                    bundle1.putString(Variables.school, schoolTv.getText().toString());
                }
                intent1.putExtra("school", bundle1);
                schoolResultCallback.launch(intent1);
                break;
        }
    }

    //All Result Callback of Current Activity
    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    Uri selectedImage = data.getData();
                    beginCrop(selectedImage);
                }
            });

    ActivityResultLauncher<Intent> cropResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    CropImage.ActivityResult result1 = CropImage.getActivityResult(data);
                    handleCrop(result1.getUri());
                }
            });

    ActivityResultLauncher<Intent> passionResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String passions = data.getStringExtra(Variables.uPassions);
                        if(passions.equals("")){
                            passionsTv.setText(getString(R.string.add_passions));
                            sb = new StringBuilder();
                        }else {
                            passionIds.clear();
                            passionIds = data.getStringArrayListExtra("passionIds");
                            passionsTv.setText(data.getStringExtra(Variables.uPassions));
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> schoolResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Bundle bundle = data.getBundleExtra("bundle_school");
                        schoolTv.setText(bundle.getString("school_name"));
                        selectedSchoolId = bundle.getString("school_id");
                    }
                }
            });


    @Override
    public void finish() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.finish();
    }


}
