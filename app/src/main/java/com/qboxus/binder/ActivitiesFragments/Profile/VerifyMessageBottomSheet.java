package com.qboxus.binder.ActivitiesFragments.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.binder.R;
import com.qboxus.binder.interfaces.FragmentCallback;

public class VerifyMessageBottomSheet extends BottomSheetDialogFragment {
    FragmentCallback fragmentCallback;
    View view;

    public VerifyMessageBottomSheet() {
    }

    public VerifyMessageBottomSheet(FragmentCallback fragmentCallback) {
        this.fragmentCallback=fragmentCallback;
    }

    public static VerifyMessageBottomSheet newInstance(FragmentCallback fragmentCallback) {
        VerifyMessageBottomSheet fragment = new VerifyMessageBottomSheet(fragmentCallback);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_verify_message_bottom_sheet, container, false);

        view.findViewById(R.id.takePhotoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                fragmentCallback.responce(bundle);
                dismiss();
            }
        });

        view.findViewById(R.id.gotitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return view;
    }
}