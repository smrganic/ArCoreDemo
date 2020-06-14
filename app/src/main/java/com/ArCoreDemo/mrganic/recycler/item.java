package com.ArCoreDemo.mrganic.recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.ArCoreDemo.mrganic.interfaces.CompletionListener;
import com.ArCoreDemo.mrganic.network.AsyncHttpRequest;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;

public class item {
    private static final String TAG = "RecyclerItem";

    private String id;
    private String description;
    private String modelUrl;
    private String thumbnail;

    private CompletableFuture<Bitmap> thumbnailHolder;
    private RecyclerView.ViewHolder viewHolder;
    private CompletableFuture<ModelRenderable> modelRenderableHolder;

    //Constructor for item
    public item(String id) {
        this.id = id;
    }

    //Getters and setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public CompletableFuture<Bitmap> getThumbnailHolder() {
        return thumbnailHolder;
    }

    //Async loading of thumbnail image
    public void loadThumbnail(Handler handler) {
        if(thumbnailHolder == null) {
            thumbnailHolder = new CompletableFuture<>();

            CompletionListener listener = new CompletionListener() {
                @Override
                public void onHttpRequestFailure(int status, String message, Exception ex) {
                    Log.e(TAG, "Thumbnail loading failed: " + status + " " + message, ex);
                    thumbnailHolder.completeExceptionally(ex);
                }

                @Override
                public void onHttpRequestSuccess(byte[] responseBody) {
                    thumbnailHolder.complete(BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length));
                }
            };

            AsyncHttpRequest request = new AsyncHttpRequest(getThumbnail(), handler, listener);
            request.send();
        }
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
