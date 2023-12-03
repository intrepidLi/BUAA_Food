package com.buaa.food.ui.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.adapter.StatusAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/07/10
 *    desc   : 加载案例 Fragment
 */
public final class StatusFragment extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    private enum Type {
        HotRank,
        Hangwei,
        SearchResult,
        Collection,
    }

    private Type type;

    private String searchHint;

    public static StatusFragment newInstance() {
        return new StatusFragment(Type.Hangwei);
    }

    public static StatusFragment newInstance(String searchHint) {
        return new StatusFragment(Type.SearchResult, searchHint);
    }

    StatusFragment(Type type) {
        this.type = type;
    }

    StatusFragment(Type type, String searchHint) {
        this.type = type;
        this.searchHint = searchHint;
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private StatusAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.status_fragment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_status_refresh);
        mRecyclerView = findViewById(R.id.rv_status_list);

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
        switch (type) {
            case HotRank:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add("热度第" + i + "的菜品");
                }
                break;
            case Hangwei:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add("第" + i + "航味");
                }
                break;
            case SearchResult:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add(searchHint + "的搜索结果" + i);
                }
                break;
            case Collection:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add("第" + i + "道菜品");
                }
                break;
        }
        return data;
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        toast(mAdapter.getItem(position));
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.clearData();
            mAdapter.setData(analogData());
            mRefreshLayout.finishRefresh();
        }, 1000);
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
}