
package com.ArCoreDemo.mrganic.retrofit.POJO;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Format {

    @SerializedName("formatComplexity")
    private FormatComplexity mFormatComplexity;
    @SerializedName("formatType")
    private String mFormatType;
    @SerializedName("resources")
    private List<Resource> mResources;
    @SerializedName("root")
    private Root mRoot;

    public FormatComplexity getFormatComplexity() {
        return mFormatComplexity;
    }

    public void setFormatComplexity(FormatComplexity formatComplexity) {
        mFormatComplexity = formatComplexity;
    }

    public String getFormatType() {
        return mFormatType;
    }

    public void setFormatType(String formatType) {
        mFormatType = formatType;
    }

    public List<Resource> getResources() {
        return mResources;
    }

    public void setResources(List<Resource> resources) {
        mResources = resources;
    }

    public Root getRoot() {
        return mRoot;
    }

    public void setRoot(Root root) {
        mRoot = root;
    }

}
