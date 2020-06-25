
package com.ArCoreDemo.mrganic.retrofit.POJO;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PolyResponse {

    @SerializedName("assets")
    private List<Asset> mAssets;
    @SerializedName("nextPageToken")
    private String mNextPageToken;
    @SerializedName("totalSize")
    private Long mTotalSize;

    public List<Asset> getAssets() {
        return mAssets;
    }

    public void setAssets(List<Asset> assets) {
        mAssets = assets;
    }

    public String getNextPageToken() {
        return mNextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        mNextPageToken = nextPageToken;
    }

    public Long getTotalSize() {
        return mTotalSize;
    }

    public void setTotalSize(Long totalSize) {
        mTotalSize = totalSize;
    }

    public boolean isEmpty() { return mAssets == null; }

}
