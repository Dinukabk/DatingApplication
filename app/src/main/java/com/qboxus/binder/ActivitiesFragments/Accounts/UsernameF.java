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

public class UsernameF extends Fragment {

    private EditText usernameField;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_username, container, false);
        usernameField = view.findViewById(R.id.username);
        return view;
    }

    public String getUsername() {
        return usernameField.getText().toString().trim();
    }
}
