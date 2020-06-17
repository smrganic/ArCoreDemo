package com.ArCoreDemo.mrganic.recycler;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class itemHolder extends RecyclerView.ViewHolder {

    private static final int SELECTED_VALUE = Color.BLACK;
    private static final int DESELECTED_VALUE = Color.WHITE;

    private itemAdapter adapter;
    private item item;

    public itemHolder(@NonNull View itemView, itemAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
    }

    public void setItem(item item){
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
        item selected = adapter.getSelected();
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
