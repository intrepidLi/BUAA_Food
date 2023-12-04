package com.buaa.food.ui.fragment;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buaa.food.ui.activity.DishDetailsActivity;
import com.buaa.food.ui.activity.admin.DishUploadActivity;
import com.hjq.base.BaseAdapter;
import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.adapter.DishesAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.buaa.food.DishPreview;

import java.util.ArrayList;
import java.util.List;


public final class DishesFragment extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    public enum StatusType {
        HotRank,
        Hangwei,
        SearchResult,
        Collection,
        History,
        Admin,
    }

    private final StatusType type;

    private String searchHint;

    public static DishesFragment newInstance(StatusType type) {
        return new DishesFragment(type);
    }

    public static DishesFragment newInstance(String searchHint) {
        return new DishesFragment(StatusType.SearchResult, searchHint);
    }

    public DishesFragment(StatusType type) {
        this.type = type;
    }

    public DishesFragment(StatusType type, String searchHint) {
        this.type = type;
        this.searchHint = searchHint;
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private DishesAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.status_fragment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_status_refresh);
        mRecyclerView = findViewById(R.id.rv_status_list);

        mAdapter = new DishesAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        mAdapter.setData(analogData());
    }

    private List<DishPreview> analogData() {
        List<DishPreview> data = new ArrayList<>();
        switch (type) {
            case HotRank:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add(new DishPreview("热度第" + i, "￥" + i));
                }
                break;
            case Hangwei:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add(new DishPreview("第" + i + "道航味", "￥" + i));
                }
                break;
            case SearchResult:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add(new DishPreview(searchHint + i, "￥" + i));
                }
                break;
            case Collection:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add(new DishPreview("收藏" + i, "￥" + i));
                }
                break;
            case History:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add(new DishPreview("历史记录" + i, "￥" + i));
                }
                break;
            case Admin:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add(new DishPreview("管理" + i, "￥" + i));
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
        if (type == StatusType.Admin) {
            assert mAdapter.getData() != null;
            // int dishId = mAdapter.getData().get(position).getId();
            int dishId = 13;
            Intent intent = new Intent(getAttachActivity(), DishUploadActivity.class);
            intent.putExtra("dishId", dishId);
            startActivity(intent);
        } else {
            startActivity(DishDetailsActivity.class);
            toast(mAdapter.getItem(position));
        }
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