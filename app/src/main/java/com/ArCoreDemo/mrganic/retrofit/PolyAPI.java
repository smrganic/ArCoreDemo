package com.ArCoreDemo.mrganic.retrofit;

import android.net.Uri;

import com.ArCoreDemo.mrganic.retrofit.POJO.PolyResponse;
import com.ArCoreDemo.mrganic.retrofit.interfaces.CallBackListener;
import com.ArCoreDemo.mrganic.retrofit.interfaces.IAPICallPoly;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Final to disable extension
public final class PolyAPI {

    private static final String BASE_API = "https://poly.googleapis.com/";
    private static final String TAG = "PolyAPI";

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

    public static void callAPI() {
        getPolyAPIResponse(new PolyUriBuilder().build());
    }

    public static void callAPI(String keyword) {
        getPolyAPIResponse(
                new PolyUriBuilder()
                        .appendKeyword(keyword)
                        .build());
    }

    public static void callAPI(String keyword, int pageSize) {
        getPolyAPIResponse(
                new PolyUriBuilder()
                        .appendKeyword(keyword)
                        .appendPageSize(pageSize)
                        .build());
    }

    private static void getPolyAPIResponse(Uri url) {
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
