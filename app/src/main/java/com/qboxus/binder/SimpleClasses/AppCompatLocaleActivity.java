package com.qboxus.binder.SimpleClasses;

import android.content.Context;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import com.qboxus.binder.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AppCompatLocaleActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {

        String[] languageArray=newBase.getResources().getStringArray(R.array.language_code);

        List<String> languageCode = Arrays.asList(languageArray);

        String language = Functions.getSharedPreference(newBase).getString(Variables.selectedLanguage,Variables.defultLanguage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && languageCode.contains(language)) {
            Locale newLocale = new Locale(language);
            super.attachBaseContext(ContextWrapper.wrap(newBase, newLocale));
        } else {
            super.attachBaseContext(newBase);
        }

    }
}
