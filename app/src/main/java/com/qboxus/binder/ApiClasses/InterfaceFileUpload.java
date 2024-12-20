package com.qboxus.binder.ApiClasses;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface InterfaceFileUpload {


    @Multipart
    @POST(ApiLinks.verifyProfilePhoto)
    Call<Object> UploadProfileVideo(@Part MultipartBody.Part file,
                                    @Part("user_id") RequestBody UserId);


}
