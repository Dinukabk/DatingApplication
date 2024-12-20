package com.qboxus.binder.ActivitiesFragments.Accounts;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.qboxus.binder.R;
import com.qboxus.binder.SimpleClasses.AppCompatLocaleActivity;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;

public class WebviewA extends AppCompatLocaleActivity implements View.OnClickListener{



    ProgressBar progressBar;
    WebView webView;
    String url = "www.google.com";
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.selectedLanguage,Variables.defultLanguage)
                , this, getClass(),false);
        setContentView(R.layout.activity_webview);
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");

        Functions.printLog(url);


        findViewById(R.id.goBack).setOnClickListener(this);

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress_bar);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress >= 80) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (url.equalsIgnoreCase("closePopup")) {
                    onBackPressed();
                }
                return false;
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                onBackPressed();
                break;
        }
    }

}
