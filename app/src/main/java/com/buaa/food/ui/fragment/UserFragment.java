package com.buaa.food.ui.fragment;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    private ViewGroup mAvatarLayout;
    private ImageView mAvatarView;
    private SettingBar mIdView;
    private SettingBar mNameView;

    /** 头像地址 */
    private Uri mAvatarUrl;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_fragment;
    }

    @Override
    protected void initView() {
        mAvatarLayout = findViewById(R.id.fl_person_data_avatar);
        mAvatarView = findViewById(R.id.iv_person_data_avatar);
        mIdView = findViewById(R.id.sb_person_data_id);
        mNameView = findViewById(R.id.sb_person_data_name);
    }

    @Override
    protected void initData() {
        GlideApp.with(getActivity())
                .load(R.drawable.avatar_placeholder_ic)
                .placeholder(R.drawable.avatar_placeholder_ic)
                .error(R.drawable.avatar_placeholder_ic)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mAvatarView);

        mIdView.setRightText("UserID");
        mNameView.setRightText("UserName");
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}