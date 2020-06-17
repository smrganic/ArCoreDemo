package com.ArCoreDemo.mrganic.retrofit;

import com.ArCoreDemo.mrganic.interfaces.IAPICallPoly;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PolyAPI {
    private static final String BASE_API = "https://poly.googleapis.com/";
    private static IAPICallPoly apiInterface;
    public static IAPICallPoly getApiInterface() {
        if (apiInterface == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = retrofit.create(IAPICallPoly.class);
        }
        return apiInterface;
    }
}
