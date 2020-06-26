package com.ArCoreDemo.mrganic.recycler;

import androidx.recyclerview.widget.RecyclerView;


public class Item {
    private static final String TAG = "Item";

    private String id;
    private String modelUrl;
    private String thumbnail;

    private ItemHolder holder;

    //Constructor for Item
    public Item(String id) {
        this.id = id;
    }

    //Getters and setters
    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public RecyclerView.ViewHolder getHolder() {
        return holder;
    }

    public void setHolder(ItemHolder holder) {
        this.holder = holder;
    }
}
