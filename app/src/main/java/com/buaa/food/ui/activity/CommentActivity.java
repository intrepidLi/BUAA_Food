package com.buaa.food.ui.activity;

import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.buaa.food.DataBaseHelper;
import com.buaa.food.R;
import com.buaa.food.aop.SingleClick;
import com.buaa.food.app.AppActivity;
import com.buaa.food.app.AppFragment;
import com.buaa.food.ui.fragment.CommentsFragment;
import com.buaa.food.ui.fragment.DishesFragment;
import com.hjq.bar.TitleBar;
import com.hjq.base.FragmentPagerAdapter;

public class CommentActivity extends AppActivity {
    private int dishId;
    private int commentId;
    private TitleBar mTitleBar;
    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;
    private DataBaseHelper dataBaseHelper;
    private AppCompatButton mCommentButton;

    protected int getLayoutId() {
        return R.layout.comment_activity;
    }

    protected void initView() {
        dishId = getIntent().getIntExtra("dishId", 0);
        commentId = getIntent().getIntExtra("commentId", 0);

        mTitleBar = findViewById(R.id.comment);
        mViewPager = findViewById(R.id.vp_comment_pager);
        mPagerAdapter = new FragmentPagerAdapter<>(this);

        if (dishId != 0 && commentId == 0) {
            mTitleBar.setTitle("一级评论");
            mPagerAdapter.addFragment(CommentsFragment.newInstance(dishId, CommentsFragment.StatusType.CommentOne));
        } else if (dishId == 0 && commentId != 0) {
            mTitleBar.setTitle("二级评论");
            mPagerAdapter.addFragment(CommentsFragment.newInstance(commentId, CommentsFragment.StatusType.CommentTwo));
        }

        mViewPager.setAdapter(mPagerAdapter);

        mCommentButton = findViewById(R.id.btn_comment);

        setOnClickListener(mCommentButton);

        this.dataBaseHelper = new DataBaseHelper(this);
    }


    protected void initData() {
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCommentButton) {

        }
    }
}
