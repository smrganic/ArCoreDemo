package com.ArCoreDemo.mrganic.network;

import android.os.Handler;
import android.util.Log;

import com.ArCoreDemo.mrganic.recycler.item;
import com.ArCoreDemo.mrganic.retrofit.Format;
import com.ArCoreDemo.mrganic.retrofit.PolyObject;

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

    public static String parseAsset(PolyObject responseBody) {
        String url = null;
        List<Format> formats = responseBody.getFormats();
        Format helper;
        for(int i = 0; i < formats.size(); i++) {
            helper = formats.get(i);
            if(helper.getFormatType().equals("GLTF2")){
                url = helper.getRoot().getUrl();
            }
        }
        return url;
    }
}
