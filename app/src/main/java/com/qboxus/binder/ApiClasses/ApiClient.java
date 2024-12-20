package com.qboxus.binder.ApiClasses;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qboxus.binder.Constants;
import com.qboxus.binder.SimpleClasses.Functions;
import com.qboxus.binder.SimpleClasses.Variables;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance(Context context) {


        if (retrofit == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .build();

            httpClient.addInterceptor(chain -> {
                 Request.Builder requestBuilder = chain.request().newBuilder();
                 requestBuilder.header("Content-Type", "application/json");
                 requestBuilder.header("Api-Key", Constants.API_KEY);
                 requestBuilder.header("User-Id", Functions.getSharedPreference(context).getString(Variables.uid,"null"));
                 requestBuilder.header("Auth-Token", Functions.getSharedPreference(context).getString(Variables.authToken,"null"));
                 return chain.proceed(requestBuilder.build());
            });

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
