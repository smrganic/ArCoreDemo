
package com.ArCoreDemo.mrganic.retrofit.POJO;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Asset {

    @SerializedName("authorName")
    private String mAuthorName;
    @SerializedName("createTime")
    private String mCreateTime;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("displayName")
    private String mDisplayName;
    @SerializedName("formats")
    private List<Format> mFormats;
    @SerializedName("isCurated")
    private Boolean mIsCurated;
    @SerializedName("license")
    private String mLicense;
    @SerializedName("name")
    private String mName;
    @SerializedName("presentationParams")
    private PresentationParams mPresentationParams;
    @SerializedName("thumbnail")
    private Thumbnail mThumbnail;
    @SerializedName("updateTime")
    private String mUpdateTime;
    @SerializedName("visibility")
    private String mVisibility;

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    public String getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(String createTime) {
        mCreateTime = createTime;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public List<Format> getFormats() {
        return mFormats;
    }

    public void setFormats(List<Format> formats) {
        mFormats = formats;
    }

    public Boolean getIsCurated() {
        return mIsCurated;
    }

    public void setIsCurated(Boolean isCurated) {
        mIsCurated = isCurated;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        mLicense = license;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public PresentationParams getPresentationParams() {
        return mPresentationParams;
    }

    public void setPresentationParams(PresentationParams presentationParams) {
        mPresentationParams = presentationParams;
    }

    public Thumbnail getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        mThumbnail = thumbnail;
    }

    public String getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        mUpdateTime = updateTime;
    }

    public String getVisibility() {
        return mVisibility;
    }

    public void setVisibility(String visibility) {
        mVisibility = visibility;
    }

}
