package com.buaa.food.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.buaa.food.R;
import com.buaa.food.app.AppAdapter;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/22
 *    desc   : 状态数据列表
 */
public final class StatusAdapter extends AppAdapter<String> {

    public StatusAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mTextView;

        private ViewHolder() {
            super(R.layout.hangwei_dish);
            mTextView = findViewById(R.id.tv_hangwei_dishName);
        }

        @Override
        public void onBindView(int position) {
            mTextView.setText(getItem(position));
        }
    }
}