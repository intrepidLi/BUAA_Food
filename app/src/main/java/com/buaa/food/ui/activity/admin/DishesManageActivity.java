package com.buaa.food.ui.activity.admin;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.buaa.food.R;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.AppFragment;
import com.buaa.food.manager.DialogManager;
import com.buaa.food.ui.dialog.AddressDialog;
import com.buaa.food.ui.dialog.DateDialog;
import com.buaa.food.ui.dialog.InputDialog;
import com.buaa.food.ui.dialog.MenuDialog;
import com.buaa.food.ui.dialog.MessageDialog;
import com.buaa.food.ui.dialog.PayPasswordDialog;
import com.buaa.food.ui.dialog.SelectDialog;
import com.buaa.food.ui.dialog.ShareDialog;
import com.buaa.food.ui.dialog.TimeDialog;
import com.buaa.food.ui.dialog.TipsDialog;
import com.buaa.food.ui.dialog.UpdateDialog;
import com.buaa.food.ui.dialog.WaitDialog;
import com.buaa.food.ui.fragment.DishesFragment;
import com.buaa.food.ui.popup.ListPopup;
import com.hjq.base.BaseDialog;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.umeng.Platform;
import com.hjq.umeng.UmengClient;
import com.hjq.umeng.UmengShare;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public final class DishesManageActivity extends AppActivity {

    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.dishes_manage_activity;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_manage_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DishesFragment.newInstance(DishesFragment.StatusType.Admin));
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void initData() {}

    @Override
    public void onRightClick(View view) {
        startActivity(DishUploadActivity.class);
    }
}