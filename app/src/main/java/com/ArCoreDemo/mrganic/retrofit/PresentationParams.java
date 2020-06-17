
package com.ArCoreDemo.mrganic.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PresentationParams implements Serializable {

    @SerializedName("backgroundColor")
    private String mBackgroundColor;
    @SerializedName("colorSpace")
    private String mColorSpace;
    @SerializedName("orientingRotation")
    private OrientingRotation mOrientingRotation;

    public String getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public String getColorSpace() {
        return mColorSpace;
    }

    public void setColorSpace(String colorSpace) {
        mColorSpace = colorSpace;
    }

    public OrientingRotation getOrientingRotation() {
        return mOrientingRotation;
    }

    public void setOrientingRotation(OrientingRotation orientingRotation) {
        mOrientingRotation = orientingRotation;
    }

}
