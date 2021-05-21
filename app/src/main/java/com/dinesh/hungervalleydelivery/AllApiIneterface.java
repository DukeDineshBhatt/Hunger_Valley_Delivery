package com.dinesh.hungervalleydelivery;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AllApiIneterface {


    @Multipart
    @POST("dev/bulk")
    Call<OtpBean> getOtp(
            @Part("sender_id") String sender_id,
            @Part("language") String language,
            @Part("route") String route,
            @Part("numbers") String numbers,
            @Part("message") String message,
            @Part("variables") String variables,
            @Part("variables_values") String variables_values,
            @Header("authorization") String authorization

    );

    @Headers({"Authorization:key=AAAADwuN9VI:APA91bEQvTrHPurCbaVFb2_83xXfzAM11fcZciop5oVEuo4T5JDtc1fznIbSHUHZ63-IL0GbY5nFKdXRZgz--GiSuHbYJS4awvYTR6a-HTDZrl84TOs1qPmRwj2zxJskgWcSEWyx3m07",
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResultBean> sendNotification(@Body DataBean requestNotificaton
    );
}