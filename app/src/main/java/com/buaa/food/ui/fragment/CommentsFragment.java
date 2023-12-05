package com.buaa.food.ui.fragment;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.DishPreview;
import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.activity.DishDetailActivity;
import com.buaa.food.ui.activity.admin.DishUploadActivity;
import com.buaa.food.ui.adapter.DishesAdapter;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

// TODO : 修改评论
public final class CommentsFragment extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    public enum StatusType {
        HotRank,
        HangweiXinBei,
        HangweiDongQu,
        SearchResult,
        Collection,
        History,
        Admin,
    }

    private final StatusType type;

    private String searchHint;
    private List<DishPreview> allDishes;
    private List<DishPreview> allFavoriteDishes;
    private List<DishPreview> allHistoryDishes;

    public static CommentsFragment newInstance(StatusType type) {
        Timber.tag("dishesFragment").d("Enter newInstance1");
        return new CommentsFragment(type);
    }

    public static CommentsFragment newInstance(String searchHint) {
        Timber.tag("dishesFragment").d("Enter newInstance3");
        return new CommentsFragment(StatusType.SearchResult, searchHint);
    }

    public CommentsFragment(StatusType type) {
        this.type = type;
    }

//    public DishesFragment(StatusType type, String canteenTitle) {
//        this.type = type;
//    }

    public CommentsFragment(StatusType type, String searchHint) {
        Timber.tag("dishesFragment").d("Construct DishesFragment");
        this.type = type;
        this.searchHint = searchHint;
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private DishesAdapter mAdapter;

    // 添加数据库连接
    private DataBaseHelper dataBaseHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.status_fragment_comment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_status_refresh_comment);
        mRecyclerView = findViewById(R.id.rv_status_list_comment);

        mAdapter = new DishesAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
        this.dataBaseHelper = new DataBaseHelper(this.getContext());
        // canteenTitle = "新北食堂";
        allDishes = dataBaseHelper.fetchAllDishes();
    }

    @Override
    protected void initData() {
        Timber.tag("dishesFragment").d("Enter initData");
        if (type == StatusType.HangweiXinBei) {
            allDishes = dataBaseHelper.fetchCanteenDishes("新北食堂");
        } else if (type == StatusType.HangweiDongQu) {
            allDishes = dataBaseHelper.fetchCanteenDishes("东区食堂");
        }

        if (type == StatusType.Collection) {
            allFavoriteDishes = dataBaseHelper.fetchFavorites();
        }

        if (type == StatusType.History) {
            allHistoryDishes = dataBaseHelper.fetchHistorys();
        }

        mAdapter.setData(analogData());
    }

    private void hangWeiProcess(List<DishPreview> data, StatusType type) {
        if (type == StatusType.HangweiXinBei) {
            allDishes = dataBaseHelper.fetchCanteenDishes("新北食堂");
        } else if (type == StatusType.HangweiDongQu) {
            allDishes = dataBaseHelper.fetchCanteenDishes("东区食堂");
        }
        for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
            DishPreview dishPreview = allDishes.get(i);
            data.add(dishPreview);
        }
    }

    private void collectionProcess(List<DishPreview> data) {
        allFavoriteDishes = dataBaseHelper.fetchFavorites();
        for (int i = mAdapter.getCount(); i < allFavoriteDishes.size(); i++) {
            DishPreview dishPreview = allFavoriteDishes.get(i);
            // dishPreview.setDishPrice("￥" + dishPreview.getDishPrice());
            data.add(dishPreview);
        }
    }

    private void historyProcess(List<DishPreview> data) {
        allHistoryDishes = dataBaseHelper.fetchHistorys();
        for (int i = mAdapter.getCount(); i < allHistoryDishes.size(); i++) {
            DishPreview dishPreview = allHistoryDishes.get(i);
            // dishPreview.setDishPrice("￥" + dishPreview.getDishPrice());
            data.add(dishPreview);
        }
    }

    private List<DishPreview> analogData() {
        List<DishPreview> data = new ArrayList<>();
        switch (type) {
            case HotRank:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add(new DishPreview("热度第" + i, "￥" + i));
                }
                break;
            case HangweiXinBei:
            case HangweiDongQu:
                hangWeiProcess(data, type);
                break;
            case SearchResult:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    data.add(new DishPreview(searchHint + i, "￥" + i));
                }
                break;
            case Collection:
                collectionProcess(data);
                break;
            case History:
                historyProcess(data);
                break;
            case Admin:
                for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
                    DishPreview dishPreview = allDishes.get(i);
                    data.add(dishPreview);
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
        assert mAdapter.getData() != null;
        int dishId = mAdapter.getData().get(position).getDishId();
        Intent intent;
        if (type == StatusType.Admin) {
            intent = new Intent(getAttachActivity(), DishUploadActivity.class);
        } else {
            intent = new Intent(getAttachActivity(), DishDetailActivity.class);
        }
        intent.putExtra("dishId", dishId);
        startActivity(intent);
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