package com.ArCoreDemo.mrganic.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IAPICallPoly {
    @GET
    Call<PolyObject> getListAssets(@Url String url);
}
