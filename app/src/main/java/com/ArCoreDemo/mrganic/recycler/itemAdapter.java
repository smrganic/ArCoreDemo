package com.ArCoreDemo.mrganic.recycler;

import android.app.ActionBar;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ArCoreDemo.mrganic.R;

import java.util.List;

public class itemAdapter extends RecyclerView.Adapter {

    private static final String TAG = "itemAdapter";
    private final List<item> items;
    private int selected;

    public itemAdapter(List<item> items) {
        this.items = items;
        selected = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ImageView iv = new ImageView(parent.getContext());

        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(4,4,4,4);

        iv.setPadding(8,8,8,8);
        iv.setImageResource(R.drawable.ivplaceholder);
        iv.setCropToPadding(true);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setLayoutParams(layoutParams);

        return new itemHolder(iv, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemId() != position) {
            ((itemHolder) holder).setItem(items.get(position));
            items.get(position).setViewHolder(holder);
            items.get(position)
                    .getThumbnailHolder()
                    .thenAccept(bitmap -> {
                        ImageView iv = (ImageView) holder.itemView;
                        iv.setImageBitmap(bitmap);
                        iv.requestLayout();
                    });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public item getSelected() {
        if(selected >= 0){
            return items.get(selected);
        }
        else return null;
    }

    public void setSelected(item item) {
        if(item == null) {
            selected = -1;
        }
        else selected = items.indexOf(item);
    }
}
