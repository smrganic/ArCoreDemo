package com.ArCoreDemo.mrganic.retrofit;

import android.net.Uri;
import android.util.Log;

import com.ArCoreDemo.mrganic.interfaces.CallBackListener;
import com.ArCoreDemo.mrganic.interfaces.IAPICallPoly;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Final to disable extension
public final class PolyAPI {

    private static final String BASE_API = "https://poly.googleapis.com/";
    private static final String TAG = "PolyAPI";
    private static final String APIKey = "AIzaSyAKyQb2p4l9c4sJU_RahUXSpym2-E3Xjbs";
    private static final int pageSize = 25;

    private static CallBackListener callBackListener;

    private static IAPICallPoly apiInterface;

    //To disable instantiation
    private PolyAPI() { }

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

    public static void callAPIWithKeyword(String keyword) {

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("https")
                .authority("poly.googleapis.com")
                .appendPath("v1")
                .appendPath("assets")
                .appendQueryParameter("key", APIKey)
                .appendQueryParameter("curated", Boolean.toString(true))
                .appendQueryParameter("format", "GLTF2")
                .appendQueryParameter("pageSize", String.valueOf(pageSize));

        if (keyword != null && !keyword.isEmpty()) {
            uriBuilder.appendQueryParameter("keywords", keyword);
        }

        Uri uri = uriBuilder.build();

        Log.d(TAG, "Url used for api call: " + uri.toString());

        getPolyAPIResponse(uri);
    }

    public static void callAPIWithKeyword(String keyword, int pageSize) {

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("https")
                .authority("poly.googleapis.com")
                .appendPath("v1")
                .appendPath("assets")
                .appendQueryParameter("key", APIKey)
                .appendQueryParameter("curated", Boolean.toString(true))
                .appendQueryParameter("format", "GLTF2")
                .appendQueryParameter("pageSize", String.valueOf(pageSize));

        if (keyword != null && !keyword.isEmpty()) {
            uriBuilder.appendQueryParameter("keywords", keyword);
        }

        Uri uri = uriBuilder.build();

        Log.d(TAG, "Url used for api call: " + uri.toString());

        getPolyAPIResponse(uri);
    }

    private static void getPolyAPIResponse(Uri url) {
        Call<PolyResponse> PolyAPICall = getApiInterface().getListAssets(url);
        Callback<PolyResponse> callback = new Callback<PolyResponse>() {
            @Override
            public void onResponse(Call<PolyResponse> call, Response<PolyResponse> response) {
                if (response.isSuccessful()) { callBackListener.successfulResponse(response.body()); } }

            @Override
            public void onFailure(Call<PolyResponse> call, Throwable t) {
                callBackListener.failedResponse((Exception) t);
            }
        };
        PolyAPICall.enqueue(callback);
    }
}
