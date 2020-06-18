package com.ArCoreDemo.mrganic.recycler;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemHolder extends RecyclerView.ViewHolder {

    private static final int SELECTED_VALUE = Color.BLACK;
    private static final int DESELECTED_VALUE = Color.WHITE;

    private ItemAdapter adapter;
    private Item item;

    public ItemHolder(@NonNull View itemView, ItemAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
    }

    public void setItem(Item item){
        this.item = item;
        itemView.setOnClickListener(this::onClick);
        if(item.equals(adapter.getSelected())) {
            itemView.setSelected(true);
            itemView.setBackgroundColor(SELECTED_VALUE);
        }
        else {
            itemView.setSelected(false);
            itemView.setBackgroundColor(DESELECTED_VALUE);
        }
    }

    private void onClick(View view) {
        Item selected = adapter.getSelected();
        if (!item.equals(selected)) {
            if (selected != null) {
                selected.getViewHolder().itemView.setBackgroundColor(DESELECTED_VALUE);
            }
            adapter.setSelected(item);
            itemView.setSelected(true);
            itemView.setBackgroundColor(SELECTED_VALUE);
        } else {
            adapter.setSelected(null);
            itemView.setSelected(false);
            itemView.setBackgroundColor(DESELECTED_VALUE);
        }
    }
}
