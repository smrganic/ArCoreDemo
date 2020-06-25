
package com.ArCoreDemo.mrganic.retrofit.POJO;


import com.google.gson.annotations.SerializedName;

public class Root {

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
