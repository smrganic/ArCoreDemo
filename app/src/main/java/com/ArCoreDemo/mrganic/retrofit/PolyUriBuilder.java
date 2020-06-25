package com.ArCoreDemo.mrganic.retrofit;

import android.net.Uri;
import android.util.Log;

public final class PolyUriBuilder {

    private final static String APIKey = "AIzaSyAKyQb2p4l9c4sJU_RahUXSpym2-E3Xjbs";
    private final static String TAG = "UriBuilder";
    private Uri.Builder uriBuilder;
    public PolyUriBuilder() {
        uriBuilder = new Uri.Builder()
                .scheme("https")
                .authority("poly.googleapis.com")
                .appendPath("v1")
                .appendPath("assets")
                .appendQueryParameter("key", APIKey)
                .appendQueryParameter("curated", Boolean.toString(true))
                .appendQueryParameter("format", "GLTF2");
    }

    public PolyUriBuilder appendKeyword(String keyword){
        if(keyword != null && !keyword.isEmpty()) {
            uriBuilder.appendQueryParameter("keywords", keyword);
        }
        return this;
    }
    public PolyUriBuilder appendPageSize(int pageSize){
        uriBuilder.appendQueryParameter("pageSize", String.valueOf(pageSize));
        return this;
    }
    public Uri build(){
        Log.d(TAG, "Used URL: " + this.toString());
        return uriBuilder.build();
    }
    @Override
    public String toString() {
        return uriBuilder.build().toString();
    }
}
