package com.buaa.food.ui.activity;

import android.content.Intent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.AppFragment;
import com.buaa.food.ui.fragment.DishesFragment;
import com.hjq.base.FragmentPagerAdapter;

public class HistoryActivity extends AppActivity {
    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;
    private DataBaseHelper dataBaseHelper;
    protected int getLayoutId() {
        return R.layout.history_record;
    }

    protected void initView() {
        mViewPager = findViewById(R.id.vp_history_pager);
        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(DishesFragment.newInstance(DishesFragment.StatusType.History));
        mViewPager.setAdapter(mPagerAdapter);
        this.dataBaseHelper = new DataBaseHelper(this);
    }


    protected void initData() {}

    @Override
    public void onRightClick(View view) {
        dataBaseHelper.clearHistoryDishes();
        reload();
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
