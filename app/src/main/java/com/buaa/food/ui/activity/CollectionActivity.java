package com.buaa.food.ui.activity;

import androidx.viewpager.widget.ViewPager;

import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.AppFragment;
import com.buaa.food.ui.fragment.DishesFragment;
import com.hjq.base.FragmentPagerAdapter;

public class CollectionActivity extends AppActivity {
    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    protected int getLayoutId() {
        return R.layout.favorite_content;
    }

    protected void initView() {
        mViewPager = findViewById(R.id.vp_favorite_pager);
        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DishesFragment.newInstance(DishesFragment.StatusType.Collection));
        mViewPager.setAdapter(mPagerAdapter);
    }


    protected void initData() {}
}
