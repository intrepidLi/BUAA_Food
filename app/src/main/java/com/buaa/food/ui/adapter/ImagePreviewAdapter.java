package com.buaa.food.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.buaa.food.http.glide.GlideApp;
import com.github.chrisbanes.photoview.PhotoView;
import com.buaa.food.R;
import com.buaa.food.app.AppAdapter;


public final class ImagePreviewAdapter extends AppAdapter<String> {

    public ImagePreviewAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final PhotoView mPhotoView;

        private ViewHolder() {
            super(R.layout.image_preview_item);
            mPhotoView = (PhotoView) getItemView();
        }

        @Override
        public void onBindView(int position) {
            GlideApp.with(getContext())
                    .load(getItem(position))
                    .into(mPhotoView);
        }
    }
}