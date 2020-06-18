package com.ArCoreDemo.mrganic.recycler;
import androidx.recyclerview.widget.RecyclerView;


public class Item {
    private static final String TAG = "RecyclerItem";

    private String id;
    private String modelUrl;
    private String thumbnail;

    private RecyclerView.ViewHolder viewHolder;

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

    public RecyclerView.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(RecyclerView.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }
}
