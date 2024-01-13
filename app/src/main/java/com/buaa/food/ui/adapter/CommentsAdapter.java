package com.buaa.food.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.buaa.food.CommentPreview;
import com.buaa.food.DishPreview;
import com.buaa.food.R;
import com.buaa.food.app.AppAdapter;

public final class CommentsAdapter extends AppAdapter<CommentPreview> {

    public CommentsAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mCommentText;
        private final TextView mUserText;

        private ViewHolder() {
            super(R.layout.comment_item);
            mCommentText = findViewById(R.id.tv_comment_content);
            mUserText = findViewById(R.id.tv_comment_time);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        @Override
        public void onBindView(int position) {
            mCommentText.setText(getItem(position).getComment());
            mUserText.setText(getItem(position).getUserName() + "  " + getItem(position).getTime());
        }
    }
}