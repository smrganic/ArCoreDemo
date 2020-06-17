
package com.ArCoreDemo.mrganic.retrofit;

import com.google.gson.annotations.SerializedName;

public class FormatComplexity {

    @SerializedName("triangleCount")
    private String mTriangleCount;

    public String getTriangleCount() {
        return mTriangleCount;
    }

    public void setTriangleCount(String triangleCount) {
        mTriangleCount = triangleCount;
    }

}
