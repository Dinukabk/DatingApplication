package com.qboxus.binder.ActivitiesFragments;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.binder.SimpleClasses.Functions;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */

public class SeeFullImageF extends Fragment {

    View view;
    Context context;
    ImageButton saveButton, shareButton, closeGallery;
    SimpleDraweeView singleImage;
    String imageUrl, chatId;
    ProgressBar pBar;
    ProgressDialog progressDialog;
    PermissionUtils takePermissionUtils;
    // this is the third party library that will download the image
    DownloadRequest prDownloader;

    File direct;
    File fullPath;
    int width,height;

    public SeeFullImageF() {
        // Required empty public constructor
    }

    public static SeeFullImageF newInstance() {
        SeeFullImageF fragment = new SeeFullImageF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_see_full_image, container, false);
        context=getContext();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        imageUrl = getArguments().getString("image_url");

        chatId = getArguments().getString("chat_id");

        closeGallery =view.findViewById(R.id.close_gallery);
        closeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        progressDialog=new ProgressDialog(context,R.style.AlertDialogCustom);
        progressDialog.setMessage(getActivity().getString(R.string.please_Wait));
        PRDownloader.initialize(getActivity().getApplicationContext());


        // get the full path of image in database
        fullPath = new File(context.getExternalFilesDir(null).getAbsolutePath()  +"/"+context.getResources().getString(R.string.app_name)+"/"+ chatId +".jpg");

        // if the image file is exits then we will hide the save btn
        saveButton =view.findViewById(R.id.savebtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePermissionUtils=new PermissionUtils(getActivity(), mSaveFalseStoragePermissionResult);
                if (takePermissionUtils.isStoragePermissionGranted())
                {
                    savePicture(false);
                }
                else
                {
                    takePermissionUtils.showStoragePermissionDailog(context.getString(R.string.we_need_storage_permission_to_save_picture));
                }
            }
        });
        if(fullPath.exists()){
            saveButton.setVisibility(View.GONE);
        }


        // get  the directory inwhich we want to save the image
        direct = new File(context.getExternalFilesDir(null).getAbsolutePath()  +"/"+context.getResources().getString(R.string.app_name)+"/");

        // this code will download the image
        prDownloader= PRDownloader.download(imageUrl, direct.getPath(), chatId +".jpg").build();
        pBar = view.findViewById(R.id.p_bar);


        singleImage = view.findViewById(R.id.single_image);
        // if the image is already save then we will show the image from directory otherwise
        // we will show the image by using picasso
        if(fullPath.exists()){
            Uri uri= Uri.parse(fullPath.getAbsolutePath());
            singleImage.setController(Functions.frescoImageLoad(uri,R.drawable.image_placeholder,singleImage,false));
        }else {
            pBar.setVisibility(View.GONE);
            singleImage.setController(Functions.frescoImageLoad(imageUrl,R.drawable.image_placeholder,singleImage,false));
        }


        shareButton =view.findViewById(R.id.sharebtn);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePermissionUtils=new PermissionUtils(getActivity(), mShareStoragePermissionResult);
                if (takePermissionUtils.isStoragePermissionGranted())
                {
                    sharePicture();
                }
                else
                {
                    takePermissionUtils.showStoragePermissionDailog(context.getString(R.string.we_need_storage_permission_to_save_picture));
                }

            }
        });

        return view;
    }

    private ActivityResultLauncher<String[]> mSaveStoragePermissionResult = registerForActivityResult(
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
                        Functions.showPermissionSetting(context,context.getString(R.string.we_need_storage_and_recording_permission_for_voice_message));
                    }
                    else
                    if (allPermissionClear)
                    {
                        savePicture(true);
                    }

                }
            });


    private ActivityResultLauncher<String[]> mShareStoragePermissionResult = registerForActivityResult(
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
                        Functions.showPermissionSetting(context,context.getString(R.string.we_need_storage_and_recording_permission_for_voice_message));
                    }
                    else
                    if (allPermissionClear)
                    {
                        sharePicture();
                    }

                }
            });


    private ActivityResultLauncher<String[]> mSaveFalseStoragePermissionResult = registerForActivityResult(
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
                        Functions.showPermissionSetting(context,context.getString(R.string.we_need_storage_and_recording_permission_for_voice_message));
                    }
                    else
                    if (allPermissionClear)
                    {
                        savePicture(false);
                    }

                }
            });


    // this method will share the picture to other user
    public void sharePicture(){
        Uri bitmapUri;
        if(fullPath.exists()){
            bitmapUri= Uri.parse(fullPath.getAbsolutePath());
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            startActivity(Intent.createChooser(intent, ""));
        } else {
            takePermissionUtils=new PermissionUtils(getActivity(), mSaveStoragePermissionResult);
            if (takePermissionUtils.isStoragePermissionGranted())
            {
                savePicture(true);
            }
            else
            {
                takePermissionUtils.showStoragePermissionDailog(context.getString(R.string.we_need_storage_permission_to_save_picture));
            }
        }
    }


    // this funtion will save the picture but we have to give tht permision to right the storage
    public void savePicture(final boolean isFromShare){
        final File direct = new File(
                context.getExternalFilesDir(null).getAbsolutePath() + "/DCIM/"+context.getResources().getString(R.string.app_name)+"/");
        progressDialog.show();
        prDownloader.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {

                MediaScannerConnection.scanFile(getActivity(), new String[] { direct.getPath() + chatId + ".jpg" }, null, new MediaScannerConnection.OnScanCompletedListener() {
                    /*
                     *   (non-Javadoc)
                     * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                     */
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });


                progressDialog.dismiss();
                if (isFromShare) {
                    sharePicture();
                } else {
                    new AlertDialog.Builder(context,R.style.AlertDialogCustom)
                            //set title
                            .setTitle("Image Saved")
                            //set message
                            .setMessage(fullPath.getAbsolutePath())
                            //set negative button
                            .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onError(Error error) {
                progressDialog.dismiss();
                Toast.makeText(context, getActivity().getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

}


