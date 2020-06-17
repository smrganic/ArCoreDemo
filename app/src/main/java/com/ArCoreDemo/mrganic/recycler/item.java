package com.ArCoreDemo.mrganic.recycler;

import android.content.Context;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;


public class item {
    private static final String TAG = "RecyclerItem";

    private String id;
    private String modelUrl;
    private String thumbnail;

    private RecyclerView.ViewHolder viewHolder;
    private CompletableFuture<ModelRenderable> modelRenderableHolder;

    //Constructor for item
    public item(String id) {
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

    public CompletableFuture<ModelRenderable> createModelRenderableHolder() {
        if(modelRenderableHolder == null){
            Context context = viewHolder.itemView.getContext();

            RenderableSource source = RenderableSource.builder()
                    .setSource(context, Uri.parse(modelUrl), RenderableSource.SourceType.GLTF2)
                    .build();
                    /* This could help with model alignment will test later
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build();
                     */

            modelRenderableHolder = ModelRenderable.builder()
                    .setRegistryId(id).setSource(context, source)
                    .build();
        }

        return modelRenderableHolder;
    }
}