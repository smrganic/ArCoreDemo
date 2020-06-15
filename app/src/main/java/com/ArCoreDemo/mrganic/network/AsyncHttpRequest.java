// Copyright 2018 Google LLC
// Copyright 2020 Stjepan Mrganić
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// This file has been modified by Stjepan Mrganić
// Completion listener interface has been refactored into a separate file
// Functions have been reordered to make the class easier to read and understand.

package com.ArCoreDemo.mrganic.network;

import android.os.Handler;
import android.util.Log;

import com.ArCoreDemo.mrganic.interfaces.CompletionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncHttpRequest {
    private static final String TAG = "HttpRequest";

    // The URL of the request.
    private URL url;

    // The listener to call when the request is complete.
    private CompletionListener listener;

    // The handler on which to post a call to the listener.
    private Handler handler;

    // If true, the request was started.
    private boolean requestStarted;

    /*
     * Creates a new AsyncHttpRequest for the given URL.
     *
     * @param url      The URL of the request.
     * @param handler  The handler on which the listener should be called.
     * @param listener The listener to call when the request completes.
     */
    public AsyncHttpRequest(String url, Handler handler, CompletionListener listener) {
        this.handler = handler;
        this.listener = listener;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Invalid URL: " + url);
            listener.onHttpRequestFailure(0, "Invalid URL: " + url, ex);
        }
    }

    /*
     * Sends the request.
     * After the request completes, the listener specified in the constructor will be called
     * to report the result of the request. This method does not block, it returns immediately.
     */
    public void send() {
        if (requestStarted) {
            throw new IllegalStateException("AsyncHttpRequest can only be sent once.");
        }
        requestStarted = true;
        new Thread(this::backgroundMain).start();
    }

    // Main method for background thread.
    private void backgroundMain() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                postFailure(responseCode,
                        "Request to " + url + " failed with HTTP status code " + responseCode, null);
                return;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            copyStream(connection.getInputStream(), outputStream);
            postSuccess(outputStream.toByteArray());
        } catch (Exception ex) {
            postFailure(-12, "Exception while processing request to " + url, ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Copies the entire contents of the given input stream to the given output stream.
    private static void copyStream(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        byte[] buffer = new byte[16384];
        int bytesReadThisTime;
        while ((bytesReadThisTime = inputStream.read(buffer, 0, buffer.length)) > 0) {
            outputStream.write(buffer, 0, bytesReadThisTime);
        }
    }

    // Posts a failure callback to the listener.
    private void postFailure(final int statusCode, final String message, final Exception exception) {
        handler.post(() -> listener.onHttpRequestFailure(statusCode, message, exception));
    }

    // Posts a success callback to the listener.
    private void postSuccess(final byte[] responseBody) {
        handler.post(() -> listener.onHttpRequestSuccess(responseBody));
    }
}
