package com.megvii.livenesslib.util;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by wrf on 2016/10/27.
 */

public interface FaceIDRequest {
    @Multipart
    @POST("v2/verify")
    Call<ResponseBody> verify(
            @Part("api_key") RequestBody api_key
            , @Part("api_secret") RequestBody api_secret
            , @Part("comparison_type") RequestBody comparison_type
            , @Part("face_image_type") RequestBody face_image_type
            , @Part("uuid") RequestBody uuid
            , @Part MultipartBody.Part image_ref1
            , @Part MultipartBody.Part image
    );

    @Multipart
    @POST("v1/detect")
    Call<ResponseBody> detect(
            @Part("api_key") RequestBody api_key
            , @Part("api_secret") RequestBody api_secret
            , @Part MultipartBody.Part image);


    @Multipart
    @POST("v3/detect")
    Call<ResponseBody> detectMG(
            @Part("api_key") RequestBody api_key
            , @Part("api_secret") RequestBody api_secret
            , @Part MultipartBody.Part image);
}
