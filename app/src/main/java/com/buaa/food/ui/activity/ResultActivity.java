package com.buaa.food.ui.activity;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.AppFragment;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.adapter.StatusAdapter;
import com.buaa.food.ui.fragment.StatusFragment;
import com.buaa.food.widget.StatusLayout;
import com.hjq.base.BaseAdapter;
import com.hjq.base.FragmentPagerAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;
import java.util.ArrayList;

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
        mPagerAdapter.addFragment(StatusFragment.newInstance(searchHint));
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void initData() {}
}