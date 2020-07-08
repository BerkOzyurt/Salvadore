package com.example.salvadore.rest;

import com.example.salvadore.models.NotificationBody;
import com.example.salvadore.models.NotificationMessage;
import com.example.salvadore.rest.base.DBResponse;
import com.example.salvadore.rest.response.EmptyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("v1/APP_ID/messages:send")
    Call<DBResponse<EmptyResponse>> createNotification(
            @Header("Authorization") String authorization,
            @Body NotificationMessage notificationBody

    );
}
