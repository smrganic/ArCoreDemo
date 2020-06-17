
package com.ArCoreDemo.mrganic.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class OrientingRotation implements Serializable {

    @SerializedName("w")
    private Long mW;

    public Long getW() {
        return mW;
    }

    public void setW(Long w) {
        mW = w;
    }

}
