package com.ArCoreDemo.mrganic.recycler;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ArCoreDemo.mrganic.R;


public class ItemHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "ItemHolder";

    private static final int SELECTED_VALUE = Color.parseColor("#ffb26c");
    private static final int DESELECTED_VALUE = Color.WHITE;

    private ItemAdapter adapter;
    private Item item;
    private View ivThumbnail;

    public ItemHolder(@NonNull View itemView, ItemAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
    }

    public void setItem(Item item) {
        this.item = item;
        ivThumbnail.setOnClickListener(this::onClick);
        if (item.equals(adapter.getSelected())) {
            ivThumbnail.setSelected(true);
            ivThumbnail.setBackgroundColor(SELECTED_VALUE);
        } else {
            ivThumbnail.setSelected(false);
            ivThumbnail.setBackgroundColor(DESELECTED_VALUE);
        }
    }

    private void onClick(View view) {
        Item selected = adapter.getSelected();
        if (!item.equals(selected)) {
            if (selected != null) {
                selected.getHolder().ivThumbnail.setBackgroundColor(DESELECTED_VALUE);
            }
            adapter.setSelected(item);
            ivThumbnail.setSelected(true);
            ivThumbnail.setBackgroundColor(SELECTED_VALUE);
        }
    }
}
