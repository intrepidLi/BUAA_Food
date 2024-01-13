package com.buaa.food.ui.activity.admin;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.AppFragment;
import com.buaa.food.ui.fragment.DishesFragment;
import com.hjq.base.FragmentPagerAdapter;

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