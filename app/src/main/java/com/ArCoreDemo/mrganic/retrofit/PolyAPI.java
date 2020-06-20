package com.ArCoreDemo.mrganic.retrofit;

import android.net.Uri;

import com.ArCoreDemo.mrganic.interfaces.CallBackListener;
import com.ArCoreDemo.mrganic.interfaces.IAPICallPoly;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class PolyAPI implements CallBackListener {

    private static CallBackListener callBackListener;
    private static String APIKey;

    private static final String BASE_API = "https://poly.googleapis.com/";
    private static IAPICallPoly apiInterface;

    private static IAPICallPoly getApiInterface() {
        if (apiInterface == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = retrofit.create(IAPICallPoly.class);
        }
        return apiInterface;
    }

    public static void setCallBackListener(CallBackListener callBackListener) {
        PolyAPI.callBackListener = callBackListener;
    }

    public static void setAPIKey(String APIKey) {
        PolyAPI.APIKey = APIKey;
    }

    public static void callAPIWithKeyword(String keyword) {

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("https")
                .authority("poly.googleapis.com")
                .appendPath("v1")
                .appendPath("assets")
                .appendQueryParameter("key", APIKey)
                .appendQueryParameter("curated", Boolean.toString(true))
                .appendQueryParameter("format", "GLTF2")
                .appendQueryParameter("pageSize", "40");

        if (keyword != null && !keyword.isEmpty()) {
            uriBuilder.appendQueryParameter("keywords", keyword);
        }

        String url = uriBuilder.build().toString();

        APICallPolyResponse(url);
    }

    private static void APICallPolyResponse(String url) {
        Call<PolyResponse> PolyAPICall = getApiInterface().getListAssets(url);
        Callback<PolyResponse> callback = new Callback<PolyResponse>() {
            @Override
            public void onResponse(Call<PolyResponse> call, Response<PolyResponse> response) {
                if (response.isSuccessful()) {
                    callBackListener.successfulResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<PolyResponse> call, Throwable t) {
                callBackListener.failedResponse((Exception) t);
            }
        };
        PolyAPICall.enqueue(callback);
    }
}
