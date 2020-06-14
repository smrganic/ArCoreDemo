package com.ArCoreDemo.mrganic.network;

import android.os.Handler;
import android.util.Log;

import com.ArCoreDemo.mrganic.recycler.item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class Parser {

    private static final String TAG = "Parser";

    public static List<item> parseListAssets(byte[] responseBody, Handler backGroundThreadHandler) throws IOException {
        String assetBody = new String(responseBody, Charset.forName("UTF-8"));
        try {
            JSONObject response = new JSONObject(assetBody);
            List<item> items = new ArrayList<>();

            JSONArray assets = response.getJSONArray("assets");

            for(int i = 0; i < assets.length(); i++) {
                JSONObject object = assets.getJSONObject(i);
                item item = new item(object.getString("name"));

                String url = object.getJSONObject("thumbnail").getString("url");
                item.setThumbnail(url);
                item.loadThumbnail(backGroundThreadHandler);

                if(object.has("description")) {
                    item.setDescription(object.getString("description"));
                }


                //Find gltf2 format url
                JSONArray formats = object.getJSONArray("formats");
                for(int j = 0; j < formats.length(); j++) {
                    JSONObject format = formats.getJSONObject(j);
                    if(format.getString("formatType").equals("GLTF2")) {
                        item.setModelUrl(format.getJSONObject("root").getString("url"));
                    }
                }
                items.add(item);
            }
            return items;
        }
        catch (JSONException ex) {
            Log.e(TAG, "JSON parsing error while processing response: " + ex);
            throw new IOException("JSON parsing error", ex);
        }
    }

    public static String parseAsset(byte[] responseBody) {

        String assetBody = new String(responseBody, Charset.forName("UTF-8"));
        String url = null;

        try {

            JSONObject response = new JSONObject(assetBody);

            String displayName = response.getString("displayName");
            String authorName = response.getString("authorName");
            Log.d(TAG, "Display name: " + displayName);
            Log.d(TAG, "Author name: " + authorName);

            JSONArray formats = response.getJSONArray("formats");
            for (int i = 0; i < formats.length(); i++) {
                JSONObject format = formats.getJSONObject(i);

                if (format.getString("formatType").equals("GLTF2")) {
                    url = format.getJSONObject("root").getString("url");
                }
            }
        }

        catch (JSONException jsonException) {
            Log.e(TAG, "JSON parsing error while processing response: " + jsonException);
            jsonException.printStackTrace();
        }
        return url;
    }
}
