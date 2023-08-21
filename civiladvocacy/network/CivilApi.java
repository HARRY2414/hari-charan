package com.example.civiladvocacy.network;

import com.example.civiladvocacy.models.OfficialResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CivilApi {

    @GET("representatives")
    Call<OfficialResponse> getOfficialsData(
            @Query("address") String address,
            @Query("key") String apiKey
    );
}
