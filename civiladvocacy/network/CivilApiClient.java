package com.example.civiladvocacy.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CivilApiClient {

    public static Retrofit retrofit = null;
    public static String API_KEY = "AIzaSyBBFOU680zwuTHslwkDY4hcHQGHyAh647M";
    public static String BASE_URL = "https://civicinfo.googleapis.com/civicinfo/v2/";

    public static CivilApi getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(CivilApi.class);
    }
}