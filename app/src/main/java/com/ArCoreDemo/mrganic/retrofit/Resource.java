
package com.ArCoreDemo.mrganic.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Resource implements Serializable {

    @SerializedName("contentType")
    private String mContentType;
    @SerializedName("relativePath")
    private String mRelativePath;
    @SerializedName("url")
    private String mUrl;

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String contentType) {
        mContentType = contentType;
    }

    public String getRelativePath() {
        return mRelativePath;
    }

    public void setRelativePath(String relativePath) {
        mRelativePath = relativePath;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}
