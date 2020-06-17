
package com.ArCoreDemo.mrganic.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FormatComplexity implements Serializable {

    @SerializedName("triangleCount")
    private String mTriangleCount;

    public String getTriangleCount() {
        return mTriangleCount;
    }

    public void setTriangleCount(String triangleCount) {
        mTriangleCount = triangleCount;
    }

}
