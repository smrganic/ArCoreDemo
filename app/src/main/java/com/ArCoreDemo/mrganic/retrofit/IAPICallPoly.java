package com.ArCoreDemo.mrganic.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IAPICallPoly {
    @GET
    Call<List<PolyObject>> getListAssets(@Url String url);
    @GET
    Call<PolyObject> getAsset(@Url String url);
}
