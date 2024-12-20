package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.qboxus.binder.R;

public class EmailF extends Fragment {

    private EditText emailField;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email, container, false);
        emailField = view.findViewById(R.id.email);
        return view;
    }

    public String getEmail() {
        return emailField.getText().toString().trim();
    }
}
