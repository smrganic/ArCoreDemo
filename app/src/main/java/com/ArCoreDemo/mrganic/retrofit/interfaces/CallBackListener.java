package com.ArCoreDemo.mrganic.retrofit.interfaces;

import com.ArCoreDemo.mrganic.retrofit.POJO.PolyResponse;

public interface CallBackListener {
    void successfulResponse(PolyResponse response);
    void failedResponse(Exception ex);
}
