package com.ArCoreDemo.mrganic.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ArCoreDemo.mrganic.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter {

    private static final String TAG = "ItemAdapter";

    private final List<Item> items;
    private int selected;

    public ItemAdapter(List<Item> items) {
        this.items = items;
        selected = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail, parent, false);
        return new ItemHolder(imageView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemId() != position) {
            ((ItemHolder) holder).setItem(items.get(position));
            items.get(position).setHolder((ItemHolder) holder);
            ImageView iv = (ImageView) holder.itemView.findViewById(R.id.iv);
            Picasso.get().load(items.get(position).getThumbnail()).into(iv);
            //Tells Android the state of view may have changed
            //& need to be re-drawn.
            iv.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Item getSelected() {
        if (selected >= 0) {
            return items.get(selected);
        } else return null;
    }

    public void setSelected(Item item) {
        if (item == null) {
            selected = -1;
        } else selected = items.indexOf(item);
    }
}
