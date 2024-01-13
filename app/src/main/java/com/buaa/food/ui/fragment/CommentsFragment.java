package com.buaa.food.ui.fragment;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buaa.food.CommentPreview;
import com.buaa.food.DataBaseHelper;
import com.buaa.food.DishPreview;
import com.buaa.food.R;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.TitleBarFragment;
import com.buaa.food.ui.activity.CommentActivity;
import com.buaa.food.ui.activity.DishDetailActivity;
import com.buaa.food.ui.activity.admin.DishUploadActivity;
import com.buaa.food.ui.adapter.CommentsAdapter;
import com.buaa.food.ui.adapter.DishesAdapter;
import com.hjq.base.BaseAdapter;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public final class CommentsFragment extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    public enum StatusType {
        CommentOne,
        CommentTwo
    }

    private final int id;
    private final StatusType type;

    private List<CommentPreview> allComments;

    public static CommentsFragment newInstance(int id, StatusType type) {
        return new CommentsFragment(id, type);
    }

    public CommentsFragment(int id, StatusType type) {
        this.id = id;
        this.type = type;
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private CommentsAdapter mAdapter;

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

        mAdapter = new CommentsAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshLoadMoreListener(this);

        this.dataBaseHelper = new DataBaseHelper(this.getContext());
    }

    @Override
    protected void initData() {
        mAdapter.setData(analogData());
    }

    private List<CommentPreview> analogData() {
        List<CommentPreview> data = new ArrayList<>();
        if (type == StatusType.CommentOne) {
            data = dataBaseHelper.fetchComments(id);
        } else if (type == StatusType.CommentTwo) {
            data = dataBaseHelper.fetchSecondComments(id);
        }
        return data;
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView RecyclerView对象
     * @param itemView     被点击的条目对象
     * @param position     被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        assert mAdapter.getData() != null;
        int commentId = mAdapter.getData().get(position).getCommentId();
        Intent intent;
        Timber.tag("onItemClick").d("commentId: %d", commentId );
        if (type == StatusType.CommentOne) {
            intent = new Intent(getAttachActivity(), CommentActivity.class);
            intent.putExtra("commentId", commentId);
            intent.putExtra("dishId", 0);
            Timber.tag("onItemClickOne").d("commentId: %d", commentId );
            try {
                finish();
                startActivity(intent);
            } catch (Exception e) {
                Timber.tag("onItemClickOne").e("error%s", e.toString());
                e.printStackTrace();
            }
            // finish();
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