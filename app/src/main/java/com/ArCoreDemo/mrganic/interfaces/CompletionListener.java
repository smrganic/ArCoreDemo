package com.ArCoreDemo.mrganic.interfaces;

public interface CompletionListener {

    // Called to indicate the asynchronous HTTP request completion resulted in failure
    void onHttpRequestFailure(int status, String message, Exception ex);

    // Called to indicate the asynchronous HTTP request completion resulted in success
    void onHttpRequestSuccess(byte[] responseBody);
}