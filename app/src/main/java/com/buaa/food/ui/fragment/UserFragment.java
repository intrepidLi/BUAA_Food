package com.buaa.food.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.drawable.Drawable;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.activity.CollectionActivity;
import com.buaa.food.ui.activity.HistoryActivity;
import com.buaa.food.ui.activity.HomeActivity;
import com.buaa.food.ui.activity.SettingActivity;
import com.buaa.food.ui.activity.UserCenterActivity;
import com.buaa.food.UserAuth;


public final class UserFragment extends TitleBarFragment<HomeActivity> {

    // user name
    private TextView mNameView;

    // user avatar
    private ImageView mAvatarView;

    // collection, history, setting
    private LinearLayout mCollectionLayout;
    private LinearLayout mHistoryLayout;
    private LinearLayout mSettingLayout;
    private LinearLayout mUserCenterView;

    private DataBaseHelper dataBaseHelper;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_fragment;
    }

    @Override
    protected void initView() {
        mAvatarView = findViewById(R.id.iv_person_data_avatar);
        mNameView = findViewById(R.id.sb_person_data_name);

        mUserCenterView = findViewById(R.id.btn_user_center);

        mCollectionLayout = findViewById(R.id.user_collection);
        mHistoryLayout = findViewById(R.id.user_history);
        mSettingLayout = findViewById(R.id.user_setting);

        setOnClickListener(mUserCenterView, mCollectionLayout, mHistoryLayout, mSettingLayout);

        dataBaseHelper = new DataBaseHelper(this.getContext());
    }

    @Override
    protected void initData() {
        // TODO: get user name and avatar from server
        String username = dataBaseHelper.getUsername(UserAuth.getLocalUserPhone());
        // String username = "Username";
        @SuppressLint("UseCompatLoadingForDrawables") Drawable avatarImage = getResources().getDrawable(R.drawable.test_user_img);

        mNameView.setText(username);
        mAvatarView.setImageDrawable(avatarImage);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == mUserCenterView.getId()) {
            startActivity(UserCenterActivity.class);
        } else if (viewId == mCollectionLayout.getId()) {
            startActivity(CollectionActivity.class);
        } else if (viewId == mHistoryLayout.getId()) {
             startActivity(HistoryActivity.class);
        } else if (viewId == mSettingLayout.getId()) {
            startActivity(SettingActivity.class);
        }

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}