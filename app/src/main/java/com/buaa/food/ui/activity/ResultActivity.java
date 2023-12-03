package com.buaa.food.ui.activity;

import androidx.viewpager.widget.ViewPager;

import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.AppFragment;
import com.buaa.food.ui.fragment.DishesFragment;
import com.hjq.base.FragmentPagerAdapter;

public final class ResultActivity extends AppActivity {

    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.result_activity;
    }

    @Override
    protected void initView() {
        String searchHint = getIntent().getStringExtra("searchHint");

        mViewPager = findViewById(R.id.vp_result_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DishesFragment.newInstance(searchHint));
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void initData() {}
}