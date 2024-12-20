package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.app.Activity;
import android.content.Context;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qboxus.binder.Adapters.ProfilePhotosAdapter;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.ItemMoveCallback;
import com.qboxus.binder.ViewHolders.PhotosviewHolder;
import com.qboxus.binder.ApiClasses.ApiRequest;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.PermissionUtils;
import com.qboxus.binder.SimpleClasses.Variables;
import com.qboxus.binder.interfaces.Callback;
import com.qboxus.binder.Models.SexualOrientationModel;
import com.qboxus.binder.Models.UserMultiplePhotoModel;
import com.qboxus.binder.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddPhotosF extends Fragment implements ItemMoveCallback.ItemTouchHelperContract{

    Context context;

    RecyclerView profilePhotoList;
    List<UserMultiplePhotoModel> imagesList;
    ProfilePhotosAdapter profilePhotosAdapter;

    RelativeLayout continueButton;
    TextView continueTv;

    String imageBas64;
    List<String> selectedImagesList = new ArrayList<>();

    int currentPosition=0;
    View view;

    Date c;
    SimpleDateFormat df;
    int currentYear;


    PermissionUtils takePermissionUtils;

    public AddPhotosF() {
        // Required empty public constructor
    }

    public static AddPhotosF newInstance() {
        AddPhotosF fragment = new AddPhotosF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_photos, container, false);
        context = getActivity();

        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("yyyy", Locale.getDefault());
        currentYear = Integer.parseInt(df.format(c));


        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupA.progressBar.setProgress((int) Functions.calculateSegmentProgress(
                        SignupA.pager.getCurrentItem(),
                        SignupA.pager.getOffscreenPageLimit()));
                SignupA.pager.setCurrentItem(SignupA.pager.getCurrentItem()-1);
            }
        });

        profilePhotoList =view.findViewById(R.id.Profile_photos_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        profilePhotoList.setLayoutManager(layoutManager);
        profilePhotoList.setHasFixedSize(false);

        imagesList = new ArrayList<>();
        for (int i=0; i<6; i++){
            imagesList.add(new UserMultiplePhotoModel());
        }

        profilePhotosAdapter = new ProfilePhotosAdapter(getContext(), imagesList, true, new ProfilePhotosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserMultiplePhotoModel item, int postion, View view) {
                currentPosition = postion;
                switch (view.getId()){
                    case R.id.add_btn:
                    {
                        takePermissionUtils=new PermissionUtils(getActivity(),mPermissionResult);
                        if (takePermissionUtils.isStorageCameraPermissionGranted()) {
                            selectImage();
                        }
                        else
                        {
                            takePermissionUtils.showStorageCameraPermissionDailog(getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                        }
                    }
                        break;

                    case R.id.cross_btn:
                        for (int j = 0; j<selectedImagesList.size(); j++){
                            if(item.getImage().equals(selectedImagesList.get(j))){
                                selectedImagesList.remove(j);
                                break;
                            }
                        }
                        imagesList.remove(postion);
                        imagesList.add(new UserMultiplePhotoModel());
                        profilePhotosAdapter.notifyDataSetChanged();
                        if(selectedImagesList.size()>1){
                            continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pink_background));
                            continueTv.setTextColor(ContextCompat.getColor(context, R.color.white));
                        }else{
                            continueButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_google_background));
                            continueTv.setTextColor(ContextCompat.getColor(context, R.color.gray));
                        }
                        break;
                }
            }
        });


        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(profilePhotoList);

        profilePhotoList.setAdapter(profilePhotosAdapter);


        continueButton = view.findViewById(R.id.continueButton);
        continueTv = view.findViewById(R.id.continue_tv);
        continueButton.setOnClickListener(v -> {
            if(selectedImagesList.size() > 1){
                callApiRegisterUser();
            }
        });

        return view;
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
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(),key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(getActivity(),getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
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

            profilePhotosAdapter.notifyItemMoved(fromPosition, toPosition);
        }


    }



    @Override
    public void onRowSelected(PhotosviewHolder myViewHolder) {

    }


    @Override
    public void onRowClear(PhotosviewHolder myViewHolder) {

    }


    private void callApiRegisterUser() {
        JSONObject parameters = new JSONObject();
        try {

            if(SignupA.userModel.isSocialLogin){
                parameters.put("social", ""+ SignupA.userModel.socail_type);
                parameters.put("social_id", ""+ SignupA.userModel.socail_id);
                parameters.put("auth_token", ""+ SignupA.userModel.auth_tokon);
                parameters.put("email", "" + SignupA.userModel.email);
            }
            else if(!SignupA.userModel.isFromPh){
                parameters.put("email", "" + SignupA.userModel.email);
                parameters.put("password", "" + SignupA.userModel.password);
            }

            parameters.put("phone", "" + SignupA.userModel.phone_no);
            parameters.put("dob", "" + SignupA.userModel.date_of_birth);
            parameters.put("first_name", "" + SignupA.userModel.fname);
            parameters.put("last_name", "");
            parameters.put("username", "" + SignupA.userModel.fname);
            parameters.put("gender", "" + SignupA.userModel.gender);
            parameters.put("show_gender", "" + SignupA.userModel.show_gender);
            parameters.put("show_me_gender", "" + SignupA.userModel.show_me_gender.toLowerCase());

            JSONArray sexualOrientation = new JSONArray();
            if(SignupA.userModel.orientationList.size()>0){
                for (int i = 0; i< SignupA.userModel.orientationList.size(); i++){
                    SexualOrientationModel model = SignupA.userModel.orientationList.get(i);
                    JSONObject object = new JSONObject();
                    object.put("sexual_orientation_id", model.getId());
                    sexualOrientation.put(object);
                }
            }

            parameters.put("user_sexual_orientation", sexualOrientation);
            parameters.put("show_orientation", "" + SignupA.userModel.show_orientation);
            parameters.put("school_id", "" + SignupA.userModel.mySchoolId);

            JSONArray passion = new JSONArray();
            if(SignupA.userModel.userPassion.size()>0){
                for (int i = 0; i< SignupA.userModel.userPassion.size(); i++){
                    JSONObject object = new JSONObject();
                    object.put("passion_id", SignupA.userModel.userPassion.get(i));
                    passion.put(object);
                }
            }
            parameters.put("user_passion", passion);

            JSONArray userPhotos = new JSONArray();
            if(imagesList.size()>0){
                for (int i = 0; i<imagesList.size(); i++){
                    UserMultiplePhotoModel model = imagesList.get(i);
                    if(model.getImage() != null && !model.getImage().isEmpty()){
                        JSONObject object = new JSONObject();
                        object.put("order_sequence", i);
                        object.put("image", model.getImage());
                        userPhotos.put(object);
                    }
                }
            }
            parameters.put("user_images", userPhotos);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getActivity(), false, false);
        ApiRequest.callApi(context, ApiLinks.registerUser, parameters, new Callback() {
            @Override
            public void response(String resp) {
                Functions.cancelLoader();
                parseSignUpData(resp);
            }
        });
    }


    // open the gallery when user press button to upload a picture
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultCallback.launch(intent);
    }

    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Uri selectedImage = data.getData();
                        beginCrop(selectedImage);
                    }
                }
            });


    // bottom there function are related to crop the image
    private void beginCrop(Uri source) {
        Intent intent=CropImage.activity(source).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1,1).getIntent(requireActivity());
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
            imageStream = requireActivity().getContentResolver().openInputStream(userImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);

        String path = userImageUri.getPath();
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
            } catch (Exception e) {
                Log.d(Constants.tag,"Exception: "+e);
            }
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        imageBas64 = Functions.bitmapToBase64(getActivity(), rotatedBitmap);
        selectedImagesList.add(imageBas64);
        for(int i = 0; i<imagesList.size(); i++){
            UserMultiplePhotoModel model = imagesList.get(i);
            if(imagesList.get(currentPosition).getImage() != null &&
                    !imagesList.get(currentPosition).getImage().equals("")){
                imagesList.remove(i);
                model.setImage(imageBas64);
                model.setOrderSequence(i);
                imagesList.add(i,model);
                break;
            }else if(model.getImage() == null || model.getImage().equals("")){
                imagesList.remove(i);
                model.setImage(imageBas64);
                model.setOrderSequence(i);
                imagesList.add(i,model);
                break;
            }
        }

        if(selectedImagesList.size()>1){
            continueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pink_background));
            continueTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }else{
            continueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_google_background));
            continueTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray));
        }

        profilePhotosAdapter.notifyDataSetChanged();
    }

    public void parseSignUpData(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");

            if (code.equals("200")) {
                JSONObject userdata = jsonObject.optJSONObject("msg").optJSONObject("User");
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Variables.prefName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Variables.uid, userdata.optString("id"));
                editor.putString(Variables.fName, userdata.optString("first_name"));
                editor.putString(Variables.lName, userdata.optString("last_name"));
                editor.putString(Variables.gender, userdata.optString("gender"));

                JSONArray userImagesArray = jsonObject.optJSONObject("msg").optJSONArray("UserImage");

                imagesList.clear();
                for(int i = 0; i<6; i++){
                    UserMultiplePhotoModel model = new UserMultiplePhotoModel();
                    if(i<userImagesArray.length()){
                        model.setImage(userImagesArray.optJSONObject(i).optString("image"));
                        model.setId(userImagesArray.optJSONObject(i).getString("id"));
                        model.setOrderSequence(Integer.parseInt(userImagesArray.optJSONObject(i).getString("order_sequence")));
                        imagesList.add(i, model);
                    }else {
                        model.setOrderSequence(i);
                        imagesList.add(i, model);
                    }
                }

                Collections.sort(imagesList, new Comparator<UserMultiplePhotoModel>() {
                    @Override public int compare(UserMultiplePhotoModel p1, UserMultiplePhotoModel p2) {
                        return p1.getOrderSequence() - p2.getOrderSequence(); // Ascending
                    }
                });

                if(imagesList.size()>0){
                    editor.putString(Variables.uPic, imagesList.get(0).getImage());
                }

                if(!userdata.optString("dob").equals("0000-00-00")){
                    try {
                        Date date = df.parse(userdata.optString("dob"));
                        int age = Integer.parseInt(df.format(date));
                        editor.putString(Variables.birthDay, " " + (currentYear - age));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                editor.putBoolean(Variables.showMeOnTinder, userdata.optString("hide_me").equals("0"));

                editor.putBoolean(Variables.hideAge, !userdata.optString("hide_age").equals("0"));

                editor.putString(Variables.showMe,userdata.optString("show_me_gender","all"));

                editor.putBoolean(Variables.hide_distance, !userdata.optString("hide_location").equals("0"));

                editor.putInt(Variables.minAge, 18);
                editor.putInt(Variables.maxAge, 75);
                editor.putString(Variables.school, ""+jsonObject.optJSONObject("msg").optJSONObject("School").optString("name"));

                editor.putBoolean(Variables.userLikeLimit, false);

                editor.putString(Variables.uTotalBoost,userdata.optString("total_boost"));
                editor.putString(Variables.uBoost,userdata.optString("boost"));
                editor.putString(Variables.uWallet,userdata.optString("wallet"));
                editor.putString(Variables.authToken,userdata.optString("auth_token"));
                editor.putBoolean(Variables.islogin, true);
                editor.apply();

                openEnableLocation();
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }

    private void openEnableLocation() {
        startActivity(new Intent(getActivity(), EnableLocationA.class));
        requireActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        getActivity().finish();
    }


}