package com.ArCoreDemo.mrganic.interfaces;

import com.ArCoreDemo.mrganic.retrofit.PolyResponse;

public interface CallBackListener {
    void successfulResponse(PolyResponse response);

    void failedResponse(Exception ex);
}
