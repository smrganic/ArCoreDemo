package com.ArCoreDemo.mrganic.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PolyAPI {
    private static final String BASE_API = "https://poly.googleapis.com/";
    private static IAPICallPoly apiInterface;
    private static String apiKey;
    public static IAPICallPoly getApiInterface(String apiKeyValue) {
        if (apiInterface == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = retrofit.create(IAPICallPoly.class);
            apiKey = apiKeyValue;
        }
        return apiInterface;
    }

    public static String getApiKey() {
        return apiKey;
    }
}
