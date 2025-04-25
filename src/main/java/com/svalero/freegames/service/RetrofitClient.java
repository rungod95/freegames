package com.svalero.freegames.service;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String EXTERNAL_BASE_URL = "https://www.freetogame.com/api/";
    private static final String LOCAL_BASE_URL = "http://localhost:8080/";

    private static Retrofit externalRetrofit;
    private static Retrofit localRetrofit;


    //
    public static FreeToGameService getExternalService() {
        if (externalRetrofit == null) {
            externalRetrofit = new Retrofit.Builder()
                .baseUrl(EXTERNAL_BASE_URL)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return externalRetrofit.create(FreeToGameService.class);
    }

    public static FreeToGameService getLocalService() {
        if (localRetrofit == null) {
            localRetrofit = new Retrofit.Builder()
                .baseUrl(LOCAL_BASE_URL)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return localRetrofit.create(FreeToGameService.class);
    }
}
