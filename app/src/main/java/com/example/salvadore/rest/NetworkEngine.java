package com.example.salvadore.rest;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkEngine {

    Context context;
    ApiInterface apiInterface;

    private String baseURL = "https://push-api.cloud.huawei.com/";

    public Retrofit retrofit;
    Retrofit retrofitIpLocation;

    private final long TIMEOUT = 500000;

    private static NetworkEngine ourInstance = new NetworkEngine();

    public static NetworkEngine getInstance(){
        return ourInstance;
    }

    private NetworkEngine(){

    }

    public ApiInterface getSalvadoreApi(Context context){
        this.context = context;
        initApi();
        return apiInterface;
    }

    private void initApi(){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(TIMEOUT, TimeUnit.MICROSECONDS)
                .writeTimeout(TIMEOUT,TimeUnit.MICROSECONDS)
                .readTimeout(TIMEOUT,TimeUnit.MICROSECONDS)
                .addInterceptor(loggingInterceptor).build();

        retrofit = new Retrofit.Builder().baseUrl(baseURL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }





}
