package com.buaa.food.ui.fragment;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.buaa.food.R;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppFragment;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.http.api.UpdateImageApi;
import com.buaa.food.http.glide.GlideApp;
import com.buaa.food.http.model.HttpData;
import com.buaa.food.ui.activity.HomeActivity;
import com.buaa.food.ui.activity.ImageCropActivity;
import com.buaa.food.ui.activity.ImagePreviewActivity;
import com.buaa.food.ui.activity.ImageSelectActivity;
import com.buaa.food.ui.activity.PersonalDataActivity;
import com.buaa.food.ui.activity.SettingActivity;
import com.buaa.food.ui.activity.UserCenterActivity;
import com.buaa.food.ui.adapter.TabAdapter;
import com.buaa.food.ui.dialog.AddressDialog;
import com.buaa.food.ui.dialog.InputDialog;
import com.buaa.food.widget.XCollapsingToolbarLayout;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.model.FileContentResolver;
import com.hjq.widget.layout.SettingBar;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


public final class UserFragment extends TitleBarFragment<HomeActivity> {

    // user name
    private TextView mNameView;

    // user avatar
    private ImageView mAvatarView;
    private Uri mAvatarUrl;

    // collection, history, setting
    private LinearLayout mCollectionLayout;
    private LinearLayout mHistoryLayout;
    private LinearLayout mSettingLayout;

    private LinearLayout mUserCenterView;

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
    }

    @Override
    protected void initData() {
//        GlideApp.with(getActivity())
//                .load(R.drawable.test_user_img)
//                .placeholder(R.drawable.test_user_img)
//                .error(R.drawable.test_user_img)
//                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
//                .into(mAvatarView);

        // TODO: get user name and avatar from server
        String userName = "UserName";
        @SuppressLint("UseCompatLoadingForDrawables") Drawable avatarImage = getResources().getDrawable(R.drawable.test_user_img);

        mNameView.setText(userName);
        mAvatarView.setImageDrawable(avatarImage);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == mUserCenterView.getId()) {
            startActivity(UserCenterActivity.class);
        } else if (viewId == mCollectionLayout.getId()) {
            // TODO:
            // startActivity(CollectionActivity.class);
        } else if (viewId == mHistoryLayout.getId()) {
            // TODO:
            // startActivity(HistoryActivity.class);
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