package com.buaa.food.ui.activity;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.adapter.StatusAdapter;
import com.hjq.base.BaseAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;
import java.util.ArrayList;

public final class ResultActivity extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private StatusAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.result_activity;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_result_refresh);
        mRecyclerView = findViewById(R.id.rv_result_list);

        mAdapter = new StatusAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        mAdapter.setData(analogData());
    }

    private List<String> analogData() {
        List<String> data = new ArrayList<>();
        // TODO : 获取搜索结果

        for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 10; i++) {
            data.add("搜索结果" + i);
        }
        return data;
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        // TODO : 点击搜索结果, 跳转到餐品详情页
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.addData(analogData());
            mRefreshLayout.finishLoadMore();
            mAdapter.setLastPage(mAdapter.getCount() >= 100);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        }, 1000);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.clearData();
            mAdapter.setData(analogData());
            mRefreshLayout.finishRefresh();
        }, 1000);
    }
}