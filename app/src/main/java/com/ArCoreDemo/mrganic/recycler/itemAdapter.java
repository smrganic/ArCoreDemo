package com.ArCoreDemo.mrganic.recycler;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ArCoreDemo.mrganic.R;
import com.squareup.picasso.Picasso;

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

        //Programmatically create new image view
        ImageView iv = new ImageView(parent.getContext());

        //Sets the parameters of the image view
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(250, 250);
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
            ImageView iv = (ImageView) holder.itemView;
            Picasso.get().load(items.get(position).getThumbnail()).into(iv);
            //Todo check what this does
            iv.requestLayout();
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
