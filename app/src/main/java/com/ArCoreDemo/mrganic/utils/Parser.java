package com.ArCoreDemo.mrganic.utils;

import com.ArCoreDemo.mrganic.recycler.item;
import com.ArCoreDemo.mrganic.retrofit.Asset;
import com.ArCoreDemo.mrganic.retrofit.Format;
import com.ArCoreDemo.mrganic.retrofit.PolyResponse;

import java.util.ArrayList;
import java.util.List;

public abstract class Parser {

    private static final String TAG = "Parser";

    public static List<item> parseListAssets(PolyResponse responseBody) {

        List<item> items = new ArrayList<>();
        List<Asset> assets = responseBody.getAssets();
        Asset helper;
        Format formatHelper;

        for(int i = 0; i < assets.size(); i++) {
            helper = assets.get(i);
            item item = new item(helper.getName());
            String url = helper.getThumbnail().getUrl();
            item.setThumbnail(url);


            List<Format> formats = helper.getFormats();
            for(int j = 0; j < formats.size(); j++) {
                formatHelper = formats.get(j);
                if(formatHelper.getFormatType().equals("GLTF2")){
                    item.setModelUrl(formatHelper.getRoot().getUrl());
                }
            }
            items.add(item);
        }
        return items;
    }
}
