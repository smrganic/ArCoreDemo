package com.ArCoreDemo.mrganic.interfaces;

import android.net.Uri;

import com.ArCoreDemo.mrganic.retrofit.PolyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IAPICallPoly {
    @GET
    Call<PolyResponse> getListAssets(@Url Uri url);
}
