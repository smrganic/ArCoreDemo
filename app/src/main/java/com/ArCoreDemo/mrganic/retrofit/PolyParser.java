package com.ArCoreDemo.mrganic.retrofit;

import com.ArCoreDemo.mrganic.recycler.Item;
import com.ArCoreDemo.mrganic.retrofit.POJO.Asset;
import com.ArCoreDemo.mrganic.retrofit.POJO.Format;
import com.ArCoreDemo.mrganic.retrofit.POJO.PolyResponse;

import java.util.ArrayList;
import java.util.List;

public final class PolyParser {

    private PolyParser() {}

    public static List<Item> parseListAssets(PolyResponse responseBody) {

        List<Item> items = new ArrayList<>();
        List<Asset> assets = responseBody.getAssets();
        Asset helper;
        Format formatHelper;

        for(int i = 0; i < assets.size(); i++) {
            helper = assets.get(i);
            Item item = new Item(helper.getName());
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
