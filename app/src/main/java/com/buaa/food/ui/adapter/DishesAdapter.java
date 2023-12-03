package com.buaa.food.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.buaa.food.DishPreview;
import com.buaa.food.R;
import com.buaa.food.app.AppAdapter;

public final class DishesAdapter extends AppAdapter<DishPreview> {

    public DishesAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mNameView;
        private final TextView mPriceView;
        private final AppCompatImageView mImageView;

        private ViewHolder() {
            super(R.layout.hangwei_dish);
            mNameView = findViewById(R.id.tv_hangwei_dishName);
            mImageView = findViewById(R.id.iv_hangwei_dishImage);
            mPriceView = findViewById(R.id.tv_hangwei_dishPrice);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        @Override
        public void onBindView(int position) {
            mNameView.setText(getItem(position).getDishName());
            mImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.test_user_img));
            mPriceView.setText(getItem(position).getDishPrice());
        }
    }
}