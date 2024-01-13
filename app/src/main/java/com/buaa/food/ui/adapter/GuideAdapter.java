package com.buaa.food.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.buaa.food.R;
import com.buaa.food.app.AppAdapter;


public final class GuideAdapter extends AppAdapter<Integer> {

    public GuideAdapter(Context context) {
        super(context);
        addItem(R.drawable.guide_1_bg);
        addItem(R.drawable.guide_2_bg);
        addItem(R.drawable.guide_3_bg);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final ImageView mImageView;

        private ViewHolder() {
            super(R.layout.guide_item);
            mImageView = (ImageView) getItemView();
        }

        @Override
        public void onBindView(int position) {
            mImageView.setImageResource(getItem(position));
        }
    }
}